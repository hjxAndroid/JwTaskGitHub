package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Signed;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ragn on 2015/12/16.
 */
public class CheckOutSignActivity extends JwListActivity {
    List<Signed> mListItems = new ArrayList<Signed>();
    @Bind(R.id.listview)
    PullToRefreshListView listview;
    private CommonAdapter commonAdapter;
    private Users users;
    private String signedCode;
    private String userNick;
    private String userPic;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<Signed> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_sign);
        ButterKnife.bind(this);
        setTitle("我的签到");
        users = JwAppAplication.getInstance().users;
        getData();
        initUser();
        initListViewController();
    }

    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<Signed>(getMy(), mListItems, R.layout.item_my_signed) {
            @Override
            public void convert(ViewHolder helper, Signed item) {
                helper.setText(R.id.tv_check_title_name, item.getSign_title());
                helper.setText(R.id.tv_check_time, item.getCreate_time().substring(0, 16));
                helper.setText(R.id.tv_check_loc, item.getLocation());
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }

    private void initUser() {
        userNick = users.getNickname();
        if (StrUtils.IsNotEmpty(userNick)) {
            userPic = userNick.substring(userNick.length() - 2, userNick.length());
        } else {
            userPic = "";
        }
    }

    private void getData() {
        showLoading();
        signedCode = getIntent().getStringExtra(StaticStrUtils.baseItem);
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
                        list = jCloudDB.findAllByWhere(Signed.class,
                                "sign_user_code ="
                                        + StrUtils.QuotedStr(users.getUser_code())
                                        + " and " +
                                        " sign_code = " +
                                        StrUtils.QuotedStr(signedCode) +
                                        " ORDER BY create_time DESC " +
                                        " limit " +
                                        pageStart +
                                        "," +
                                        pageEnd);
                        mListItems.clear();
                    } else {
                        setPage(false);
                        list = jCloudDB.findAllByWhere(Signed.class,
                                "sign_user_code ="
                                        + StrUtils.QuotedStr(users.getUser_code())
                                        + " and " +
                                        " sign_code = " +
                                        StrUtils.QuotedStr(signedCode) +
                                        " ORDER BY create_time DESC " +
                                        " limit " +
                                        pageStart +
                                        "," +
                                        pageEnd);
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
            }
            listview.onRefreshComplete();
            hideLoading();
        }
    }

    private void setPage(boolean tag) {
        if (tag) {
            pageStart = 0;
            pageEnd = 10;
        } else {
            pageStart += addNum;
            pageEnd += addNum;
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
