package com.jeeweel.syl.jwtask.business.main.module.more;

import android.os.Bundle;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import butterknife.ButterKnife;

/**
 * Created by Ragn on 2015/11/30.
 */
public class SettingActivity extends JwActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_setting);
    }
}
