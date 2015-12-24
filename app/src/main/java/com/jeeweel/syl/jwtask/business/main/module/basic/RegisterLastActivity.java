package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.content.Context;
import android.content.Intent;
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
import com.jeeweel.syl.jwtask.business.main.module.more.MineEditnameActivity;
import com.jeeweel.syl.jwtask.business.main.module.more.MineQRCodeActivity;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.UmengRegistrar;

import net.tsz.afinal.FinalDb;

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
    List<Users> list = null;
    private String device_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_last);
        ButterKnife.bind(this);
        setTitle("密码设置");
        device_token = UmengRegistrar.getRegistrationId(getApplicationContext());
        phone = getIntent().getStringExtra(StaticStrUtils.baseItem);
    }

    @OnClick(R.id.bt_login)
    void nextClick() {
        String pwd = etPwd.getText().toString();
        String confirmPwd = etConfirm.getText().toString();

        if (StrUtils.IsNotEmpty(pwd) && StrUtils.IsNotEmpty(confirmPwd)) {
            if (pwd.equals(confirmPwd)) {
                showLoading();
                new FinishRefresh(getMy()).execute(pwd, phone);
            } else {
                ToastShow("两次密码不一样");
            }
        } else {
            ToastShow("请完善密码信息");
        }

    }


    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        Users users = new Users();

        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            String pwd = params[0].toString();
            String phone = params[1].toString();
            users.setPassword(pwd);
            users.setUsername(phone);
            users.setUser_code(Utils.getUUid());
            if (StrUtils.IsNotEmpty(device_token)) {
                users.setDevice_token(device_token);
            }

            JCloudDB jCloudDB = new JCloudDB();

            try {
                list = jCloudDB.findAllByWhere(Users.class,
                        "username=" + StrUtils.QuotedStr(users.getUsername()));
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            if (ListUtils.IsNull(list)) {
                try {
                    jCloudDB.save(users);
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "0";
                }

            } else {
                result = "2";
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {

                FinalDb finalDb = JwAppAplication.getInstance().finalDb;
                finalDb.deleteAll(Users.class);
                finalDb.save(users);
                JwAppAplication.getInstance().setUsers(users);

                SharedPreferencesUtils.save(context, "autologin", true);

                Intent intent = new Intent();
                intent.putExtra("register", true);
                intent.putExtra("title", "设置昵称");
                intent.setClass(RegisterLastActivity.this, MineEditnameActivity.class);
                JwStartActivity(intent);
                finish();
                //          JwStartActivity(TabHostActivity.class);

            } else if (result.equals("2")) {
                ToastShow("用户已存在");
            } else {
                ToastShow("用户保存出错");
            }
            hideLoading();
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
