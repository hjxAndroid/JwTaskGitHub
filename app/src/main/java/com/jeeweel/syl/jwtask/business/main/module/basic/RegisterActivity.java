package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import android.os.Handler.Callback;

public class RegisterActivity extends JwActivity {

    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.bt_next)
    Button btNext;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setTitle("注册账号");
    }

    @OnClick(R.id.bt_next)
    void nextClick() {
        String phone = etPhone.getText().toString();
        if (StrUtils.IsNotEmpty(phone)) {
            //JwStartActivity(RegisterSceondActivity.class, phone);
            JwStartActivity(RegisterLastActivity.class, phone);
            finish();
        } else {
            ToastShow("请输入手机号码");
        }

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
