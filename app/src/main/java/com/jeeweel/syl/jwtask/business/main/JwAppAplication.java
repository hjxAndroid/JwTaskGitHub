package com.jeeweel.syl.jwtask.business.main;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.base.JeeweelApplication;
import com.jeeweel.syl.lib.api.core.base.PublicColors;

import net.tsz.afinal.FinalDb;

/**
 * Created by asusa on 2015/11/5.
 */
public class JwAppAplication extends JeeweelApplication {
    //创建一个static MyApplication对象，便于全局访问
    private static JwAppAplication mInstance;
    public static FinalDb finalDb = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        PublicColors.setNavicationBarColorRID(R.color.title_gray);
        finalDb = FinalDb.create(this);
    }

    //用于返回一个MyApplication单例
    public static synchronized JwAppAplication getInstance() {
        return mInstance;
    }
}
