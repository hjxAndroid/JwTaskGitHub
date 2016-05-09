package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.toast.JwToast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

public class ForgetActivity extends JwActivity {

    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_findpwd)
    EditText etFindpwd;
    @Bind(R.id.bt_next)
    Button btNext;

    String phone = null;
    List<Users> listUsers = null;
    @Bind(R.id.li_pwd)
    LinearLayout liPwd;
    @Bind(R.id.et_yzm)
    EditText etYzm;
    @Bind(R.id.bt_yzm)
    Button btYzm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        ButterKnife.bind(this);
        setTitle("忘记密码");
    }

    @OnClick(R.id.bt_next)
    void forgetClick() {
        phone = etPhone.getText().toString();
        String yzm = etYzm.getText().toString();
        if (StrUtils.IsNotEmpty(phone)&&StrUtils.IsNotEmpty(yzm)) {
            verify(phone,yzm);
        } else {
            JwToast.ToastShow("请输入电话号码");
        }
    }

    @OnClick(R.id.bt_yzm)
    void yamClick() {
        phone = etPhone.getText().toString();
        if (StrUtils.IsNotEmpty(phone)) {
            requestSms(phone);
        } else {
            JwToast.ToastShow("请输入电话号码");
        }
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
                    new FinishRefresh(ForgetActivity.this).execute();
                }else{
                    ToastShow("短信验证失败");
                }
            }

        });

    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;

        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "0";

            JCloudDB jCloudDB = new JCloudDB();
            try {
                String sSql = "SELECT * FROM users where username=" + phone + "";
                listUsers = jCloudDB.findAllBySql(Users.class, sSql);
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            if (ListUtils.IsNotNull(listUsers)) {
                result = "1";
            } else {
                result = "0";
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {

                if (ListUtils.IsNotNull(listUsers)) {
                    Users users = listUsers.get(0);
                    liPwd.setVisibility(View.VISIBLE);
                    etFindpwd.setText(users.getPassword());
                }
            } else {
                ToastShow("请输入正确的的电话号码");
            }
            hideLoading();
        }
    }

}
