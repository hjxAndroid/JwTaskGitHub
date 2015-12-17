package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Applydelay;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import java.util.List;

import api.util.Contants;
import api.util.OttUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SolveGiveUpActivity extends JwActivity {

    @Bind(R.id.tv_rmw)
    TextView tvRmw;
    @Bind(R.id.tv_kssj)
    TextView tvKssj;
    @Bind(R.id.tv_jiesusj)
    TextView tvJiesusj;
    @Bind(R.id.tv_rwyq)
    TextView tvRwyq;
    @Bind(R.id.tv_fqly)
    TextView tvFqly;
    @Bind(R.id.btn_ty)
    Button btnTy;
    @Bind(R.id.btn_jj)
    Button btnJj;

    Task task;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_give_up);
        ButterKnife.bind(this);
        setTitle("放弃审核");
        getData();
    }

    //同意
    @OnClick(R.id.btn_ty)
    void tyClick() {
        flag = 0;
        new changeTask(getMy()).execute();
    }

    //拒绝
    @OnClick(R.id.btn_jj)
    void jjClick() {
        flag = 1;
        new changeTask(getMy()).execute();
    }

    private void getData() {
        task = (Task) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        if (null != task) {
            tvRmw.setText(StrUtils.IsNull(task.getTask_name()));
            tvKssj.setText(StrUtils.IsNull(task.getBegin_time()));
            tvJiesusj.setText(StrUtils.IsNull(task.getOver_time()));
            tvRwyq.setText(StrUtils.IsNull(task.getTask_request()));
            tvFqly.setText(StrUtils.IsNull(task.getGive_up_content()));
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
                if (null != task) {
                    String sql = "";
                    if (flag == 0) {
                        sql = "update task set now_state = 8 , now_state_name = '已放弃'  where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and auditor_code like " + StrUtils.QuotedStrLike(task.getAuditor_code());
                        CloudDB.execSQL(sql);
                        //保存到流程表里
                        Taskflow taskflow = new Taskflow();
                        taskflow.setTask_code(task.getTask_code());
                        taskflow.setNow_state(8);
                        taskflow.setNow_state_name(Contants.yfq);
                        taskflow.setUser_action(Contants.action_tyfq);
                        jCloudDB.save(taskflow);
                    } else if (flag == 1) {
                        sql = "update task set now_state = 9 , now_state_name = '放弃驳回' where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and auditor_code like " + StrUtils.QuotedStrLike(task.getAuditor_code());
                        CloudDB.execSQL(sql);
                        //保存到流程表里
                        Taskflow taskflow = new Taskflow();
                        taskflow.setTask_code(task.getTask_code());
                        taskflow.setNow_state(9);
                        taskflow.setNow_state_name(Contants.fqbh);
                        taskflow.setUser_action(Contants.action_fqbh);
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
                ToastShow("操作成功");
                OttUtils.push("give_refresh", "");
                finish();
            } else {
                ToastShow("操作失败");
                finish();
            }
            hideLoading();
        }
    }
}
