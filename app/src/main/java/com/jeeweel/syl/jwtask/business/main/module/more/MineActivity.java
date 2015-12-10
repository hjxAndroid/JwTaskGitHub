package com.jeeweel.syl.jwtask.business.main.module.more;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.basic.GetUserPicture;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineActivity extends JwActivity {
    Users users;
    String phone;
    String birthday;
    String str;
    String user_code;

    @Bind(R.id.tv_user_head1)
    TextView tv_user_head1;
    @Bind(R.id.iv_user_head2)
    ImageView iv_user_head2;
    @Bind(R.id.tv_nickname)
    TextView tv_nickname;
    @Bind(R.id.tv_mail)
    TextView tv_mail;
    @Bind(R.id.tv_sex)
    TextView tv_sex;
    @Bind(R.id.tv_strong_point)
    TextView tv_strong_point;
    @Bind(R.id.tv_sign)
    TextView tv_sign;
    @Bind(R.id.tv_birthday)
    TextView tv_birthday;
    @Bind(R.id.tv_area)
    TextView tv_area;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);
        setTitle(getString(R.string.mineinformation));

        users  = JwAppAplication.getInstance().users;
        phone = users.getUsername();
        user_code = users.getUser_code();
    }

    @Override
    protected void onResume() {
        super.onResume();
        str = users.getNickname();
        if (StrUtils.IsNotEmpty(str)) {
            tv_nickname.setText(str);
            if (!StrUtils.IsNotEmpty(users.getPhoto_code())) {
                str = str.substring(str.length() - 2, str.length());
                tv_user_head1.setText(str);
            }
        }
        str = users.getEmail();
        if (StrUtils.IsNotEmpty(str)) {
            tv_mail.setText(str);
        }
        str = users.getSex();
        if (StrUtils.IsNotEmpty(str)) {
            tv_sex.setText(str);
        }
        str= users.getStrong_point();
        if (StrUtils.IsNotEmpty(str)) {
            tv_strong_point.setText(str);
        }else {
            tv_strong_point.setText("未设置");
        }
        str = users.getSign();
        if (StrUtils.IsNotEmpty(str)) {
            tv_sign.setText(str);
        }else {
            tv_sign.setText("未设置");
        }
        str = users.getArea();
        if (StrUtils.IsNotEmpty(str)) {
            tv_area.setText(str);
        }
        birthday = users.getBirthday();
        if (StrUtils.IsNotEmpty(birthday)) {
            tv_birthday.setText(birthday);
        }
        new GetUserPicture(getMy(),iv_user_head2,user_code).execute();
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


    @OnClick(R.id.ll_user_head)
    void editPhotoClick() {
        JwStartActivity(MinePhotoActivity.class);
    }

    @OnClick(R.id.ll_nickname)
    void editNameClick() {
        Intent intent=new Intent();
        intent.putExtra("title", "设置昵称");
        intent.setClass(MineActivity.this, MineEditnameActivity.class);
        JwStartActivity(intent);
    }

    @OnClick(R.id.ll_email)
    void editEmailClick() {
        Intent intent=new Intent();
        intent.putExtra("title", "设置邮箱");
        intent.setClass(MineActivity.this, MineEditnameActivity.class);
        JwStartActivity(intent);
    }

    @OnClick(R.id.ll_qrcode)
    void editQRCdoeClick() {
        Intent intent=new Intent();
        intent.putExtra("phone", phone);
        intent.setClass(MineActivity.this, MineQRCodeActivity.class);
        JwStartActivity(intent);
    }

    @OnClick(R.id.ll_sex)
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

    @OnClick(R.id.ll_birthday)
    void editMyBirthdayClick() {
        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                MineActivity.this, birthday);
        dateTimePicKDialog.dateTimePicKDialog(tv_birthday);
    }

    @OnClick(R.id.ll_address)
    void editAddressClick() {
        JwStartActivity(MineAddressActivity.class);
    }

    @OnClick(R.id.ll_strong_point)
    void editSpecialtyClick() {
        Intent intent=new Intent();
        intent.putExtra("title", "特长、兴趣");
        intent.setClass(MineActivity.this, MineEditActivity.class);
        JwStartActivity(intent);
    }

    @OnClick(R.id.ll_sign)
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
