package com.jeeweel.syl.jwtask.business.main;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jeeweel.syl.jcloudlib.db.api.CloudClient;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.lib.api.core.base.JeeweelApplication;
import com.jeeweel.syl.lib.api.core.base.PublicColors;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import net.tsz.afinal.FinalDb;

import java.util.List;

import api.util.Contants;
import cn.smssdk.SMSSDK;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by asusa on 2015/11/5.
 */
public class JwAppAplication extends JeeweelApplication {
    //创建一个static MyApplication对象，便于全局访问
    private static JwAppAplication mInstance;
    public static FinalDb finalDb = null;

    //捷微
    private  JCloudDB jCloudDB = null;
    // 填写从短信SDK应用后台注册得到的APPKEY
    private  String APPKEY = "cbcb5ea9d879";

    // 填写从短信SDK应用后台注册得到的APPSECRET
    private  String APPSECRET = "4999b5a40e56d32fc49bd569cfb8158d";

    //全局当前登录人信息；
    public static Users users = null;

    //友盟推送
    private static final String TAG = JwAppAplication.class.getName();
    private PushAgent mPushAgent;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //设置整个APP的地址
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/RobotoCondensed-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        PublicColors.setNavicationBarColorRID(R.color.bottom_background);
        PublicColors.setTextColorRID(R.color.black);
        //短信验证
        SMSSDK.initSDK(this, APPKEY, APPSECRET);
        finalDb = FinalDb.create(this);

        initUmeng();

        jCloudDB = new JCloudDB();

        try {
            CloudClient.init(getApplicationContext(), Contants.URL, Contants.WEB_DB, Contants.DB_ID, "");
        }catch (Exception e){
            e.printStackTrace();
        }
        List<Users> usersList = JwAppAplication.getInstance().finalDb.findAll(Users.class);
        if (ListUtils.IsNotNull(usersList)) {
            users = usersList.get(0);
        }
    }

    //用于返回一个MyApplication单例
    public static synchronized JwAppAplication getInstance() {
        return mInstance;
    }

    public static FinalDb getFinalDb() {
        return finalDb;
    }



    public JCloudDB getjCloudDB() {
        return jCloudDB;
    }

    public void setjCloudDB(JCloudDB jCloudDB) {
        this.jCloudDB = jCloudDB;
    }

    public String getAPPKEY() {
        return APPKEY;
    }


    public String getAPPSECRET() {
        return APPSECRET;
    }


    public static Users getUsers() {
        if(users==null){
            List<Users> usersList = JwAppAplication.getInstance().finalDb.findAll(Users.class);
            if (ListUtils.IsNotNull(usersList)) {
                users = usersList.get(0);
            }
            return users;
        }else{
            return users;
        }
    }

    public static void setUsers(Users users) {
        JwAppAplication.users = users;
    }

    private void initUmeng(){

        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        mPushAgent.setPushCheck(true);// 默认不检查集成配置文件
        mPushAgent.onAppStart();
        mPushAgent.enable();


        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            /**
             * 参考集成文档的1.6.3
             * http://dev.umeng.com/push/android/integration#1_6_3
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if(isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

            /**
             * 参考集成文档的1.6.4
             * http://dev.umeng.com/push/android/integration#1_6_4
             * */
            @Override
            public Notification getNotification(Context context,
                                                UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                      //  myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView);
                        builder.setContentTitle(msg.title)
                                .setContentText(msg.text)
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);
                        Notification mNotification = builder.build();
                        //由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
                        mNotification.contentView = myNotificationView;
                        return mNotification;
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * 参考集成文档的1.6.2
         * http://dev.umeng.com/push/android/integration#1_6_2
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }
}
