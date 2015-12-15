package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
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

import java.util.List;

import api.util.Contants;
import api.util.Utils;
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
        if(StrUtils.IsNotEmpty(flag)){
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
            } else if (state == 2) {
                btQrjs.setText("已确认");
                btQrjs.setClickable(false);

                btDjsh.setText("已递交");
                btDjsh.setClickable(false);
            }else if(state!=0){
                btQrjs.setText("已确认");
                btQrjs.setClickable(false);

                btDjsh.setText("已递交");
                btDjsh.setClickable(false);
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

    @OnClick(R.id.tv_pl)
    void commitClick() {
        Intent intent = new Intent(this, CommitListActivity.class);
        intent.putExtra(StaticStrUtils.baseItem, task);
        if(StrUtils.IsNotEmpty(flag)&&flag.equals("gc")){
            intent.putExtra("flag","gc");
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
            } else {
                ToastShow("操作失败");
            }
            hideLoading();
        }
    }


    /**
     * 获取发布人，审核人等信息
     */
    private class getUsersTask extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public getUsersTask(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            try {
                if (null != users) {
                    if (null != task) {
                        String unid = Utils.getUUid();
                        String sql = "call get_task_detail('"+unid+"','"+task.getTask_code()+"');";
                        CloudDB.execSQL(sql);

                        String newSql = "select * from tmp"+unid;
                        //查找数据
                        List<Task> tasks = jCloudDB.findAllBySql(Task.class,newSql);
                        if(ListUtils.IsNotNull(tasks)){
                            Task task = tasks.get(0);
                        }
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
                btDjsh.setText("已递交");
                btDjsh.setClickable(false);
            } else {
                ToastShow("操作失败");
            }
            hideLoading();
        }
    }


    /**
     * 递交任务给上级审核
     */
    private class changeDjTask extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public changeDjTask(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            try {
                if (null != users) {
                    if (null != task) {
                        String sql = "update task set now_state = 2 , now_state_name = '未审核' where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and principal_code like " + StrUtils.QuotedStrLike(users.getUser_code());
                        CloudDB.execSQL(sql);


                        //保存到流程表里
                        Taskflow taskflow = new Taskflow();
                        taskflow.setTask_code(task.getTask_code());
                        taskflow.setNow_state(2);
                        taskflow.setNow_state_name(Contants.wsh);
                        taskflow.setUser_action(Contants.action_dj);
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
                btDjsh.setText("已递交");
                btDjsh.setClickable(false);
            } else {
                ToastShow("操作失败");
            }
            hideLoading();
        }
    }
}
