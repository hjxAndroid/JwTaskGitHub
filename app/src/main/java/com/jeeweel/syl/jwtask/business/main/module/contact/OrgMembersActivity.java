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
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.UserdeptItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.UserorgItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
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
public class OrgMembersActivity extends JwActivity {
    List<UserorgItem> mListItems = new ArrayList<UserorgItem>();

    @Bind(R.id.listview)
    ListView listview;
    private CommonAdapter commonAdapter;
    private Users users;
    List<UserorgItem> list;
    private String orgCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        users = JwAppAplication.getUsers();
        setTitle("组织成员");
        ButterKnife.bind(this);
        getData();
        initListView();
        showLoading();
        new FinishRefresh(getMy()).execute();
    }


    private void getData() {
        orgCode = getIntent().getStringExtra(StaticStrUtils.baseItem);
    }

    protected void initListView() {
        commonAdapter = new CommonAdapter<UserorgItem>(getMy(), mListItems, R.layout.item_org_members) {
            @Override
            public void convert(ViewHolder helper, UserorgItem item) {
                String nickname = item.getNickname();
                helper.setText(R.id.tv_name, item.getNickname());
                helper.setText(R.id.tv_nick_name, nickname);

               // if (item.getPic_road() != null) {
                    ImageView iv_photo = helper.getImageView(R.id.iv_xz);
                    //     Logv("qwqwqw--"+Utils.getPicUrl()+"!!!"+item.getPhoto_code());
                    JwImageLoader.displayImage(Utils.getPicUrl() + item.getPic_road(), iv_photo);
//                } else {
//                    if (nickname.length() > 2) {
//                        nickname = nickname.substring(nickname.length() - 2, nickname.length());
//                        helper.setText(R.id.tv_user_head1, nickname);
//                    } else {
//                        helper.setText(R.id.tv_user_head1, nickname);
//                    }
//                }

            }
        };
        listview.setAdapter(commonAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String flag = "OrgMembers";
                UserorgItem userorg = (UserorgItem) commonAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("flag", true);
                intent.putExtra("mark", flag);
                intent.putExtra(StaticStrUtils.baseItem, userorg.getUser_name());
                intent.putExtra("friend_code", userorg.getUser_code());
                intent.putExtra("org_code", userorg.getOrg_code());
                intent.setClass(OrgMembersActivity.this, FriendDetailActivity.class);
                JwStartActivity(intent);
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
//                    list = jCloudDB.findAllByWhere(Userorg.class,
//                            " org_code = " + StrUtils.QuotedStr(orgCode));

                    String readSql  = "select * from userorg left join picture on userorg.user_code = picture.pic_code WHERE org_code = "+ StrUtils.QuotedStr(orgCode);
                    list = jCloudDB.findAllBySql(UserorgItem.class, readSql);


                    mListItems.clear();
                    removeDuplicate(list);
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
     * 去除多余元素
     *
     * @param list
     */
    public void removeDuplicate(List<UserorgItem> list) {
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
            showLoading();
            new FinishRefresh(getMy()).execute();
        }
    }
}
