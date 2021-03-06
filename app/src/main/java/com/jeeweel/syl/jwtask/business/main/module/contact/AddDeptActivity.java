package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Dept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import api.util.Contants;
import api.util.OttUtils;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeptActivity extends JwActivity {


    @Bind(R.id.et_group_name)
    EditText etGroupName;
    @Bind(R.id.fr_add)
    FrameLayout frAdd;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.tv_name)
    TextView tvName;

    List<Friend> friendList = new ArrayList<>();

    private CommonAdapter commonAdapter;

    List<Friend> friends;

    private String orgname = "";

    private String org_code = "";

    private String deptName = "";

    private Users users;

    private JCloudDB jCloudDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        setTitle("创建团队");
        orgname = getIntent().getStringExtra(StaticStrUtils.baseItem);
        org_code = getIntent().getStringExtra("org_code");
        ButterKnife.bind(this);
        jCloudDB = new JCloudDB();
        initView();
    }

    private void initView() {
        users = JwAppAplication.getInstance().getUsers();
        if (null != users) {
            tvName.setText(users.getNickname());
        }
        //添加自己为管理员
        commonAdapter = new CommonAdapter<Friend>(self, friendList, R.layout.item_friend) {
            @Override
            public void convert(ViewHolder helper, Friend item) {
                helper.setText(R.id.tv_name, item.getFriend_name());
                helper.setText(R.id.tv_nick_name, item.getFriend_nickname());
            }
        };

        listview.setAdapter(commonAdapter);
    }

    @OnClick(R.id.fr_add)
    void nextClick() {
        JwStartActivity(DeptSelectFriendListActivity.class, Contants.group);
    }

    @OnClick(R.id.bt_ok)
    void okClick() {
        deptName = etGroupName.getText().toString();
        if (StrUtils.IsNotEmpty(deptName)) {
            showLoading("正在努力创建中...");
            new FinishRefresh(getMy()).execute();
        } else {
            ToastShow("请输入团队名称");
        }
    }

    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (StrUtils.IsNotEmpty(msg) && msg.equals(Contants.group)) {
            String json = activityMsgEvent.getParam();
            friends = JwJSONUtils.getParseArray(json, Friend.class);
            friendList.addAll(friends);
            commonAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private String orgUnid;
        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";
            //将组织，团队，和成员保存到数据库
            try {
                if (StrUtils.IsNotEmpty(org_code)) {
                    orgUnid = org_code;
                    //本来已经有，就不必再添加组织表
                } else {
                    orgUnid = Utils.getUUid();

                    //添加到组织表
                    Orgunit orgunit = new Orgunit();
                    orgunit.setOrg_code(orgUnid);
                    orgunit.setOrg_name(orgname);
                    orgunit.setFounder_code(users.getUser_code());
                    orgunit.setFounder_name(users.getUsername());
                    orgunit.setNickname(users.getNickname());
                    jCloudDB.save(orgunit);
                }

                String deptUnid = Utils.getUUid();

                Userdept adminuserdept = new Userdept();
                adminuserdept.setOrg_code(orgUnid);
                adminuserdept.setOrg_name(orgname);
                adminuserdept.setDept_code(deptUnid);
                adminuserdept.setDept_name(deptName);
                adminuserdept.setUser_code(users.getUser_code());
                adminuserdept.setUsername(users.getUsername());
                adminuserdept.setNickname(users.getNickname());
                adminuserdept.setAdmin_state(1);
                jCloudDB.save(adminuserdept);

                //添加到组织用户表
                Userorg adminuserorg = new Userorg();
                adminuserorg.setOrg_code(orgUnid);
                adminuserorg.setOrg_name(orgname);
                adminuserorg.setUser_code(users.getUser_code());
                adminuserorg.setUser_name(users.getUsername());
                adminuserorg.setNickname(users.getNickname());
                jCloudDB.save(adminuserorg);
//                Friend myfriend = new Friend();
//                myfriend.setFriend_code(users.getUser_code());
//                myfriend.setFriend_name(users.getUsername());
//                myfriend.setFriend_nickname(users.getNickname());
//                friendList.add(myfriend);

                //添加到部门表
                Dept dept = new Dept();
                dept.setOrg_code(orgUnid);
                dept.setOrg_name(orgname);
                dept.setDepart_code(deptUnid);
                dept.setDepart_name(deptName);
                dept.setFounder_code(users.getUser_code());
                dept.setFounder_name(users.getUsername());
                dept.setNickname(users.getNickname());
                jCloudDB.save(dept);


                //添加到部门用户表
                for (Friend friend : friendList) {
                    Userdept userdept = new Userdept();
                    userdept.setOrg_code(orgUnid);
                    userdept.setOrg_name(orgname);
                    userdept.setDept_code(deptUnid);
                    userdept.setDept_name(deptName);
                    userdept.setUser_code(friend.getFriend_code());
                    userdept.setUsername(friend.getFriend_name());
                    userdept.setNickname(friend.getFriend_nickname());
                    jCloudDB.save(userdept);

                    //添加到组织用户表
                    Userorg userorg = new Userorg();
                    userorg.setOrg_code(orgUnid);
                    userorg.setOrg_name(orgname);
                    userorg.setUser_code(friend.getFriend_code());
                    userorg.setUser_name(friend.getFriend_name());
                    userorg.setNickname(friend.getFriend_nickname());
                    jCloudDB.save(userorg);

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
                SharedPreferencesUtils.save(getMy(), Contants.org_code, orgUnid);
                SharedPreferencesUtils.save(getMy(), Contants.org_name, orgname);
                ToastShow("创建成功");
                OttUtils.push("deptAdd_refresh", "");
                finish();
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
