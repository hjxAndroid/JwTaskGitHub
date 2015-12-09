package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudFile;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import api.photoview.Bimp;
import api.util.Contants;
import api.util.Utils;
import api.view.ListNoScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;
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

    private String org_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        ButterKnife.bind(this);
        setTitle("任务详情");
        setData();
    }

    private void setData() {
        Task task = (Task) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        if (null != task) {


            tvNickname.setText(StrUtils.IsNull(task.getNickname()));
            tvNowState.setText(StrUtils.IsNull(task.getNow_state_name()));
            tvTaskName.setText(StrUtils.IsNull(task.getTask_name()));
            tvStartTime.setText(StrUtils.IsNull(task.getBegin_time()));
            tvEndTime.setText(StrUtils.IsNull(task.getOver_time()));

            tvTaskYq.setText(StrUtils.IsNull(task.getTask_request()));
            tvYxj.setText(StrUtils.IsNull(task.getPriority()));
            tvKhbz.setText(StrUtils.IsNull(task.getAssess_standard()));

            org_code = task.getTask_code();
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
                        "task_code=" + StrUtils.QuotedStr(org_code));
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
}
