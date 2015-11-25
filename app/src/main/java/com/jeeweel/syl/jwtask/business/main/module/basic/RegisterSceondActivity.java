package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterSceondActivity extends JwActivity {

    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.et_yzm)
    EditText etYzm;
    @Bind(R.id.bt_next)
    Button btNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sceond);
        ButterKnife.bind(this);
        setTitle("设置验证码");
    }

    @OnClick(R.id.bt_next)
    void nextClick() {
        JwStartActivity(RegisterLastActivity.class);
    }
}
