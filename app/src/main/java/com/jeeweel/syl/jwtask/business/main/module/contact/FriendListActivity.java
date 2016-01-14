package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.FriendItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.UserdeptItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.config.jsonclass.V_publicityunread;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.basic.GetUserPicture;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
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

import api.util.Contants;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FriendListActivity extends JwListActivity {
    List<FriendItem> mListItems = new ArrayList<FriendItem>();

    @Bind(R.id.listview)
    PullToRefreshListView listview;
    private CommonAdapter commonAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<FriendItem> list;

    private Users users;

    /**
     * 用于判断是从哪请求过来的
     */
    private String tag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        setTitle("好友列表");
        users = JwAppAplication.getInstance().users;
        ButterKnife.bind(this);
        initView();
        initListViewController();
    }

    private void initView() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("添加");
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
        commonAdapter = new CommonAdapter<FriendItem>(getMy(), mListItems, R.layout.item_friend) {
            @Override
            public void convert(ViewHolder helper, FriendItem item) {
                String friend_nickname = item.getFriend_nickname();
                helper.setText(R.id.tv_nick_name, item.getFriend_name());
                helper.setText(R.id.tv_name, friend_nickname);

                ImageView iv_photo = helper.getImageView(R.id.iv_xz);
                //     Logv("qwqwqw--"+Utils.getPicUrl()+"!!!"+item.getPhoto_code());
                JwImageLoader.displayImage(Utils.getPicUrl() + item.getPic_road(), iv_photo);
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }


    @Override
    public void onListItemClick(int position) {
        FriendItem FriendItem = (FriendItem) commonAdapter.getItem(position);
        Intent intent = new Intent();
        if (null != FriendItem) {
            intent.putExtra("friend_code", FriendItem.getFriend_code());
            intent.putExtra(StaticStrUtils.baseItem, FriendItem.getFriend_name());
            intent.setClass(FriendListActivity.this, FriendDetailActivity.class);
            JwStartActivity(intent);
        }
        //     JwStartActivity(FriendDetailActivity.class, FriendItem.getFriend_name());
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

            String result = "1";

            if (null != users) {
                try {
                    if (mode == 0) {
                        setPage(true);
                        String sql = "select * from Friend left join picture on Friend.friend_code = picture.pic_code  where user_code = " + StrUtils.QuotedStr(users.getUser_code())  +" and state = '2' GROUP BY friend_code  limit " + pageStart + "," + pageEnd;
                        list = jCloudDB.findAllBySql(FriendItem.class, sql);
                        mListItems.clear();
                    } else {
                        setPage(false);
                        String sql = "select * from Friend left join picture on Friend.friend_code = picture.pic_code  where user_code = " + StrUtils.QuotedStr(users.getUser_code())  +" and state = '2' GROUP BY friend_code  limit " + pageStart + "," + pageEnd;
                        list = jCloudDB.findAllBySql(FriendItem.class, sql);
                    }

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
                removeDuplicate(mListItems);
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
     * 去除多余元素
     *
     * @param list
     */
    public void removeDuplicate(List<FriendItem> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getFriend_code().equals(list.get(i).getFriend_code())) {
                    list.remove(j);
                }
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
