package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.umeng.message.UmengRegistrar;
import com.umeng.update.UmengUpdateAgent;

import net.tsz.afinal.FinalDb;

import java.util.List;

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

    private List<Users> list;
    String device_token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideNavcationBar(true);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        autologin();
        device_token = UmengRegistrar.getRegistrationId(getApplicationContext());
        //友盟自动更新检查
        UmengUpdateAgent.update(this);
        //允许基站更新
        UmengUpdateAgent.setUpdateOnlyWifi(false);
    }

    private void autologin(){
        //boolean autu = false;
        boolean autu = (boolean)SharedPreferencesUtils.get(getMy(),"autologin",false);
        if(autu){
            JwStartActivity(TabHostActivity.class);
            finish();
        }else{
            Users users = JwAppAplication.getInstance().users;
            if(null!=users){
                String phone = users.getUsername();
                etPhone.setText(phone);
            }
        }
    }

    @OnClick(R.id.bt_login)
    void loginClick() {
        String phone = etPhone.getText().toString();
        String pwd = etPwd.getText().toString();

        if(StrUtils.IsNotEmpty(phone)&&StrUtils.IsNotEmpty(pwd)){
            showLoading();
            new FinishRefresh(getMy()).execute(pwd,phone);
        }else{
            ToastShow("请输入用户名密码");
        }

    }

    @OnClick(R.id.tv_rigster)
    void registerClick() {
        JwStartActivity(RegisterActivity.class);
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
            JCloudDB jCloudDB = new JCloudDB();
            try {
                list = jCloudDB.findAllByWhere(Users.class,
                        "username=" + StrUtils.QuotedStr(phone) + "and password=" + StrUtils.QuotedStr(pwd));
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            if(ListUtils.IsNotNull(list)){
                result = "1";
            }else{
                result = "0";
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("1")){
                SharedPreferencesUtils.save(context,"autologin",true);

                if(ListUtils.IsNotNull(list)){
                    Users users = list.get(0);
                    FinalDb finalDb = JwAppAplication.getInstance().finalDb;
                    finalDb.deleteAll(Users.class);
                    finalDb.save(users);
                    JwAppAplication.getInstance().setUsers(users);
                }
                JwStartActivity(TabHostActivity.class);
                finish();
            }else{
                ToastShow("用户名或密码出错");
            }
            hideLoading();
        }
    }
}
