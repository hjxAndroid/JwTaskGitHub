package com.jeeweel.syl.jwtask.business.main.module.task;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Publicity;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicyAddNextActivity extends JwActivity {

    @Bind(R.id.tv_num)
    TextView tvNum;
    @Bind(R.id.li_fw)
    LinearLayout liFw;
    private Publicity publicity;
    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("发公告");
        setContentView(R.layout.activity_publicy_add_next);
        ButterKnife.bind(this);
        initRight();
        users = JwAppAplication.getInstance().getUsers();
        publicity = (Publicity) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("发布");
        menuTextView.setTextColor(getResources().getColor(R.color.white));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });
        addMenuView(menuTextView);
    }

    @OnClick(R.id.li_fw)
    void loginClick() {
        JwStartActivity(PublicyContactHomeActivity.class);
    }
}
