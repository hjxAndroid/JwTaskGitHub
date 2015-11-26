package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends JwActivity {

    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.bt_next)
    Button btNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setTitle("注册账号");
    }

    @OnClick(R.id.bt_next)
    void nextClick() {
       JwStartActivity(RegisterSceondActivity.class);
    }
}
