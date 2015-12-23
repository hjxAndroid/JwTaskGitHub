package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import api.util.Contants;
import api.util.OttUtils;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ApplyGiveUpActivity extends JwActivity {

    Task task;
    Users users;
    @Bind(R.id.et_fqly)
    EditText etFqly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_give_up);
        ButterKnife.bind(this);
        setTitle("申请放弃");
        initRight();
        getData();
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLoading();
                String fqly = etFqly.getText().toString();
                if (StrUtils.IsNotEmpty(fqly) && StrUtils.IsNotEmpty(fqly)) {
                    task.setGive_up_content(fqly);
                    new fqTask(getMy()).execute();
                } else {
                    ToastShow("请填写延期时间和理由");
                }
            }
        });
        addMenuView(menuTextView);
    }


    private void getData() {
        users = JwAppAplication.getInstance().getUsers();
        task = (Task) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
    }

    /**
     * 改变任务表状态
     */
    private class fqTask extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public fqTask(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            try {
                if (null != users) {
                    if (null != task) {
                        String sql = "update task set now_state = 7 , now_state_name = '放弃申请中',give_up_content=" + StrUtils.QuotedStr(task.getGive_up_content()) + "  where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and principal_code like " + StrUtils.QuotedStrLike(users.getUser_code());
                        CloudDB.execSQL(sql);

                        //保存到流程表里
                        Taskflow taskflow = new Taskflow();
                        taskflow.setTask_code(task.getTask_code());
                        taskflow.setNow_state(7);
                        taskflow.setNow_state_name(Contants.fqsqz);
                        taskflow.setUser_action(Contants.action_sqfq);
                        jCloudDB.save(taskflow);

                    }
                }
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                OttUtils.push("give_refresh", "");
                ToastShow("操作成功");
                finish();
            } else {
                ToastShow("操作失败");
            }
            hideLoading();
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
