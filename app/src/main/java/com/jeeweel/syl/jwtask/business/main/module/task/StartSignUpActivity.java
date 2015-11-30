package com.jeeweel.syl.jwtask.business.main.module.task;

import android.os.Bundle;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.main.module.more.MineActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ragn on 2015/11/27.
 */
public class StartSignUpActivity extends JwActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_sign_up);
        ButterKnife.bind(this);
        setTitle("发起签到");
    }

    @OnClick(R.id.line_start_sign_user)
    void personlInformation() {
        JwStartActivity(MineActivity.class);
    }

    @OnClick(R.id.start_sign_button)
    void startSign() {
        JwStartActivity(SignUpActivity.class);
    }
}
