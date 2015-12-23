package com.jeeweel.syl.jwtask.business.main.module.more;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Feedback;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.umeng.analytics.MobclickAgent;

import api.util.Utils;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends JwActivity {
    EditText et_content;
    String content;
    Users users;
    String user_code;
    JCloudDB jCloudDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        setTitle("帮助与反馈");

        et_content = (EditText) findViewById(R.id.et_content);
        users = JwAppAplication.getInstance().getUsers();
        user_code = users.getUser_code();
        jCloudDB = new JCloudDB();
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

    private class saveContent extends AsyncTask<String, Void, String> {
        private Context context;

        public saveContent(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "1";
            String code = Utils.getUUid();
            Logv("qwqwqw--code" + code);
            Feedback feedback = new Feedback();
            feedback.setCode(code);
            feedback.setUser_code(user_code);
            feedback.setContent(content);
            try {
                jCloudDB.save(feedback);
                Logv("qwqwqw--feedback");
            } catch (CloudServiceException e) {
                Logv("qwqwqw--CloudServiceException");
                result = "0";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.equals("1")) {
                Logv("qwqwqw--数据上传成功");
                ToastShow("数据上传成功");
            } else {
                Logv("qwqwqw--数据上传成功");
                ToastShow("数据保存失败");
            }
            FeedbackActivity.this.finish();
        }
    }

    @OnClick(R.id.bt_feed_back)
    public void feedbackClick() {
        content = StrUtils.StrIfNull(et_content.getText().toString());
        new saveContent(getMy()).execute();
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
