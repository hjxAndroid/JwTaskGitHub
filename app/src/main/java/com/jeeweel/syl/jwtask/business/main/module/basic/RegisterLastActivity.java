package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterLastActivity extends JwActivity {

    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.et_confirm)
    EditText etConfirm;
    @Bind(R.id.bt_login)
    Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_last);
        ButterKnife.bind(this);
        setTitle("密码设置");
    }

    @OnClick(R.id.bt_login)
    void nextClick() {
        JwStartActivity(TabHostActivity.class);
    }
}
