package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.os.Bundle;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddHomeActivity extends JwActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home);
        setTitle("添加");
        ButterKnife.bind(this);
    }

    @OnClick(R.id.rl_friend)
    void friendClick() {
        JwStartActivity(FriendAddActivity.class);
    }

    @OnClick(R.id.rl_dept)
    void deptClick() {
        JwStartActivity(AddDeptActivity.class);
    }

    @OnClick(R.id.rl_org)
    void orgClick() {
        JwStartActivity(AddOrgActivity.class);
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
