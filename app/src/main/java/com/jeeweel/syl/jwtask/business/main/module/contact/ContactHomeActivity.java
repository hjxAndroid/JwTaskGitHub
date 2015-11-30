package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.main.module.basic.RegisterActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactHomeActivity extends JwActivity {

    @Bind(R.id.org_name)
    TextView orgName;
    @Bind(R.id.rl_org)
    RelativeLayout rlOrg;
    @Bind(R.id.dept_name)
    TextView deptName;
    @Bind(R.id.rl_dept)
    RelativeLayout rlDept;
    @Bind(R.id.rl_friend)
    RelativeLayout rlFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_home);
        ButterKnife.bind(this);
        setTitle(getString(R.string.contact));
    }

    @OnClick(R.id.rl_friend)
    void friendClick() {
        JwStartActivity(FriendListActivity.class);
    }
}
