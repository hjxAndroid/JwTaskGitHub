package com.jeeweel.syl.jwtask.business.main.module.task;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

public class JobAddActivity extends JwActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_add);
        setTitle("发起任务");
    }


}
