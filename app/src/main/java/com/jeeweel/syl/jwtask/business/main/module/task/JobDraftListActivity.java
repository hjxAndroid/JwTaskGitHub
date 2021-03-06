package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskdraft;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class JobDraftListActivity extends JwListActivity {
    List<Taskdraft> mListItems = new ArrayList<Taskdraft>();

    @Bind(R.id.listview)
    PullToRefreshListView listview;
    @Bind(R.id.et_search)
    EditText etSearch;
    private CommonAdapter commonAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<Taskdraft> list;

    private Users users;
    private String tv_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);
        setTitle("我草稿的");
        users = JwAppAplication.getInstance().getUsers();
        ButterKnife.bind(this);
        initListViewController();
        initView();
    }

    private void initView(){
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(StrUtils.isEmpty(etSearch.getText().toString())) {
                    new FinishRefresh(getMy(),0).execute();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @OnClick(R.id.iv_search)
    void searchClick() {
        tv_search = etSearch.getText().toString();
        if (StrUtils.IsNotEmpty(tv_search)) {
            new FinishRefreshSearch(getMy()).execute();
        }
    }


    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<Taskdraft>(getMy(), mListItems, R.layout.item_job_list) {
            @Override
            public void convert(ViewHolder helper, Taskdraft item) {

                helper.setText(R.id.tv_task_name, item.getTask_name());
                helper.setText(R.id.tv_state, item.getNow_state_name());
                helper.setText(R.id.tv_time, item.getCreate_time());
                helper.setText(R.id.tv_yxj, item.getPriority());
                helper.setText(R.id.tv_fbr,item.getPrincipal_nickname());
                helper.setText(R.id.tv_end_time,item.getOver_time());
                String priority = item.getPriority();
                TextView tv_yxj = helper.getView(R.id.tv_yxj);
                tv_yxj.setText(priority);
                if (StrUtils.IsNotEmpty(priority)) {
                    if (priority.equals("急")) {
                        tv_yxj.setTextColor(getResources().getColor(R.color.red));
                    } else if (priority.equals("优先")) {
                        tv_yxj.setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        tv_yxj.setTextColor(getResources().getColor(R.color.green));
                    }
                }

//                //未确认
//                if(state ==0){
//                    imageView.setBackgroundResource(R.drawable.circle_red);
//
//                }else
                int state = item.getNow_state();
                ImageView imageView = helper.getView(R.id.iv_xz);
                //已确认
                if (state == 1) {
                    imageView.setBackgroundResource(R.drawable.circle_red);
                    //未审核
                } else if (state == 2) {
                    imageView.setBackgroundResource(R.drawable.circular_blue);
                    //已审核
                } else if (state == 3) {
                    imageView.setBackgroundResource(R.drawable.circular_portrait);
                } else {
                    imageView.setBackgroundResource(R.drawable.dialog_share_link_default_icon);
                }

            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }


    @Override
    public void onListItemClick(int position) {
        Taskdraft taskdraft = (Taskdraft) commonAdapter.getItem(position);

        String json = new Gson().toJson(taskdraft);
        Task task = (Task)new Gson().fromJson(json,Task.class);
        Intent intent = new Intent(getMy(), JobDetailActivity.class);
        intent.putExtra(StaticStrUtils.baseItem, task);
        intent.putExtra("flag", "fb");
        intent.putExtra("draft", "draft");
        startActivity(intent);
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

            if (null != users) {
                try {
                    if (mode == 0) {
                        setPage(true);
                        list = jCloudDB.findAllByWhere(Taskdraft.class,
                                "promulgator_code = " + StrUtils.QuotedStr(users.getUser_code()) + "ORDER BY now_state ASC  limit " + pageStart + "," + pageEnd);
                        mListItems.clear();
                    } else {
                        setPage(false);
                        list = jCloudDB.findAllByWhere(Taskdraft.class,
                                "promulgator_code = " + StrUtils.QuotedStr(users.getUser_code()) + "ORDER BY now_state ASC  limit " + pageStart + "," + pageEnd);
                    }
                } catch (CloudServiceException e) {
                    e.printStackTrace();
                }

                if (ListUtils.IsNotNull(list)) {
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
                mListItems.addAll(list);
                commonAdapter.notifyDataSetChanged();
            } else {
                //没有加载到数据
            }
            listview.onRefreshComplete();
            hideLoading();
        }
    }


    /**
     * 保存到数据库
     */
    private class FinishRefreshSearch extends AsyncTask<String, Void, String> {
        private Context context;
        private int mode = 0;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefreshSearch(Context context) {
            this.context = context;
            this.mode = mode;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "0";

            if (null != users) {
                try {
                    setPage(true);
                    //已读人数4
                    String sql = "SELECT * from task t where t.principal_code = (SELECT user_code from users where nickname like" + StrUtils.QuotedStrLike(tv_search) + ") or t.task_name LIKE " + StrUtils.QuotedStrLike(tv_search) + " and t.promulgator_code = " + StrUtils.QuotedStr(users.getUser_code()) + " ORDER BY now_state ASC limit " + pageStart + ", " + pageEnd;
                    //查找数据
                    list = jCloudDB.findAllBySql(Taskdraft.class, sql);

                    mListItems.clear();
                } catch (CloudServiceException e) {
                    e.printStackTrace();
                }

                if (ListUtils.IsNotNull(list)) {
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
                mListItems.addAll(list);
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

    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (msg.equals("delet_refresh")) {
            new FinishRefresh(getMy(), 0).execute();
        }
    }
}
