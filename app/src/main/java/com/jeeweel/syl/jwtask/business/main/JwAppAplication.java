package com.jeeweel.syl.jwtask.business.main;

import com.jeeweel.syl.jcloudlib.db.api.CloudClient;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.lib.api.core.base.JeeweelApplication;
import com.jeeweel.syl.lib.api.core.base.PublicColors;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import net.tsz.afinal.FinalDb;

import java.util.List;

import api.util.Contants;
import cn.smssdk.SMSSDK;

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
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        PublicColors.setNavicationBarColorRID(R.color.ios7blue);
        PublicColors.setTextColorRID(R.color.white);
        //短信验证
        SMSSDK.initSDK(this, APPKEY, APPSECRET);
        finalDb = FinalDb.create(this);

        jCloudDB = new JCloudDB();

        try {
            CloudClient.init(getApplicationContext(), Contants.URL, Contants.WEB_NAME, Contants.DB_ID, "");
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
}
