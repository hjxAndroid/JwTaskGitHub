package com.jeeweel.syl.jwtask.business.main.module.more;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

public class FeedbackActivity extends JwActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_feedback);
        setTitle("帮助与反馈");
        initRight();
    }
    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("提交");
        menuTextView.setTextColor(getResources().getColor(R.color.white));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });
        addMenuView(menuTextView);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actvity_feedback, menu);
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
}
