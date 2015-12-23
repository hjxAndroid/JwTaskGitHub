package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskcommit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.contact.FriendDetailActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import api.util.Contants;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommitListActivity extends JwListActivity {

    @Bind(R.id.listview)
    PullToRefreshListView listview;
    @Bind(R.id.et_sned)
    EditText etSned;
    @Bind(R.id.bt_send)
    Button btSend;
    @Bind(R.id.li_send)
    LinearLayout liSend;
    @Bind(R.id.tv_xpl)
    TextView tvXpl;

    Task task;

    Users users;

    CommonAdapter commonAdapter;
    List<Taskcommit> mListItems = new ArrayList<Taskcommit>();
    List<Taskcommit> taskcommits;
    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_list);
        ButterKnife.bind(this);
        setTitle("评论");
        users = JwAppAplication.getInstance().getUsers();
        task = (Task) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        String flag = getIntent().getStringExtra("flag");
        if (StrUtils.IsNotEmpty(flag)) {
            tvXpl.setVisibility(View.GONE);
        }
        initListViewController();
    }

    @OnClick(R.id.tv_xpl)
    void showPlClick() {
        liSend.setVisibility(View.VISIBLE);
        etSned.setFocusable(true);
    }

    @OnClick(R.id.bt_send)
    void sendClick() {
        content = etSned.getText().toString();
        if (StrUtils.IsNotEmpty(content)) {
            showLoading();
            new SavehRefresh(getMy()).execute();
        } else {
            ToastShow("请输入评论内容");
        }
    }

    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<Taskcommit>(getMy(), mListItems, R.layout.item_job_list) {
            @Override
            public void convert(ViewHolder helper, Taskcommit item) {
                helper.setText(R.id.tv_task_name, item.getNickname());
                helper.setText(R.id.tv_state, item.getContent());
                helper.setText(R.id.tv_time, item.getCreate_time());
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }


    @Override
    public void onListItemClick(int position) {

    }

    @Override
    public void onListViewHeadRefresh() {
        showLoading();
        new FinishRefresh(getMy(), 0).execute();
    }

    @Override
    public void onListViewFooterRefresh() {
        showLoading();
        new FinishRefresh(getMy(), 1).execute();
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private int mode = 0;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context, int mode) {
            this.context = context;
            this.mode = mode;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "0";

            if (null != task) {
                try {
                    if (mode == 0) {
                        setPage(true);
                        taskcommits = jCloudDB.findAllByWhere(Taskcommit.class,
                                "task_code = " + StrUtils.QuotedStr(task.getTask_code()) + " limit " + pageStart + "," + pageEnd);
                        mListItems.clear();
                    } else {
                        setPage(false);
                        taskcommits = jCloudDB.findAllByWhere(Taskcommit.class,
                                "task_code = " + StrUtils.QuotedStr(task.getTask_code()) + " limit " + pageStart + "," + pageEnd);
                    }
                } catch (CloudServiceException e) {
                    e.printStackTrace();
                }

                if (ListUtils.IsNotNull(taskcommits)) {
                    result = "1";
                } else {
                    result = "0";
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                mListItems.addAll(taskcommits);
                commonAdapter.notifyDataSetChanged();
            } else {
                //没有加载到数据
            }
            listview.onRefreshComplete();
            hideLoading();
        }
    }

    /**
     * 分页增数
     */

    private void setPage(boolean tag) {
        if (tag) {
            pageStart = 0;
            pageEnd = 10;
        } else {
            pageStart += addNum;
            pageEnd += addNum;
        }
    }

    /**
     * 发送评论
     */
    private class SavehRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;
        Taskcommit taskcommit;

        /**
         * @param context 上下文
         */
        public SavehRefresh(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            try {
                if (null != task) {
                    taskcommit = new Taskcommit();
                    taskcommit.setTask_code(task.getTask_code());
                    taskcommit.setContent(content);
                    taskcommit.setContent(content);
                    if (null != users) {
                        taskcommit.setUser_code(users.getUser_code());
                        taskcommit.setUser_name(users.getUsername());
                        taskcommit.setNickname(users.getNickname());
                    }
                    jCloudDB.save(taskcommit);
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
                mListItems.add(taskcommit);
                commonAdapter.notifyDataSetChanged();
            } else {
                //没有加载到数据
            }
            listview.onRefreshComplete();
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
