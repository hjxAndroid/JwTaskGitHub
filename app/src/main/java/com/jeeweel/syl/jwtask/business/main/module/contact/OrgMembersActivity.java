package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
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
public class OrgMembersActivity extends JwListActivity {
    List<Userorg> mListItems = new ArrayList<Userorg>();

    @Bind(R.id.listview)
    PullToRefreshListView listview;
    private CommonAdapter commonAdapter;
    private Users users;
    List<Userorg> list;
    private String orgCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        users = JwAppAplication.getUsers();
        setTitle("组织成员");
        ButterKnife.bind(this);
        getData();
        initListViewController();
    }


    private void getData() {
        orgCode = getIntent().getStringExtra(StaticStrUtils.baseItem);
    }

    @Override
    protected void initListViewController() {
        commonAdapter = new CommonAdapter<Userorg>(getMy(), mListItems, R.layout.item_org_members) {
            @Override
            public void convert(ViewHolder helper, Userorg item) {
                String nickname = item.getNickname();
                helper.setText(R.id.tv_name, item.getNickname());
                helper.setText(R.id.tv_nick_name, nickname);

                if (item.getPhoto_code() != null) {
                    ImageView iv_photo = helper.getImageView(R.id.iv_xz);
                    //     Logv("qwqwqw--"+Utils.getPicUrl()+"!!!"+item.getPhoto_code());
                    JwImageLoader.displayImage(Utils.getPicUrl() + item.getPhoto_code(), iv_photo);
                } else {
                    if (nickname.length() > 2) {
                        nickname = nickname.substring(nickname.length() - 2, nickname.length());
                        helper.setText(R.id.tv_user_head1, nickname);
                    } else {
                        helper.setText(R.id.tv_user_head1, nickname);
                    }
                }

//                if (item.getAdmin_state() == 1) {
//                    ImageView tv_name = helper.getImageView(R.id.iv_admin);
//                    tv_name.setVisibility(View.VISIBLE);
//                }
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }

    @Override
    public void onListItemClick(int position) {
        String flag = "OrgMembers";
        Userorg userorg = (Userorg) commonAdapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra("flag", true);
        intent.putExtra("mark", flag);
        intent.putExtra(StaticStrUtils.baseItem, userorg.getUser_name());
        intent.putExtra("friend_code", userorg.getUser_code());
        intent.putExtra("org_code", userorg.getOrg_code());
        intent.setClass(OrgMembersActivity.this, FriendDetailActivity.class);
        JwStartActivity(intent);
        //     JwStartActivity(FriendDetailActivity.class, userdept.getUsername());
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
                    list = jCloudDB.findAllByWhere(Userorg.class,
                            " org_code = " + StrUtils.QuotedStr(orgCode));
                    mListItems.clear();
                    removeDuplicate(list);

                    if (ListUtils.IsNotNull(list)) {
                        result = "1";
                        for (Userorg userorg : list) {
                            //取头像
                            String user_code = userorg.getUser_code();
                            String sSql = "pic_code=?";
                            SqlInfo sqlInfo = new SqlInfo();
                            sqlInfo.setSql(sSql);
                            sqlInfo.addValue(user_code);
                            sSql = sqlInfo.getBuildSql();
                            List<Picture> pictureList = jCloudDB.findAllByWhere(Picture.class, sSql);
                            if (ListUtils.IsNotNull(pictureList)) {
                                Picture picture = pictureList.get(0);
                                String path = picture.getPic_road();
                                if (StrUtils.IsNotEmpty(path)) {
                                    //存头像
                                    userorg.setPhoto_code(path);
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
     * 去除多余元素
     *
     * @param list
     */
    public void removeDuplicate(List<Userorg> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getUser_code().equals(list.get(i).getUser_code())) {
                    list.remove(j);
                }
            }
        }
    }

    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (StrUtils.IsNotEmpty(msg) && msg.equals("OrgMembersRefreash")) {
            list.clear();
            onListViewHeadRefresh();
        }
    }
}
