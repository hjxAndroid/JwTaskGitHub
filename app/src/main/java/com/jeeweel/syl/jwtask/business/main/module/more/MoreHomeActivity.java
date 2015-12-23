package com.jeeweel.syl.jwtask.business.main.module.more;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.basic.GetUserPicture;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoreHomeActivity extends JwActivity {
    @Bind(R.id.tv_user_head)
    TextView tvUserHead;
    @Bind(R.id.iv_user_head2)
    ImageView iv_user_head2;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;

    String userNick;
    String user_code;
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideBack(true);
        setContentView(R.layout.activity_more_home);
        ButterKnife.bind(this);
        setTitle(getString(R.string.more));

        users = JwAppAplication.getInstance().users;
        user_code = users.getUser_code();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userNick = users.getNickname();
        if (StrUtils.IsNotEmpty(userNick)) {
            tvUserName.setText(userNick);
            if (!StrUtils.IsNotEmpty(users.getPhoto_code())) {
                if (userNick.length() > 2) {
                    userNick = userNick.substring(userNick.length() - 2, userNick.length());
                    tvUserHead.setText(userNick);
                } else {
                    tvUserHead.setText(userNick);
                }
            }
        }


        new GetUserPicture(getMy(), iv_user_head2, user_code).execute();
        MobclickAgent.onResume(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_home, menu);
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

    @OnClick(R.id.lineUsers)
    void mineClick() {
        JwStartActivity(MineActivity.class);
    }

    @OnClick(R.id.lineAdervise)
    void aderviseClick() {
        JwStartActivity(SettingActivity.class);
    }

    @OnClick(R.id.lineHelp)
    void helpClick() {
        JwStartActivity(FeedbackActivity.class);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
