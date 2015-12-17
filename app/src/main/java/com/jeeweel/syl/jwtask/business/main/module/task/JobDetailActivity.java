package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.basic.GetUserPicture;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.DateHelper;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;

import java.util.List;

import api.util.Contants;
import api.util.Utils;
import api.view.CustomDialog;
import api.view.ListNoScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class JobDetailActivity extends JwActivity {

    @Bind(R.id.iv_xz)
    CircleImageView ivXz;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.tv_now_state)
    TextView tvNowState;
    @Bind(R.id.tv_task_name)
    TextView tvTaskName;
    @Bind(R.id.tv_start_time)
    TextView tvStartTime;
    @Bind(R.id.tv_end_time)
    TextView tvEndTime;
    @Bind(R.id.tv_fbr)
    TextView tvFbr;
    @Bind(R.id.tv_fzr)
    TextView tvFzr;
    @Bind(R.id.tv_shr)
    TextView tvShr;
    @Bind(R.id.tv_cyz)
    TextView tvCyz;
    @Bind(R.id.tv_gcz)
    TextView tvGcz;
    @Bind(R.id.tv_task_yq)
    TextView tvTaskYq;
    @Bind(R.id.tv_yxj)
    TextView tvYxj;
    @Bind(R.id.tv_khbz)
    TextView tvKhbz;
    @Bind(R.id.listview)
    ListNoScrollView listview;

    @Bind(R.id.bt_qrjs)
    Button btQrjs;
    @Bind(R.id.bt_djsh)
    Button btDjsh;
    @Bind(R.id.bt_sqyq)
    Button btSqyq;
    @Bind(R.id.bt_fqrw)
    Button btFqrw;
    @Bind(R.id.li_bt)
    LinearLayout liBt;

    private Users users;

    /**
     * 任务主键
     */
    private Task task;
    String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        ButterKnife.bind(this);
        setTitle("任务详情");
        users = JwAppAplication.getInstance().getUsers();
        setData();
    }

    private void setData() {
        flag = getIntent().getStringExtra("flag");
        if (StrUtils.IsNotEmpty(flag)) {
            liBt.setVisibility(View.GONE);
        }


        task = (Task) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        if (null != task) {

            if (users != null) {
                new GetUserPicture(getMy(), ivXz, users.getUser_code()).execute();
            }

            tvNickname.setText(StrUtils.IsNull(task.getNickname()));
            tvNowState.setText(StrUtils.IsNull(task.getNow_state_name()));
            tvTaskName.setText(StrUtils.IsNull(task.getTask_name()));
            tvStartTime.setText(StrUtils.IsNull(task.getBegin_time()));
            tvEndTime.setText(StrUtils.IsNull(task.getOver_time()));

            tvTaskYq.setText(StrUtils.IsNull(task.getTask_request()));
            tvYxj.setText(StrUtils.IsNull(task.getPriority()));
            tvKhbz.setText(StrUtils.IsNull(task.getAssess_standard()));

            int state = task.getNow_state();
            if (state == 1) {
                btQrjs.setText("已确认");
                btQrjs.setClickable(false);
                btSqyq.setClickable(true);
            } else if (state == 2) {
                btQrjs.setText("已确认");
                btQrjs.setClickable(false);
                btSqyq.setClickable(false);

                btDjsh.setText("已递交");
                btDjsh.setClickable(false);
                btSqyq.setClickable(false);
            } else if(state == 4){
                btQrjs.setText("已确认");
                btQrjs.setClickable(false);
                btSqyq.setText("延期申请中");
            }else if(state == 5){
                btQrjs.setText("已确认");
                btQrjs.setClickable(false);

                btSqyq.setClickable(false);
                btSqyq.setText("延期通过");
            }else if(state == 6){
                btQrjs.setText("已确认");
                btQrjs.setClickable(false);

                btSqyq.setClickable(false);
                btSqyq.setText("延期驳回");
            }else if(state == 7){
                btFqrw.setClickable(false);
                btFqrw.setText("放弃申请中");
                //其他按钮都不可点
                btQrjs.setClickable(false);
                btDjsh.setClickable(false);
                btSqyq.setClickable(false);
            }else if(state == 8){
                btFqrw.setClickable(false);
                btFqrw.setText("任务已放弃");
                //其他按钮都不可点
                btQrjs.setClickable(false);
                btDjsh.setClickable(false);
                btSqyq.setClickable(false);
            }else if(state == 9){
                btFqrw.setClickable(false);
                btFqrw.setText("放弃驳回");
                //其他按钮都恢复可点
                btQrjs.setClickable(true);
                btDjsh.setClickable(true);
                btSqyq.setClickable(true);
            }else{
                btSqyq.setClickable(false);
            }

            showLoading();
            new FinishRefresh(getMy()).execute();
        }

    }


    @OnClick(R.id.bt_qrjs)
    void qrjsClick() {
        new changeTask(getMy()).execute();
    }

    @OnClick(R.id.bt_djsh)
    void djshClick() {
        JwStartActivity(SendShActivity.class, task);
    }

    @OnClick(R.id.bt_sqyq)
    void sqyqClick() {
        JwStartActivity(ApplyDelayActivity.class, task);
    }

    @OnClick(R.id.bt_fqrw)
    void fqrwClick() {
        JwStartActivity(ApplyGiveUpActivity.class, task);
    }

    @OnClick(R.id.tv_pl)
    void commitClick() {
        Intent intent = new Intent(this, CommitListActivity.class);
        intent.putExtra(StaticStrUtils.baseItem, task);
        if (StrUtils.IsNotEmpty(flag) && flag.equals("gc")) {
            intent.putExtra("flag", "gc");
        }
        startActivity(intent);
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;
        List<Taskflow> taskflows;
        List<Task> tasks;

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


            try {
                if (null != task) {
                    String unid = Utils.getUUid();
                    String sql = "call get_task_detail('" + unid + "','" + task.getTask_code() + "');";
                    CloudDB.execSQL(sql);

                    String newSql = "select * from tmp" + unid;
                    //查找数据
                    tasks = jCloudDB.findAllBySql(Task.class, newSql);

                    String deletSql = "DROP TABLE tmp" + unid;
                    CloudDB.execSQL(deletSql);
                }


                taskflows = jCloudDB.findAllByWhere(Taskflow.class,
                        "task_code=" + StrUtils.QuotedStr(task.getTask_code()));
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                if (ListUtils.IsNotNull(tasks)) {
                    Task task = tasks.get(0);
                    tvFbr.setText(task.getPromulgator_nickname());
                    tvFzr.setText(task.getPrincipal_nickname());
                    tvShr.setText(task.getAuditor_nickname());
                    tvCyz.setText(task.getParticipant_nickname());
                    tvGcz.setText(task.getObserver_nickname());
                }


                CommonAdapter commonAdapter = new CommonAdapter<Taskflow>(getMy(), taskflows, R.layout.item_task_detail) {
                    @Override
                    public void convert(ViewHolder helper, Taskflow item) {
                        helper.setText(R.id.tv_nick_name, item.getNickname());
                        helper.setText(R.id.tv_action, item.getUser_action());
                        helper.setText(R.id.tv_time, item.getCreate_time());
                    }
                };
                listview.setAdapter(commonAdapter);
            } else {
                ToastShow("保存失败");
            }
            hideLoading();
        }
    }


    /**
     * 改变任务表状态
     */
    private class changeTask extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public changeTask(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            try {
                if (null != users) {
                    if (null != task) {
                        String sql = "update task set now_state = 1 , now_state_name = '已确认',confirm_time =" + StrUtils.QuotedStr(DateHelper.getCurDateTime()) + "  where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and principal_code like " + StrUtils.QuotedStrLike(users.getUser_code());
                        CloudDB.execSQL(sql);


                        //保存到流程表里
                        Taskflow taskflow = new Taskflow();
                        taskflow.setTask_code(task.getTask_code());
                        taskflow.setNow_state(1);
                        taskflow.setNow_state_name(Contants.yqr);
                        taskflow.setUser_action(Contants.action_qr);
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
                btQrjs.setText("已确认");
                btQrjs.setClickable(false);
                btDjsh.setClickable(true);
            } else {
                ToastShow("操作失败");
            }
            hideLoading();
        }
    }


    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (msg.equals("job_refresh")) {
            btDjsh.setText("已递交");
            btDjsh.setClickable(false);
        }else if(msg.equals("yyq_refresh")){
            btSqyq.setText("延期申请中");
        }else if(msg.equals("give_refresh")){
            btFqrw.setText("放弃申请中");
            btFqrw.setClickable(false);
        }
    }

    public void showAlertDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("是否放弃任务");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
                showLoading();
                new fqTask(getMy()).execute();
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();

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
                        String sql = "update task set now_state = 7 , now_state_name = '放弃申请中'  where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and principal_code like " + StrUtils.QuotedStrLike(users.getUser_code());
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
                btFqrw.setText("放弃申请中");
                btFqrw.setClickable(false);
            } else {
                ToastShow("操作失败");
            }
            hideLoading();
        }
    }
}
