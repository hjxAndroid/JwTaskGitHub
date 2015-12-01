package com.jeeweel.syl.jwtask.business.main.module.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineActivity extends JwActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);
        setTitle(getString(R.string.mineinformation));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Users users;
        users  = JwAppAplication.getInstance().users;
        String str;
        str = users.getNickname();
        TextView tv;
        tv = (TextView) findViewById(R.id.nickname);
        tv.setText(str);
        str= users.getSign();
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

    @OnClick(R.id.LinearLayout02)
    void editMyAcountClick() {
        JwStartActivity(MineAccountActivity.class);
    }

    @OnClick(R.id.LinearLayout04)
    void editMySexClick() {
        JwStartActivity(MineSexActivity.class);
    }

    private TextView startDateTime;
    private String initStartDateTime = "2013年9月3日"; // 初始化开始时间
    @OnClick(R.id.LinearLayout05)
    void editMyBirthdayClick() {
        startDateTime = (TextView) findViewById(R.id.inputDate);
        startDateTime.setText(initStartDateTime);
        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                MineActivity.this, initStartDateTime);
        dateTimePicKDialog.dateTimePicKDialog(startDateTime);
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
