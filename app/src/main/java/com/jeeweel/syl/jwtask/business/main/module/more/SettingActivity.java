package com.jeeweel.syl.jwtask.business.main.module.more;

import android.os.Bundle;
import android.widget.Toast;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.basic.LoginActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateConfig;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import net.tsz.afinal.FinalDb;

import api.util.Contants;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ragn on 2015/11/30.
 */
public class SettingActivity extends JwActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setTitle("设置");
    }

    @OnClick(R.id.ll_update)
    void updateClick() {
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
                        UmengUpdateAgent.showUpdateDialog(getMy(), updateInfo);
                        break;
                    case UpdateStatus.No: // has no update
                        Toast.makeText(getMy(), "没有更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.NoneWifi: // none wifi
                        Toast.makeText(getMy(), "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.Timeout: // time out
                        Toast.makeText(getMy(), "超时", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        UmengUpdateAgent.forceUpdate(this);

    }

    @OnClick(R.id.ll_about)
    void aboutClick() {
        JwStartActivity(AboutVersionActivity.class);
    }

    @OnClick(R.id.btn_exit_Login)
    void exitLoginClick() {
        //将自动登录设置成否
        SharedPreferencesUtils.save(getMy(), "autologin", false);
        //清空afinalDB
        FinalDb finalDb = JwAppAplication.getInstance().finalDb;
        finalDb.deleteAll(Users.class);
        //清空组织
        SharedPreferencesUtils.remove(getMy(), Contants.org_code);
        SharedPreferencesUtils.remove(getMy(), Contants.org_name);
        JwStartActivity(LoginActivity.class);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
