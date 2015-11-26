package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends JwActivity {

    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.bt_login)
    Button btLogin;
    @Bind(R.id.tv_rigster)
    TextView tvRigster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideNavcationBar(true);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_login)
    void loginClick() {
        JwStartActivity(TabHostActivity.class);
    }

    @OnClick(R.id.tv_rigster)
    void registerClick() {
        JwStartActivity(RegisterActivity.class);
    }
}
