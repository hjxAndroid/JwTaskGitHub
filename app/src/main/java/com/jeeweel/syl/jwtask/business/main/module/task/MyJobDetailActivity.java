package com.jeeweel.syl.jwtask.business.main.module.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Submit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import api.util.Contants;
import api.util.OttUtils;
import api.util.Utils;
import api.view.GridNoScrollView;
import api.view.ListNoScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyJobDetailActivity extends JwActivity {

    @Bind(R.id.et_task_name)
    TextView etTaskName;
    @Bind(R.id.et_confirm_time)
    TextView etConfirmTime;
    @Bind(R.id.tv_zwpj)
    TextView tvZwpj;
    @Bind(R.id.tv_wcqk)
    TextView tvWcqk;
    @Bind(R.id.tv_yjfk)
    TextView tvYjfk;
    @Bind(R.id.noScrollgridview)
    GridNoScrollView noScrollgridview;

    Task task;
    Submit submit;
    @Bind(R.id.listview)
    ListNoScrollView listview;

    private AlertDialog dialog;
    Activity context;

    Users users;

    String wcqk;
    String shpj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_job_detail);
        ButterKnife.bind(this);
        setTitle("任务完成情况");
        context = this;
        users = JwAppAplication.getInstance().getUsers();
        getData();
    }


    private void getData() {
        showLoading();
        String task_code = getIntent().getStringExtra(StaticStrUtils.baseItem);
        if (StrUtils.IsNotEmpty(task_code)) {
            task = new Task();
            task.setTask_code(task_code);
        }
        showLoading();
        new FinishRefresh(getMy()).execute();
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        List<Submit> list;
        List<Picture> pictureList;
        List<Taskflow> taskflows;

        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            JCloudDB jCloudDB = new JCloudDB();
            try {
                if (null != task) {
                    list = jCloudDB.findAllByWhere(Submit.class,
                            "task_code=" + StrUtils.QuotedStr(task.getTask_code()));

                    pictureList = jCloudDB.findAllByWhere(Picture.class,
                            "pic_code=" + StrUtils.QuotedStr(task.getTask_code()));

                    taskflows = jCloudDB.findAllByWhere(Taskflow.class,
                            "task_code=" + StrUtils.QuotedStr(task.getTask_code()));
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
                if (ListUtils.IsNotNull(list)) {
                    submit = list.get(0);
                    etTaskName.setText(StrUtils.IsNull(submit.getTask_name()));
                    etConfirmTime.setText(StrUtils.IsNull(task.getConfirm_time()));
                    tvWcqk.setText(StrUtils.IsNull(submit.getPerformance()));
                    tvYjfk.setText(StrUtils.IsNull(submit.getFeedback()));
                    tvZwpj.setText(StrUtils.IsNull(submit.getEvaluate()));
                }

                if (ListUtils.IsNotNull(pictureList)) {
                    CommonAdapter commonAdapter = new CommonAdapter<Picture>(getMy(), pictureList, R.layout.item_img) {
                        @Override
                        public void convert(ViewHolder helper, Picture item) {
                            ImageView imageView = helper.getImageView(R.id.img);
                            JwImageLoader.displayImage(Utils.getPicUrl() + item.getPic_road(), imageView);
                        }
                    };
                    noScrollgridview.setAdapter(commonAdapter);
                }

                if (ListUtils.IsNotNull(taskflows)) {
                    CommonAdapter commonAdapter = new CommonAdapter<Taskflow>(getMy(), taskflows, R.layout.item_task_detail) {
                        @Override
                        public void convert(ViewHolder helper, Taskflow item) {
                            helper.setText(R.id.tv_nick_name, item.getNickname());
                            helper.setText(R.id.tv_action, item.getUser_action());
                            helper.setText(R.id.tv_time, item.getCreate_time());
                        }
                    };
                    listview.setAdapter(commonAdapter);
                }
            } else {

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
