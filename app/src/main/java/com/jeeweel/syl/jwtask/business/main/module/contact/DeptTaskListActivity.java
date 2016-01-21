package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.DeptTask;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.UserdeptItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.UserorgItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.task.JobDetailActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ragn on 2016/1/5.
 */
public class DeptTaskListActivity extends JwActivity {
    List<DeptTask> mListItems = new ArrayList<DeptTask>();

    @Bind(R.id.listview)
    ListView listview;
    private CommonAdapter commonAdapter;
    private Users users;
    List<DeptTask> list;
    private Userdept userdept;
    DeptTask deptTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept_task);
        setTitle("任务列表");
        users = JwAppAplication.getInstance().getUsers();
        ButterKnife.bind(this);
        getData();
        initListView();
        showLoading();

        new FinishRefresh(getMy()).execute();
    }


    private void getData() {
        userdept = (Userdept)getIntent().getSerializableExtra(StaticStrUtils.baseItem);
    }

    protected void initListView() {
        commonAdapter = new CommonAdapter<DeptTask>(getMy(), mListItems, R.layout.item_news) {
            @Override
            public void convert(ViewHolder helper, DeptTask item) {
                helper.setText(R.id.task,item.getTask_name());
                helper.setText(R.id.tv_task_news,item.getNickname());
                helper.setText(R.id.tv_task_time,item.getCreate_time());
            }
        };
        listview.setAdapter(commonAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deptTask = (DeptTask)commonAdapter.getItem(position);
                showLoading();
                new GetTaskRefresh(getMy()).execute();
            }
        });
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

            if (null != users) {
                try {
                    list = jCloudDB.findAllByWhere(DeptTask.class,
                           "dept_code = " + StrUtils.QuotedStr(userdept.getDept_code()) + " and user_code="+StrUtils.QuotedStr(userdept.getUser_code()));

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
                mListItems.addAll(list);
                commonAdapter.notifyDataSetChanged();
            } else {
                //没有加载到数据
            }
            hideLoading();
        }
    }

    /**
     * 保存到数据库
     */
    private class GetTaskRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;
        List<Task> tasks;
        /**
         * @param context 上下文
         */
        public GetTaskRefresh(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            if (null != deptTask) {
                try {
                    tasks = jCloudDB.findAllByWhere(Task.class,
                            "task_code = " + StrUtils.QuotedStr(deptTask.getTask_code()));
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
               if(ListUtils.IsNotNull(tasks)){
                   Task task = tasks.get(0);
                   JwStartActivity(JobDetailActivity.class,task);
               }
            } else {
                //没有加载到数据
            }
            hideLoading();
        }
    }
}
