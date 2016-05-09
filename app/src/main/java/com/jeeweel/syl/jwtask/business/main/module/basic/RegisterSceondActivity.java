package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.toast.JwToast;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

public class RegisterSceondActivity extends JwActivity {

    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.et_yzm)
    EditText etYzm;
    @Bind(R.id.bt_next)
    Button btNext;
    private Context mContext;

    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sceond);
        ButterKnife.bind(this);
        setTitle("设置验证码");
        mContext = this;
       // InitSMSSDK();
        sendSMSS();
    }

    private void sendSMSS() {
        phone = getIntent().getStringExtra(StaticStrUtils.baseItem);
        if (StrUtils.IsNotEmpty(phone)) {
            tvPhone.setText(phone);
            requestSms(phone);
        }else{
            JwToast.ToastShow("请输入手机号码");
        }
        //SMSSDK.getVerificationCode("86", phone);
    }

    @OnClick(R.id.bt_next)
    void nextClick() {
        String yzm = etYzm.getText().toString();
        if (StrUtils.IsNotEmpty(yzm) && StrUtils.IsNotEmpty(phone)) {
            verify(phone,yzm);
        } else {
            ToastShow("请输入验证码");
        }

    }


    @OnClick(R.id.tv_rigster)
    void rigsterClick() {

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

    //Bomb短信验证
    /** 自定义发送短信内容
     * @method requestSmsCode
     * @return void
     * @exception
     */
    private void requestSms(String phone){
        BmobSMS.requestSMSCode(getMy(), phone, "模板名称", new RequestSMSCodeListener() {

            @Override
            public void done(Integer smsId, BmobException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {//验证码发送成功
                    ToastShow("验证码发送成功");//用于查询本次短信发送详情
                }
            }
        });
    }

    private void verify(final String phone,String yzm){
        BmobSMS.verifySmsCode(getMy(),phone, yzm, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                if(ex==null){//短信验证码已验证成功
                    //注册用户
                    JwStartActivity(RegisterLastActivity.class, phone);
                }else{
                    ToastShow("短信验证失败");
                }
            }

        });

    }
}
