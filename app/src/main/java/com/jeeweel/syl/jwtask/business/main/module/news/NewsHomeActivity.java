package com.jeeweel.syl.jwtask.business.main.module.news;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

public class NewsHomeActivity extends JwActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_home);
        setTitle(getString(R.string.news));
    }

}
