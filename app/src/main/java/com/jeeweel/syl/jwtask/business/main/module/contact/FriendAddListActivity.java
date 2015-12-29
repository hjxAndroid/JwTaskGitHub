package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.base.JeeweelApplication;
import com.jeeweel.syl.lib.api.core.jwpublic.integer.IntUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.jeeweel.syl.lib.api.core.toast.JwToast;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import api.util.OttUtils;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FriendAddListActivity extends JwListActivity {
    List<Friend> mListItems = new ArrayList<Friend>();

    @Bind(R.id.listview)
    PullToRefreshListView listview;
    private CommonAdapter commonAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<Friend> list;

    MenuTextView menuTextView;
    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        setTitle("好友列表");
        users = JwAppAplication.getInstance().users;
        setReadState();
        ButterKnife.bind(this);
        initView();
        initListViewController();
    }

    private void initView() {
        menuTextView = new MenuTextView(getMy());
        menuTextView.setText("添加好友");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                JwStartActivity(FriendAddActivity.class);
            }
        });
        addMenuView(menuTextView);
    }

    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<Friend>(getMy(), mListItems, R.layout.item_friend_add) {
            @Override
            public void convert(ViewHolder helper, final Friend item) {
                //接受方
                helper.setText(R.id.tv_name, item.getFriend_name());
                helper.setText(R.id.tv_msg, "我是" + item.getFriend_nickname());
                final TextView textView = helper.getView(R.id.bt_state);
                //对方请求，展示接受按钮
                if (item.getState() == 0) {
                    textView.setText(getResources().getString(R.string.jieshou));
                    textView.setBackgroundColor(getResources().getColor(R.color.ios7blue));
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //接受好友，将好友state设置成2
                            changeState(item);
                            textView.setText(getResources().getString(R.string.tongyi));
                            textView.setTextColor(getResources().getColor(R.color.TextColorGray));
                            textView.setBackgroundColor(getResources().getColor(R.color.divider_color));
                            textView.setClickable(false);
                        }
                    });

                    //已经拒绝掉
                } else if (item.getState() == 1) {
                    textView.setText(getResources().getString(R.string.jujie));
                    textView.setTextColor(getResources().getColor(R.color.TextColorGray));
                    //已经同意
                } else if (item.getState() == 2) {
                    textView.setText(getResources().getString(R.string.tongyi));
                    textView.setTextColor(getResources().getColor(R.color.TextColorGray));
                }

            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }

    public void changeState(Friend item) {
        String phone = item.getUser_name();
        String friendphone = item.getFriend_name();
        if (StrUtils.IsNotEmpty(phone) && StrUtils.IsNotEmpty(friendphone)) {
            new changeTask(getMy()).execute(phone, friendphone);
        } else {
            ToastShow("好有信息不完全");
        }

    }

    @Override
    public void onListItemClick(int position) {
        Friend outBoundItem = (Friend) commonAdapter.getItem(position);
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

            if (null != users) {
                try {
                    if (mode == 0) {
                        setPage(true);
                        list = jCloudDB.findAllByWhere(Friend.class,
                                "user_name = " + StrUtils.QuotedStr(users.getUsername()) + "and send_state=0 ORDER BY create_time DESC limit " + pageStart + "," + pageEnd);
                        mListItems.clear();
                    } else {
                        setPage(false);
                        list = jCloudDB.findAllByWhere(Friend.class,
                                "user_name = " + StrUtils.QuotedStr(users.getUsername()) + "and send_state=0 ORDER BY create_time DESC limit " + pageStart + "," + pageEnd);
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
     * 改变好友状态
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
                    String myphone = params[0].toString();
                    String friendphone = params[1].toString();


                    //改变自己为主体的好友表状态
                    String sql = "update friend set state = 2 where user_name =" + myphone;
                    CloudDB.execSQL(sql);

                    //改变好友为主体的好友表状态
                    String sql1 = "update friend set state = 2 where user_name =" + friendphone;
                    CloudDB.execSQL(sql1);
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
                OttUtils.push("news_refresh", "");
            } else {

            }
            hideLoading();
        }
    }

    private void setReadState() {
        new readChangeTask(getMy()).execute();
    }


    /**
     * 改变已读未读状态
     */
    private class readChangeTask extends AsyncTask<String, Void, String> {
        private Context context;

        /**
         * @param context 上下文
         */
        public readChangeTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "0";
            try {
                if (null != users) {
                    String myphone = users.getUsername();
                    String sql = "update friend set read_state = 1 where user_name =" + myphone;
                    CloudDB.execSQL(sql);
                }
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
               OttUtils.push("news_refresh","");
            } else {
                //没有加载到数据
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
