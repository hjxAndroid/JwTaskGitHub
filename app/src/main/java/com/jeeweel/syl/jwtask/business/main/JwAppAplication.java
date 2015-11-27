package com.jeeweel.syl.jwtask.business.main;

import com.jeeweel.syl.jcloudlib.db.api.CloudClient;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.base.JeeweelApplication;
import com.jeeweel.syl.lib.api.core.base.PublicColors;

import net.tsz.afinal.FinalDb;

import cn.smssdk.SMSSDK;

/**
 * Created by asusa on 2015/11/5.
 */
public class JwAppAplication extends JeeweelApplication {
    //创建一个static MyApplication对象，便于全局访问
    private static JwAppAplication mInstance;
    public static FinalDb finalDb = null;

    // 填写从短信SDK应用后台注册得到的APPKEY
    private static String APPKEY = "cbcb5ea9d879";

    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = "4999b5a40e56d32fc49bd569cfb8158d";
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        PublicColors.setNavicationBarColorRID(R.color.title_gray);
        PublicColors.setTextColorRID(R.color.list_text_color);
        //短信验证
        SMSSDK.initSDK(this, APPKEY, APPSECRET);
        finalDb = FinalDb.create(this);
        try {
            CloudClient.init(getApplicationContext(), "192.168.0.37:8080", "jwtask", "58975c511b1bcaddecc906a2c9337665", "");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //用于返回一个MyApplication单例
    public static synchronized JwAppAplication getInstance() {
        return mInstance;
    }
}
