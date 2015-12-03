package com.jeeweel.syl.jwtask.business.main.module.more;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineActivity extends JwActivity {
    Users users;
    String phone;
    String birthday; // 初始化开始时间
    String str;

    @Bind(R.id.tv_nickname)
    TextView tv_nickname;
    @Bind(R.id.tv_sex)
    TextView tv_sex;
    @Bind(R.id.tv_strong_point)
    TextView tv_strong_point;
    @Bind(R.id.tv_sign)
    TextView tv_sign;
    @Bind(R.id.tv_birthday)
    TextView tv_birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);
        setTitle(getString(R.string.mineinformation));

        users  = JwAppAplication.getInstance().users;
        phone = users.getUsername();
    }

    @Override
    protected void onResume() {
        super.onResume();
        str = users.getNickname();
        if (StrUtils.IsNotEmpty(str)) {
            tv_nickname.setText(str);
        }
        str = users.getSex();
        if (StrUtils.IsNotEmpty(str)) {
            tv_sex.setText(str);
        }
        str= users.getStrong_point();
        if (StrUtils.IsNotEmpty(str)) {
            tv_strong_point.setText(str);
        }
        str = users.getSign();
        if (StrUtils.IsNotEmpty(str)) {
            tv_sign.setText(str);
        }
        birthday = users.getBirthday();
        if (StrUtils.IsNotEmpty(birthday)) {
            tv_birthday.setText(birthday);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.LinearLayout01)
    void editNameClick() {
        JwStartActivity(MineEditnameActivity.class);
    }

    @OnClick(R.id.LinearLayout04)
    void editMySexClick() {
        JwStartActivity(MineSexActivity.class);
    }

    private class saveBirthday extends AsyncTask<String, Void, String> {
        private Context context;
        public saveBirthday(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "1";
            String sql="UPDATE users SET birthday ='"+birthday+"'WHERE username ='"+phone+"'";
            try{
                CloudDB.execSQL(sql);
            }catch (CloudServiceException e){
                result = "0";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if(result.equals("1")){
                birthday = tv_birthday.getText().toString();
                users.setBirthday(birthday);

                JwAppAplication.getFinalDb().update(users);
            }else{
                ToastShow("数据保存失败");
            }
        }
    }

    @OnClick(R.id.LinearLayout05)
    void editMyBirthdayClick() {
        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                MineActivity.this, birthday);
        dateTimePicKDialog.dateTimePicKDialog(tv_birthday);
    }

    @OnClick(R.id.LinearLayout07)
    void editSpecialtyClick() {
        Intent intent=new Intent();
        intent.putExtra("title", "特长、兴趣");
        intent.setClass(MineActivity.this, MineEditActivity.class);
        JwStartActivity(intent);
    }

    @OnClick(R.id.LinearLayout08)
    void editSignatureClick() {
        Intent intent=new Intent();
        intent.putExtra("title", "个性签名");
        intent.setClass(MineActivity.this, MineEditActivity.class);
        JwStartActivity(intent);
    }

    @Subscribe
    public void dateTimeSelect(ActivityMsgEvent activityMsgEvent) {
        if (activityMsgEvent.getMsg().equals("dateTimePick")) {
            birthday = activityMsgEvent.getJson();
            if (StrUtils.IsNotEmpty(birthday)) {
                tv_birthday.setText(birthday);
                new saveBirthday(getMy()).execute();
            }
        }
    }
}
