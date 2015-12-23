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
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
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
    List<Friend> mListItems = new ArrayList<Friend>();

    @Bind(R.id.listview)
    PullToRefreshListView listview;
    private CommonAdapter commonAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<Friend> list;

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
        commonAdapter = new CommonAdapter<Friend>(getMy(), mListItems, R.layout.item_friend) {
            @Override
            public void convert(ViewHolder helper, Friend item) {
                String friend_nickname = item.getFriend_nickname();
                helper.setText(R.id.tv_nick_name, item.getFriend_name());
                helper.setText(R.id.tv_name, friend_nickname);

                if (item.getPhoto_code() != null) {
                    ImageView iv_photo = helper.getImageView(R.id.iv_xz);
                    //     Logv("qwqwqw--"+Utils.getPicUrl()+"!!!"+item.getPhoto_code());
                    JwImageLoader.displayImage(Utils.getPicUrl() + item.getPhoto_code(), iv_photo);
                } else {
                    if (friend_nickname.length() > 2) {
                        friend_nickname = friend_nickname.substring(friend_nickname.length() - 2, friend_nickname.length());
                        helper.setText(R.id.tv_user_head1, friend_nickname);
                    } else {
                        helper.setText(R.id.tv_user_head1, friend_nickname);
                    }
                }
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }


    @Override
    public void onListItemClick(int position) {
        Friend friend = (Friend) commonAdapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra("friend_code", friend.getFriend_code());
        intent.putExtra(StaticStrUtils.baseItem, friend.getFriend_name());
        intent.setClass(FriendListActivity.this, FriendDetailActivity.class);
        JwStartActivity(intent);
        //     JwStartActivity(FriendDetailActivity.class, friend.getFriend_name());
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
                                "user_name = " + StrUtils.QuotedStr(users.getUsername()) + "and state=2" + " limit " + pageStart + "," + pageEnd);
                        removeDuplicate(list);
                        mListItems.clear();
                    } else {
                        setPage(false);
                        list = jCloudDB.findAllByWhere(Friend.class,
                                "user_name = " + StrUtils.QuotedStr(users.getUsername()) + "and state=2" + " limit " + pageStart + "," + pageEnd);
                        removeDuplicate(list);
                    }

                    if (ListUtils.IsNotNull(list)) {
                        result = "1";
                        for (Friend friend : list) {
                            //取头像
                            String friend_code = friend.getFriend_code();

                            String sSql = "pic_code=?";
                            SqlInfo sqlInfo = new SqlInfo();
                            sqlInfo.setSql(sSql);
                            sqlInfo.addValue(friend_code);
                            sSql = sqlInfo.getBuildSql();
                            List<Picture> pictureList = jCloudDB.findAllByWhere(Picture.class, sSql);
                            if (ListUtils.IsNotNull(pictureList)) {
                                Picture picture = pictureList.get(0);
                                String path = picture.getPic_road();
                                if (StrUtils.IsNotEmpty(path)) {
                                    //存头像
                                    friend.setPhoto_code(path);
                                }
                            }
                        }
                    } else {
                        result = "0";
                    }
                } catch (CloudServiceException e) {
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
    public void removeDuplicate(List<Friend> list) {
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
