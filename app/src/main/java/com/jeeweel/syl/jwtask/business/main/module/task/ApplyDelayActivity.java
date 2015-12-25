package com.jeeweel.syl.jwtask.business.main.module.task;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Applydelay;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.DateHelper;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;

import api.util.Contants;
import api.util.OttUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplyDelayActivity extends JwActivity {

    @Bind(R.id.tv_rmw)
    TextView tvRmw;
    @Bind(R.id.tv_kssj)
    TextView tvKssj;
    @Bind(R.id.tv_jiesusj)
    TextView tvJiesusj;
    @Bind(R.id.tv_jssj)
    TextView tvJssj;
    @Bind(R.id.tv_sqyqsj)
    TextView tvSqyqsj;
    @Bind(R.id.et_sqly)
    EditText etSqly;

    Task task;
    Users users;
    Applydelay applydelay;
    @Bind(R.id.li_yqs1)
    LinearLayout liYqs1;
    String timeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_delay);
        ButterKnife.bind(this);
        setTitle("申请延期");
        initRight();
        getData();
    }

    /**
     * 创建日期及时间选择对话框
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        if (StrUtils.IsNotEmpty(timeData)) {
            String[] data = timeData.split("-");
            int year = Integer.parseInt(data[0]);
            int mouth = Integer.parseInt(data[1])-1;
            int day = Integer.parseInt(data[2]);
            dialog = new android.app.DatePickerDialog(
                    this,
                    new android.app.DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                            tvSqyqsj.setText("" + year + "-" + (month + 1) + "-" + dayOfMonth);
                        }
                    },
                    year, // 传入年份
                    mouth, // 传入月份
                    day // 传入天数
            );
        } else {
            Calendar c = Calendar.getInstance();
            dialog = new android.app.DatePickerDialog(
                    this,
                    new android.app.DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                            tvSqyqsj.setText("" + year + "-" + (month + 1) + "-" + dayOfMonth);
                        }
                    },
                    c.get(Calendar.YEAR), // 传入年份
                    c.get(Calendar.MONTH), // 传入月份
                    c.get(Calendar.DAY_OF_MONTH) // 传入天数
            );
        }
        return dialog;
    }


    //开始时间
    @OnClick(R.id.li_yqs1)
    void starttimeClick() {
        timeData = tvSqyqsj.getText().toString();
        showDialog(0);
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLoading();
                applydelay = new Applydelay();
                String rwm = tvRmw.getText().toString();
                String kssj = tvKssj.getText().toString();
                String jiesusj = tvJiesusj.getText().toString();
                String jssj = tvJssj.getText().toString();
                String sqyqsj = tvSqyqsj.getText().toString();
                String sqly = etSqly.getText().toString();

                applydelay.setTask_code(task.getTask_code());
                applydelay.setTask_name(task.getTask_name());

                applydelay.setAuditor_code(task.getAuditor_code());

                applydelay.setBegin_time(StrUtils.IsNull(kssj));
                applydelay.setOver_time(StrUtils.IsNull(jiesusj));
                applydelay.setConfirm_time(StrUtils.IsNull(jssj));
                applydelay.setApply_delay_time(StrUtils.IsNull(sqyqsj));
                applydelay.setApply_reason(StrUtils.IsNull(sqly));

                if (null != users) {
                    applydelay.setApply_user_code(users.getUser_code());
                    applydelay.setApply_username(users.getUsername());
                    applydelay.setApply_nickname(users.getNickname());
                }

                if (StrUtils.IsNotEmpty(sqyqsj) && StrUtils.IsNotEmpty(sqly)) {
                    new FinishRefresh(getMy()).execute();
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
        if (null != task) {
            tvRmw.setText(task.getTask_name());
            tvKssj.setText(task.getBegin_time());
            tvJiesusj.setText(task.getOver_time());
            tvJssj.setText(task.getConfirm_time());
        }
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            if (null != applydelay) {
                try {
                    jCloudDB.save(applydelay);

                    //更改任务表状态
                    String sql = "update task set now_state = 4 , now_state_name = '延期申请中'  where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and principal_code like " + StrUtils.QuotedStrLike(users.getUser_code());
                    CloudDB.execSQL(sql);

                    //保存到流程表里
                    Taskflow taskflow = new Taskflow();
                    taskflow.setUser_code(users.getUser_code());
                    taskflow.setNickname(users.getNickname());
                    taskflow.setTask_code(task.getTask_code());
                    taskflow.setNow_state(4);
                    taskflow.setNow_state_name(Contants.yqsqz);
                    taskflow.setUser_action(Contants.action_sqyq);
                    jCloudDB.save(taskflow);
                } catch (CloudServiceException e) {
                    result = "0";
                    e.printStackTrace();
                }

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                ToastShow("任务发布成功");
                OttUtils.push("yyq_refresh", "");
                finish();
            } else {
                ToastShow("保存失败");
            }
            hideLoading();
        }
    }


    @Subscribe
    public void dateTimeSelect(ActivityMsgEvent activityMsgEvent) {
        if (activityMsgEvent.getMsg().equals("dateTimePick")) {
            String birthday = activityMsgEvent.getJson();
            if (StrUtils.IsNotEmpty(birthday)) {
                tvSqyqsj.setText(birthday);
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
