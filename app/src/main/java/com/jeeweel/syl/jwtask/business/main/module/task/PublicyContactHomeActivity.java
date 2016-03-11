package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.contact.DeptSelectFriendListActivity;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import api.adapter.ExpandableAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicyContactHomeActivity extends JwActivity {


    @Bind(R.id.el_contact)
    ExpandableListView elContact;
    @Bind(R.id.rl_friend)
    RelativeLayout rlFriend;

    Users users;

    List<Orgunit> groups = new ArrayList<Orgunit>();
    List<List<Userdept>> childs = new ArrayList<List<Userdept>>();

    String tag = "";

    String data = "";

    String fzr = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_home);
        ButterKnife.bind(this);
        setTitle(getString(R.string.contact));
        users = JwAppAplication.getInstance().getUsers();
        tag = getIntent().getStringExtra(StaticStrUtils.baseItem);
        data = getIntent().getStringExtra("data");
        fzr = getIntent().getStringExtra("fzr");
        getDate();
    }

    @OnClick(R.id.rl_friend)
    void friendClick() {
        JwStartActivity(DeptSelectFriendListActivity.class, tag);
        finish();
    }


    private void getDate() {
        showLoading("正努力加载中...");
        new FinishRefresh(getMy()).execute();
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        List<Userdept> list = null;

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
                list = jCloudDB.findAllByWhere(Userdept.class,
                        "username=" + StrUtils.QuotedStr(users.getUsername()));
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                //遍历得到夫数组
                if (ListUtils.IsNotNull(list)) {
                    for (Userdept userdept : list) {
                        Orgunit orgunit = new Orgunit();
                        orgunit.setOrg_name(userdept.getOrg_name());
                        orgunit.setOrg_code(userdept.getOrg_code());
                        groups.add(orgunit);
                    }

                }
                removeDuplicate(groups);

                //遍历得到子数组
                if (ListUtils.IsNotNull(list) && ListUtils.IsNotNull(groups)) {
                    for (int i = 0; i < list.size(); i++) {
                        Userdept userdept = list.get(i);
                        for (Orgunit orgunit : groups) {
                            if (userdept.getOrg_code().equals(orgunit.getOrg_code())) {
                                orgunit.setChild(userdept);
                            }
                        }
                    }

                    final ExpandableAdapter expandableAdapter = new ExpandableAdapter(getMy(), groups);
                    elContact.setAdapter(expandableAdapter);

                    for (int i = 0; i < expandableAdapter.getGroupCount(); i++) {
                        elContact.expandGroup(i);
                    }

                    elContact.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                            Orgunit orgunit = expandableAdapter.getList().get(groupPosition);
//                            JwStartActivity(PublicyOegUsersActivity.class, orgunit);
                            Intent intent = new Intent(getMy(), PublicyOegUsersActivity.class);
                            intent.putExtra(StaticStrUtils.baseItem, orgunit);
                            intent.putExtra("tag", tag);
                            intent.putExtra("data", data);
                            if(StrUtils.IsNotEmpty(fzr)){
                                intent.putExtra("fzr", fzr);
                            }
                            startActivity(intent);
                            finish();
                            return true;
                        }
                    });

                    elContact.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                            Userdept userdept = expandableAdapter.getList().get(groupPosition).getChilds().get(childPosition);
                            Intent intent = new Intent(getMy(), PublicyUsersListActivity.class);
                            intent.putExtra(StaticStrUtils.baseItem, userdept);
                            intent.putExtra("tag", tag);
                            intent.putExtra("data", data);
                            if(StrUtils.IsNotEmpty(fzr)){
                                intent.putExtra("fzr", fzr);
                            }
                            startActivity(intent);
                            finish();
                            return true;
                        }
                    });

                }
            } else {
                ToastShow("数据获取出错");
            }
            hideLoading();
        }
    }

    /**
     * 去除多余元素
     *
     * @param list
     */
    public void removeDuplicate(List<Orgunit> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getOrg_code().equals(list.get(i).getOrg_code())) {
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
