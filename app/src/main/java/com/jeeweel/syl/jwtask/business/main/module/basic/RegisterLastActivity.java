package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.jeeweel.syl.jcloudlib.db.api.CloudClient;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;

import java.util.List;

import api.util.Utils;
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

    private String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_last);
        ButterKnife.bind(this);
        setTitle("密码设置");
        phone = getIntent().getStringExtra(StaticStrUtils.baseItem);
    }

    @OnClick(R.id.bt_login)
    void nextClick() {
        String pwd = etPwd.getText().toString();
        String confirmPwd = etConfirm.getText().toString();

        if(StrUtils.IsNotEmpty(pwd)&&StrUtils.IsNotEmpty(confirmPwd)){
            if(pwd.equals(confirmPwd)){
                showLoading();
                new FinishRefresh(getMy()).execute(pwd,phone);
            }else{
                ToastShow("两次密码不一样");
            }
        }else{
            ToastShow("请完善密码信息");
        }

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

            String pwd = params[0].toString();
            String phone = params[1].toString();
            Users usersItem = new Users();
            usersItem.setPassword(pwd);
            usersItem.setUsername(phone);
            usersItem.setUser_code(Utils.getUUid());

            JCloudDB jCloudDB = new JCloudDB();

            List<Users> list = null;
            try {
                list = jCloudDB.findAllByWhere(Users.class,
                        "username=" + StrUtils.QuotedStr(usersItem.getUsername()));
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            if (ListUtils.IsNull(list)) {
                try {
                    if(jCloudDB.save(usersItem)){

                        result = "1";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
              if(result.equals("1")){
                  SharedPreferencesUtils.save(context, "autologin", true);
                  JwStartActivity(TabHostActivity.class);
              }else{
                  ToastShow("用户保存出错");
              }
            hideLoading();
        }
    }


}
