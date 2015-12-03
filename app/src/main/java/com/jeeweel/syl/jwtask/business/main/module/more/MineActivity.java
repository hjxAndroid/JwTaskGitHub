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
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineActivity extends JwActivity {
    Users users;
    String phone;
    String birthday; // 初始化开始时间
    TextView tv_birthday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);
        setTitle(getString(R.string.mineinformation));

        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        users  = JwAppAplication.getInstance().users;
        birthday = users.getBirthday();
        birthday = convert2String(Long.parseLong(birthday));
        tv_birthday.setText(birthday);
        Logv("qwqwqw%%%" + birthday);
    }
    public static String convert2String(long time) {//把long类型的时间转换为String类型，格式为yyyy-MM-dd的时间
        String format = "yyyy-MM-dd";
        if (time > 0l) {
            SimpleDateFormat sf = new SimpleDateFormat(format);
            Date date = new Date(time);
            return sf.format(date);
        }
        return "";
    }
    @Override
    protected void onResume() {
        super.onResume();
        users  = JwAppAplication.getInstance().users;
        String str;
        TextView tv;
        str = users.getNickname();
        tv = (TextView) findViewById(R.id.nickname);
        tv.setText(str);
        str = users.getSex();
        tv = (TextView) findViewById(R.id.tv_sex);
        tv.setText(str);
        str= users.getStrong_point();
        tv = (TextView) findViewById(R.id.tv_strong_point);
        tv.setText(str);
        str = users.getSign();
        tv = (TextView) findViewById(R.id.tv_sign);
        tv.setText(str);
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
        protected String doInBackground(String... params) {
            String sql="UPDATE users SET birthday ='"+birthday+"'WHERE username ='"+phone+"'";
            try{
                CloudDB.execSQL(sql);
            }catch (CloudServiceException e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    @OnClick(R.id.LinearLayout05)
    void editMyBirthdayClick() {
        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                MineActivity.this, birthday);
        dateTimePicKDialog.dateTimePicKDialog(tv_birthday);
        phone = users.getUsername();
        birthday = tv_birthday.getText().toString();
        users.setBirthday(birthday);

        Logv("qwqwqw---" + birthday + phone);
        new saveBirthday(getMy()).execute();
   //     tv_birthday.setText(birthday);
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
}
