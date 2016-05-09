package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
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

public class EditPwdActivity extends JwActivity {

    @Bind(R.id.et_phone)
    EditText etPhone;

    @Bind(R.id.bt_next)
    Button btNext;

    String phone = null;
    String newPwd = null;
    String oldPwd = null;

    List<Users> listUsers = null;
    @Bind(R.id.et_yzm)
    EditText etYzm;
    @Bind(R.id.bt_yzm)
    Button btYzm;
    @Bind(R.id.et_old_pwd)
    EditText etOldPwd;
    @Bind(R.id.et_new_pwd)
    EditText etNewPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pwd);
        ButterKnife.bind(this);
        setTitle("修改密码");
    }

    @OnClick(R.id.bt_next)
    void edittClick() {
        phone = etPhone.getText().toString();
        oldPwd = etOldPwd.getText().toString();
        newPwd = etNewPwd.getText().toString();


            String yzm = etYzm.getText().toString();
            if (StrUtils.IsNotEmpty(phone) && StrUtils.IsNotEmpty(yzm)&&StrUtils.IsNotEmpty(oldPwd)&&StrUtils.IsNotEmpty(newPwd)) {
                verify(phone, yzm);
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

    /**
     * 自定义发送短信内容
     *
     * @return void
     * @throws
     * @method requestSmsCode
     */
    private void requestSms(String phone) {
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

    private void verify(final String phone, String yzm) {
        BmobSMS.verifySmsCode(getMy(), phone, yzm, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {//短信验证码已验证成功
                    new FinishRefresh(EditPwdActivity.this).execute();
                } else {
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

            String result = "1";
            try {
                String sql = " update users set password = "+StrUtils.QuotedStr(newPwd)+" where password = " +StrUtils.QuotedStr(oldPwd) +"and username = "+phone;
                CloudDB.execSQL(sql);
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                ToastShow("修改成功");
                hideLoading();
                finish();
            } else {
                ToastShow("修改失败");
                hideLoading();
            }
        }
    }

}
