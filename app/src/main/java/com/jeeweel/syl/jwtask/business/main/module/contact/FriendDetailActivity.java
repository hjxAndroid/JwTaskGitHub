package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.ActionItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.basic.GetUserPicture;
import com.jeeweel.syl.jwtask.business.main.module.task.WebActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.net.URLEncoder;
import java.util.List;

import api.util.OttUtils;
import api.util.Utils;
import api.view.CustomDialog;
import api.view.ListNoScrollView;
import api.view.TitlePopup;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendDetailActivity extends JwActivity {

    @Bind(R.id.nickname)
    TextView nickname;
    @Bind(R.id.tv_org_name)
    TextView tvOrgName;
    @Bind(R.id.tv_dept_name)
    TextView tvDeptName;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.tv_email)
    TextView tvEmail;
    @Bind(R.id.tv_area)
    TextView tvArea;
    @Bind(R.id.listview)
    ListNoScrollView listview;
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.tv_phone_phone)
    TextView tvPhonePhone;
    @Bind(R.id.iv_phone_msg)
    ImageView ivPhoneMsg;
    @Bind(R.id.iv_phone_phone)
    ImageView ivPhonePhone;


    private List<Users> usersList;

    private List<Userdept> userdepts;
    private List<Task> deptTasks;

    private String phone;

    String friendCode = "";
    String friend_code = "";
    Users users;
    String usercode;
    private TitlePopup titlePopup;
    //用于判断是否为创建者
    Users userIsFounder;
    private String orgCode;
    private String flag = "0";
    private String dept_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        ButterKnife.bind(this);
        setTitle("用户信息");
        userIsFounder = JwAppAplication.getInstance().getUsers();
        Intent intent = getIntent();
        boolean flag = intent.getBooleanExtra("flag", false);
        if (flag == true) {
            initView();
        } else {
            initRight();
        }
        friend_code = intent.getStringExtra("friend_code");
        getData();
        if (StrUtils.IsNotEmpty(friend_code)) {
            new GetUserPicture(getMy(), iv, friend_code).execute();
        }
    }

    private void initView() {
//        MenuTextView menuTextView = new MenuTextView(getMy());
//        menuTextView.setText("加为好友");
//        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
//        menuTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                showAlertDialog();
//            }
//        });
//        addMenuView(menuTextView);
        titlePopup = new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ActionItem action = new ActionItem(getResources().getDrawable(R.drawable.a5), "绩效查看");
        ActionItem action1 = new ActionItem(getResources().getDrawable(R.drawable.a0), "添加好友");
        ActionItem action2 = new ActionItem(getResources().getDrawable(R.drawable.a6), "移出该部");
        titlePopup.addAction(action);
        titlePopup.addAction(action1);
        titlePopup.addAction(action2);
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                if (position == 0) {
                    if (StrUtils.IsNotEmpty(friend_code))
                        JwStartActivity(WebActivity.class, friend_code);
                } else if (position == 1) {
                    showAlertDialog();
                } else if (position == 2) {
                    showAlertDelMemDialog();
                }
            }
        });
        MenuImageView menuImageView = new MenuImageView(getMy());
        menuImageView.setBackgroundResource(R.drawable.more);
        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titlePopup.show(v);
            }
        });
        addMenuView(menuImageView);
    }


    private void initRight() {
        titlePopup = new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ActionItem action = new ActionItem(getResources().getDrawable(R.drawable.a5), "绩效查看");
        ActionItem action1 = new ActionItem(getResources().getDrawable(R.drawable.a7), "删除好友");
        titlePopup.addAction(action);
        titlePopup.addAction(action1);
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                if (position == 0) {
                    if (StrUtils.IsNotEmpty(friend_code))
                        JwStartActivity(WebActivity.class, friend_code);
                } else if (position == 1) {
                    showAlertDialogDelFriends();
                }
            }
        });
        MenuImageView menuImageView = new MenuImageView(getMy());
        menuImageView.setBackgroundResource(R.drawable.more);
        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titlePopup.show(v);
            }
        });
        addMenuView(menuImageView);
    }

    public void showAlertDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("是否添加为好友");
        builder.setTitle("提示");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
                String friendPhone = tvPhone.getText().toString();
                save(friendPhone);
            }
        });

        builder.setNegativeButton("否",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();

    }

    public void showAlertDelMemDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("您确定要移除该成员？");
        builder.setTitle("提示");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showLoading();
                new deleteMember(getMy()).execute();
            }
        });

        builder.setNegativeButton("否",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();

    }

    public void showAlertDialogDelFriends() {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("是否删除该好友");
        builder.setTitle("提示");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
                showLoading();
                new FinishRefreshDelFriends(getMy()).execute();
            }
        });

        builder.setNegativeButton("否",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();

    }

    private void save(String friendPhone) {
        List<Users> usersList = JwAppAplication.getInstance().finalDb.findAll(Users.class);
        if (ListUtils.IsNotNull(usersList)) {
            users = usersList.get(0);
            String nickname = users.getNickname();
            usercode = users.getUser_code();
            String myphone = users.getUsername();
            if (StrUtils.IsNotEmpty(friendPhone) && StrUtils.IsNotEmpty(nickname)) {
                showLoading();
                new AddFriend(getMy()).execute(nickname, myphone, friendPhone);
            } else {
                ToastShow("您要先完善信息，才能添加好友");
            }
        }
    }


    /**
     * 保存到数据库
     */
    private class AddFriend extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public AddFriend(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "0";

            String unid = Utils.getUUid();
            String nickname = params[0].toString();
            String myPhone = params[1].toString();

            String friendPhone = params[2].toString();

            //先判断是否已经添加了好友
            List<Friend> friendsList = null;
            try {
                friendsList = jCloudDB.findAllByWhere(Friend.class,
                        "user_name = " + myPhone + " and friend_name=" + StrUtils.QuotedStr(friendPhone) + "and state = 2");
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }

            //尚未添加该好友
            if (ListUtils.IsNull(friendsList)) {
                //先判断好友是否存在
                List<Users> list = null;
                try {
                    list = jCloudDB.findAllByWhere(Users.class,
                            "username=" + StrUtils.QuotedStr(friendPhone));
                    if (ListUtils.IsNotNull(list)) {
                        friendCode = list.get(0).getUser_code();
                    }
                } catch (CloudServiceException e) {
                    e.printStackTrace();
                }


                if (ListUtils.IsNotNull(list)) {
                    String friendNickname = list.get(0).getNickname();

                    //添加到自己为主体的好友表
                    Friend myself = new Friend();
                    myself.setUnid_code(unid);
                    myself.setUser_code(usercode);
                    myself.setUser_name(myPhone);
                    myself.setUser_nickname(nickname);
                    myself.setFriend_code(friendCode);
                    myself.setFriend_name(friendPhone);
                    myself.setFriend_nickname(friendNickname);
                    myself.setState(0);
                    //发送状态
                    myself.setSend_state(1);

                    try {
                        if (jCloudDB.save(myself)) {
                            //添加到好友为主体的好友表
                            Friend friend = new Friend();
                            friend.setUnid_code(unid);
                            friend.setUser_code(friendCode);
                            friend.setUser_name(friendPhone);
                            friend.setUser_nickname(friendNickname);
                            friend.setFriend_code(usercode);
                            friend.setFriend_name(myPhone);
                            friend.setFriend_nickname(nickname);
                            friend.setState(0);
                            //接受状态
                            myself.setSend_state(0);
                            if (jCloudDB.save(friend)) {
                                result = "1";
                            }
                        }
                    } catch (CloudServiceException e) {
                        e.printStackTrace();
                    }

                } else {
                    result = "2";
                }

            } else {
                result = "3";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                pushData();
                ToastShow("添加好友请求已发出");
            } else if (result.equals("2")) {
                ToastShow("好友不存在");
            } else if (result.equals("3")) {
                ToastShow("该用户已经是您的好友");
            } else {
                ToastShow("好友保存出错");
            }
            hideLoading();
        }
    }


    public void pushData() {
        if (users != null) {
            if (StrUtils.IsNotEmpty(friendCode)) {
                // users.getNickname()+"请求添加您未好友"
                String title = users.getNickname();
                try {
                    title = URLEncoder.encode(title, "utf-8");
                    title = URLEncoder.encode(title, "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String content = users.getNickname() + "请求添加您未好友";
                try {
                    content = URLEncoder.encode(content, "utf-8");
                    content = URLEncoder.encode(content, "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String param = "?user_code=" + friendCode + "&title=" + title + "&content=" + content;
                String apiStr = Utils.getPushUrl() + param;
                JwHttpGet(apiStr, true);
            }
        }
    }

    private void getData() {
        showLoading();
        phone = getIntent().getStringExtra(StaticStrUtils.baseItem);
        orgCode = getIntent().getStringExtra("org_code");
        flag = getIntent().getStringExtra("mark");
        dept_code = getIntent().getStringExtra("dept_code");
        new FinishRefresh(getMy()).execute();
    }


    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;

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
                if (StrUtils.IsNotEmpty(phone)) {
                    usersList = jCloudDB.findAllByWhere(Users.class,
                            "username=" + StrUtils.QuotedStr(phone));

                    userdepts = jCloudDB.findAllByWhere(Userdept.class,
                            "username=" + StrUtils.QuotedStr(phone));

                    for (Userdept userdept : userdepts) {


                        deptTasks = jCloudDB.findAllByWhere(Task.class,
                                "principal_code = " + StrUtils.QuotedStr(friend_code) + " and principal_dept_code =" + StrUtils.QuotedStr(userdept.getDept_code()));

                        if (ListUtils.IsNotNull(deptTasks)) {
                            userdept.setAdmin_state(111111);
                        } else {
                            userdept.setAdmin_state(0);
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
            if (result.equals("1")) {
                if (ListUtils.IsNotNull(usersList)) {
                    Users users = usersList.get(0);
                    nickname.setText(users.getNickname());
                    tvPhonePhone.setText(users.getPhone());
                    tvPhone.setText(users.getUsername());
                    tvEmail.setText(users.getEmail());
                    tvArea.setText(users.getArea());
                }

                if (ListUtils.IsNotNull(userdepts)) {
                    final CommonAdapter commonAdapter = new CommonAdapter<Userdept>(getMy(), userdepts, R.layout.item_friend_detail) {
                        @Override
                        public void convert(ViewHolder helper, Userdept item) {
                            ImageView iv_task_num = helper.getImageView(R.id.iv_task_num);
                            if (item.getAdmin_state() == 111111) {
                                iv_task_num.setVisibility(View.VISIBLE);
                            } else {
                                iv_task_num.setVisibility(View.GONE);
                            }
                            helper.setText(R.id.tv_org_name, item.getOrg_name());
                            helper.setText(R.id.tv_dept_name, item.getDept_name());
                        }
                    };
                    listview.setAdapter(commonAdapter);
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Userdept userdept = (Userdept) commonAdapter.getItem(position);
                            Intent intent = new Intent(getMy(), DeptTaskListActivity.class);
                            intent.putExtra(StaticStrUtils.baseItem, userdept);
                            intent.putExtra("flag", "fb");
                            startActivity(intent);
                        }
                    });

                }

            } else {
                ToastShow("用户名或密码出错");
            }
            hideLoading();
        }
    }

    @OnClick(R.id.iv_phone)
    void callClick() {
        String number = tvPhone.getText().toString();
        if (StrUtils.IsNotEmpty(number)) {
            //用intent启动拨打电话
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            startActivity(intent);
        }

    }

    @OnClick(R.id.iv_msg)
    void msgClick() {
        String number = tvPhone.getText().toString();
        if (StrUtils.IsNotEmpty(number)) {
            Uri smsToUri = Uri.parse("smsto:" + number);

            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

            intent.putExtra("sms_body", "天天：");

            startActivity(intent);

        }

    }

    @OnClick(R.id.iv_phone_phone)
    void phonecallClick() {
        String number = tvPhonePhone.getText().toString();
        if (StrUtils.IsNotEmpty(number)) {
            //用intent启动拨打电话
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            startActivity(intent);
        }

    }

    @OnClick(R.id.iv_msg)
    void phonemsgClick() {
        String number = tvPhonePhone.getText().toString();
        if (StrUtils.IsNotEmpty(number)) {
            Uri smsToUri = Uri.parse("smsto:" + number);

            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

            intent.putExtra("sms_body", "天天：");

            startActivity(intent);

        }

    }
    /**
     * 删除组织成员
     */
    private class deleteMember extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public deleteMember(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "0";
            boolean flagUserOrg = false;
            boolean flagUserDept = false;
            List<Orgunit> listIsFounder;
            List<Userdept> listDeptFounder;
            try {

                if (friend_code.equals(userIsFounder.getUser_code())) {
                    result = "3";
                } else {
                    listDeptFounder = jCloudDB.findAllByWhere(Userdept.class, " org_code = " + StrUtils.QuotedStr(orgCode) + " and user_code = " + StrUtils.QuotedStr(userIsFounder.getUser_code()) + " and admin_state = 1 ");
                    listIsFounder = jCloudDB.findAllByWhere(Orgunit.class, " org_code = " + StrUtils.QuotedStr(orgCode) + " and founder_code = " + StrUtils.QuotedStr(userIsFounder.getUser_code()));

                    if (ListUtils.IsNotNull(listIsFounder) || ListUtils.IsNotNull(listDeptFounder)) {
                        if ("DeptUsers".equals(flag)) {
                            //flagUserOrg = jCloudDB.deleteByWhere(Userorg.class, " org_code = " + StrUtils.QuotedStr(orgCode) + " and user_code = " + StrUtils.QuotedStr(friend_code));
                            flagUserDept = jCloudDB.deleteByWhere(Userdept.class, " org_code = " + StrUtils.QuotedStr(orgCode) + "and user_code = " + StrUtils.QuotedStr(friend_code) + " and dept_code = " + StrUtils.QuotedStr(dept_code));
                            if (flagUserDept) {
                                result = "1";
                            } else {
                                result = "0";
                            }
                        } else if ("OrgMembers".equals(flag)) {
                            flagUserOrg = jCloudDB.deleteByWhere(Userorg.class, " org_code = " + StrUtils.QuotedStr(orgCode) + " and user_code = " + StrUtils.QuotedStr(friend_code));
                            flagUserDept = jCloudDB.deleteByWhere(Userdept.class, " org_code = " + StrUtils.QuotedStr(orgCode) + "and user_code = " + StrUtils.QuotedStr(friend_code));
                            if (flagUserDept || flagUserOrg) {
                                result = "1";
                            } else {
                                result = "0";
                            }
                        }
                    } else {
                        result = "2";
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
            if ("1".equals(result)) {
                ToastShow("删除成功");
                OttUtils.push("deptUsers_refresh", "");
            } else if ("0".equals(result)) {
                ToastShow("删除失败");
            } else if ("2".equals(result)) {
                ToastShow("您没有权限踢人");
            } else if ("3".equals(result)) {
                ToastShow("您不能移除自己");
            }
            hideLoading();
            finish();
            OttUtils.push("OrgMembersRefreash", "");
        }
    }


    /**
     * 保存到数据库
     */
    private class FinishRefreshDelFriends extends AsyncTask<String, Void, String> {
        private Context context;

        /**
         * @param context 上下文
         */
        public FinishRefreshDelFriends(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "0";
            boolean flag1 = false;
            boolean flag2 = false;
            JCloudDB jCloudDB = new JCloudDB();
            try {
                if (userIsFounder != null) {

                    flag1 = jCloudDB.deleteByWhere(Friend.class, " user_code = " + StrUtils.QuotedStr(userIsFounder.getUser_code()) + " and friend_code = " + StrUtils.QuotedStr(friend_code));
                    flag2 = jCloudDB.deleteByWhere(Friend.class, " user_code = " + StrUtils.QuotedStr(friend_code) + " and friend_code = " + StrUtils.QuotedStr(userIsFounder.getUser_code()));

                    if (flag1 && flag2) {
                        result = "1";
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
            if (result.equals("1")) {
                ToastShow("删除好友成功");
                finish();
                OttUtils.push("friend_del_refresh", "");
            } else {
                ToastShow("删除好友失败");
            }
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

    /**
     * 去除多余元素
     *
     * @param list
     */
    public void removeDuplicate(List<Userorg> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getOrg_code().equals(list.get(i).getOrg_code())) {
                    list.remove(j);
                }
            }
        }
    }
}
