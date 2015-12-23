package com.jeeweel.syl.jwtask.business.main.module.task;

import android.os.Bundle;
import android.widget.Button;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ragn on 2015/12/17.
 */
public class SignOptionsActivity extends JwActivity {


    @Bind(R.id.btn_start_sign_up)
    Button btnStartSignUp;
    @Bind(R.id.btn_check_my_start_sign)
    Button btnCheckMyStartSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_options);
        setTitle("签到选项");
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_start_sign_up)
    void clickStartSignUp() {
        JwStartActivity(StartSignUpActivity.class);
    }

    @OnClick(R.id.btn_check_my_start_sign)
    void clickCheckMyStartSign() {
        JwStartActivity(MyStartSignUpActivity.class);
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
