package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Dept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import api.adapter.CheckAdapter;
import api.adapter.CheckFriendAdapter;
import api.util.Contants;
import api.util.OttUtils;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DeptSelectFriendListActivity extends JwListActivity {
    List<Friend> mListItems = new ArrayList<Friend>();
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.cb_all)
    CheckBox cbAll;

    private CheckFriendAdapter checkAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<Friend> list;

    /**
     * 用于判断是从哪请求过来的
     */
    private String tag = "";


    private Userdept udept;

    private int checkNum; // 记录选中的条目数量
    private TextView tv_show;// 用于显示选中的条目数量

    Users users;

    List<Friend> friends = new ArrayList<Friend>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicy_users_list);

        setTitle("选择");

        ButterKnife.bind(this);

        users = JwAppAplication.getInstance().getUsers();
        tag = getIntent().getStringExtra(StaticStrUtils.baseItem);
        if (StrUtils.IsNotEmpty(tag) && tag.equals(Contants.dept_add_friend)) {
            udept = (Userdept) getIntent().getSerializableExtra("userdept");
        }

        initRight();
        initListView();
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                if (cbAll.isChecked()) {
                    // 遍历list的长度，将MyAdapter中的map值全部设为true
                    for (int i = 0; i < list.size(); i++) {
                        checkAdapter.getIsSelected().put(i, true);
                    }
                    // 数量设为list的长度
                    checkNum = list.size();
                    // 刷新listview和TextView的显示
                    checkAdapter.notifyDataSetChanged();

                } else {
                    // 遍历list的长度，将已选的按钮设为未选
                    for (int i = 0; i < list.size(); i++) {
                        if (checkAdapter.getIsSelected().get(i)) {
                            checkAdapter.getIsSelected().put(i, false);
                            checkNum--;// 数量减1
                        }
                    }
                    // 刷新listview和TextView的显示
                    checkAdapter.notifyDataSetChanged();

                }
            }
        });
    }


    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                HashMap<Integer, Boolean> isSelected = checkAdapter.getIsSelected();
                for (int i = 0; i < isSelected.size(); i++) {
                    if (isSelected.get(i)) {
                        friends.add(mListItems.get(i));
                    }
                }
                Gson gson = new Gson();
                String json = gson.toJson(friends);

                //添加自己的好友到部门请求
                if (StrUtils.IsNotEmpty(tag) && tag.equals(Contants.dept_add_friend)) {
                    showLoading();
                    new AddRefresh(getMy()).execute();
                    //部门添加好友请求
                } else if (StrUtils.IsNotEmpty(tag) && tag.equals(Contants.group)) {
                    OttUtils.push(Contants.group, json);
                    finish();
                    //签到请求
                } else if (StrUtils.IsNotEmpty(tag) && tag.equals(Contants.sign)) {
                    OttUtils.push(Contants.sign, json);
                    finish();
                } else {
                    //发布任务请求
                    OttUtils.push(tag, json);
                    finish();
                }
            }
        });
        addMenuView(menuTextView);
    }


    public void initListView() {
        showLoading();
        new FinishRefresh(getMy()).execute();
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
        public FinishRefresh(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "0";

            if (null != users) {
                try {
                    list = jCloudDB.findAllByWhere(Friend.class,
                            "user_name = " + StrUtils.QuotedStr(users.getUsername()) + "and state=2");

                } catch (CloudServiceException e) {
                    e.printStackTrace();
                }

                if (ListUtils.IsNotNull(list)) {
                    result = "1";
                } else {
                    result = "0";
                }


                for (Friend friend : list) {
                    //取头像
                    String friend_code = friend.getFriend_code();

                    String sSql = "pic_code=?";
                    SqlInfo sqlInfo = new SqlInfo();
                    sqlInfo.setSql(sSql);
                    sqlInfo.addValue(friend_code);
                    sSql = sqlInfo.getBuildSql();
                    List<Picture> pictureList = null;
                    try {
                        pictureList = jCloudDB.findAllByWhere(Picture.class, sSql);
                    } catch (CloudServiceException e) {
                        e.printStackTrace();
                    }
                    if (ListUtils.IsNotNull(pictureList)) {
                        Picture picture = pictureList.get(0);
                        String path = picture.getPic_road();
                        if (StrUtils.IsNotEmpty(path)) {
                            //存头像
                            friend.setPhoto_code(path);
                        }
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                mListItems.addAll(list);
                checkAdapter = new CheckFriendAdapter(mListItems, getMy());
                listview.setAdapter(checkAdapter);
            } else {
                //没有加载到数据
            }
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
     * 保存到数据库
     */
    private class AddRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public AddRefresh(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";
            try {
                if (null != udept && ListUtils.IsNotNull(friends)) {
                    //添加到部门用户表
                    for (Friend friend : friends) {

                        List<Userdept> list = jCloudDB.findAllByWhere(Userdept.class,
                                "user_code=" + StrUtils.QuotedStr(friend.getFriend_code()));
                        if (ListUtils.IsNull(list)) {

                            Userdept userdept = new Userdept();
                            userdept.setOrg_code(udept.getOrg_code());
                            userdept.setOrg_name(udept.getOrg_name());
                            userdept.setDept_code(udept.getDept_code());
                            userdept.setDept_name(udept.getDept_name());
                            userdept.setUser_code(friend.getFriend_code());
                            userdept.setUsername(friend.getFriend_name());
                            userdept.setNickname(friend.getFriend_nickname());
                            jCloudDB.save(userdept);

                            //添加到组织用户表
                            Userorg userorg = new Userorg();
                            userorg.setOrg_code(udept.getOrg_code());
                            userorg.setOrg_name(udept.getOrg_name());
                            userorg.setUser_code(friend.getFriend_code());
                            userorg.setUser_name(friend.getFriend_name());
                            userorg.setNickname(friend.getFriend_nickname());
                            jCloudDB.save(userorg);

                        } else {
                            result = "2";
                        }

                    }
                }
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.equals("1")) {
                ToastShow("创建成功");
                OttUtils.push("deptAdd_refresh", "");
                finish();
            } else if (result.equals("2")) {
                ToastShow("请选择不在该组织的用户");
            } else {
                ToastShow("创建失败");
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
