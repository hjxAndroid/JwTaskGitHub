package com.jeeweel.syl.jwtask.business.main.module.more;

import android.os.Bundle;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.main.module.basic.LoginActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.umeng.update.UmengUpdateAgent;

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
        UmengUpdateAgent.forceUpdate(this);
    }

    @OnClick(R.id.ll_about)
    void aboutClick() {
        JwStartActivity(AboutVersionActivity.class);
    }

    @OnClick(R.id.btn_exit_Login)
    void exitLoginClick() {
        JwStartActivity(LoginActivity.class);
    }
}
