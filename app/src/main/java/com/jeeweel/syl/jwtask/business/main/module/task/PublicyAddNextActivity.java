package com.jeeweel.syl.jwtask.business.main.module.task;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Publicity;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.integer.IntUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import api.util.Contants;
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
        setContentView(R.layout.activity_publicy_add_next);
        setTitle("发公告");
        ButterKnife.bind(this);
        initRight();
        users = JwAppAplication.getInstance().getUsers();
        publicity = (Publicity) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("发布");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
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


    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (StrUtils.IsNotEmpty(msg) && msg.equals("pulbicy_users")) {
            String json = activityMsgEvent.getParam();
            if (StrUtils.IsNotEmpty(json)) {
                List<Userdept> userdepts = JwJSONUtils.getParseArray(json, Userdept.class);
                if (ListUtils.IsNotNull(userdepts)) {
                    int size = userdepts.size();
                    tvNum.setText(IntUtils.toStr(size));
                }
            }

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
