package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class SolveDelayActivity extends JwActivity {

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
    @Bind(R.id.tv_sqly)
    TextView tvSqly;

    Task task;
    @Bind(R.id.btn_ty)
    Button btnTy;
    @Bind(R.id.btn_jj)
    Button btnJj;
    Applydelay applydelay;

    int flag = 0;
    @Bind(R.id.li_bt)
    LinearLayout liBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_delay);
        ButterKnife.bind(this);
        setTitle("延期审核");
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
        String task_code = getIntent().getStringExtra(StaticStrUtils.baseItem);
        if (StrUtils.IsNotEmpty(task_code)) {
            task = new Task();
            task.setTask_code(task_code);
            liBt.setVisibility(View.GONE);
        } else {
            task = (Task) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        }
        if (null != task) {
            showLoading();
            new FinishRefresh(getMy()).execute();
        }
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;
        List<Applydelay> applydelayList;

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

            if (null != task) {
                try {
                    applydelayList = jCloudDB.findAllByWhere(Applydelay.class,
                            "task_code like " + StrUtils.QuotedStrLike(task.getTask_code()) + " order by create_time desc");

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
                if (ListUtils.IsNotNull(applydelayList)) {
                    applydelay = applydelayList.get(0);
                    tvRmw.setText(StrUtils.IsNull(applydelay.getTask_name()));
                    tvKssj.setText(StrUtils.IsNull(applydelay.getBegin_time()));
                    tvJiesusj.setText(StrUtils.IsNull(applydelay.getOver_time()));
                    tvJssj.setText(StrUtils.IsNull(applydelay.getConfirm_time()));
                    tvSqyqsj.setText(StrUtils.IsNull(applydelay.getApply_delay_time()));
                    tvSqly.setText(StrUtils.IsNull(applydelay.getApply_reason()));
                }
            } else {
                //没有加载到数据
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
                if (null != applydelay) {
                    String sql = "";
                    if (flag == 0) {
                        sql = "update task set now_state = 5 , now_state_name = '已延期',over_time =" + StrUtils.QuotedStr(applydelay.getApply_delay_time()) + "  where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and auditor_code like " + StrUtils.QuotedStrLike(task.getAuditor_code());
                        CloudDB.execSQL(sql);
                        //保存到流程表里
                        Taskflow taskflow = new Taskflow();
                        taskflow.setTask_code(task.getTask_code());
                        taskflow.setNow_state(5);
                        taskflow.setNow_state_name(Contants.yyq);
                        taskflow.setUser_action(Contants.action_yqtg);
                        jCloudDB.save(taskflow);
                    } else if (flag == 1) {
                        sql = "update task set now_state = 6 , now_state_name = '延期驳回' where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and auditor_code like " + StrUtils.QuotedStrLike(task.getAuditor_code());
                        CloudDB.execSQL(sql);
                        //保存到流程表里
                        Taskflow taskflow = new Taskflow();
                        taskflow.setTask_code(task.getTask_code());
                        taskflow.setNow_state(6);
                        taskflow.setNow_state_name(Contants.yqbh);
                        taskflow.setUser_action(Contants.action_bhyq);
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
                OttUtils.push("delay_refresh", "");
                finish();
            } else {
                ToastShow("操作失败");
                finish();
            }
            hideLoading();
        }
    }
}
