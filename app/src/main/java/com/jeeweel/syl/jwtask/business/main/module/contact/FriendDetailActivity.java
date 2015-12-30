package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.ActionItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.basic.GetUserPicture;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.net.URLEncoder;
import java.util.List;

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


    private List<Users> usersList;

    private List<Userorg> userorgs;
    private String phone;

    String friendCode = "";
    Users users;
    String usercode;

    TitlePopup titlePopup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        ButterKnife.bind(this);
        setTitle("用户信息");
        getData();
        Intent intent = getIntent();
        boolean flag = intent.getBooleanExtra("flag", false);
        if (flag == true) {
            initView();
        }
        String friend_code = intent.getStringExtra("friend_code");
        if(StrUtils.IsNotEmpty(friend_code)){
            new GetUserPicture(getMy(), iv, friend_code).execute();
        }
    }

    private void initView() {
        titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ActionItem action = new ActionItem(getResources().getDrawable(R.drawable.a0),"添加");
        ActionItem action1 = new ActionItem(getResources().getDrawable(R.drawable.a0),"解散");
        titlePopup.addAction(action);
        titlePopup.addAction(action1);

        MenuImageView menuImageView = new MenuImageView(getMy());
        menuImageView.setBackgroundResource(R.drawable.more);
        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titlePopup.show(v);
            }
        });
        addMenuView(menuImageView);

//        MenuTextView menuTextView = new MenuTextView(getMy());
//        menuTextView.setText("加为好友");
//        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
//        menuTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                titlePopup.show(v);
//                //showAlertDialog();
//            }
//        });
//        addMenuView(menuTextView);
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
                new android.content.DialogInterface.OnClickListener() {
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

                    userorgs = jCloudDB.findAllByWhere(Userorg.class,
                            "user_name=" + StrUtils.QuotedStr(phone));

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
                    tvPhone.setText(users.getUsername());
                    tvEmail.setText(users.getEmail());
                    tvArea.setText(users.getArea());
                }

                if (ListUtils.IsNotNull(userorgs)) {
                    CommonAdapter commonAdapter = new CommonAdapter<Userorg>(getMy(), userorgs, R.layout.item_friend_detail) {
                        @Override
                        public void convert(ViewHolder helper, Userorg item) {
                            helper.setText(R.id.tv_org_name, item.getOrg_name());
                        }
                    };
                    listview.setAdapter(commonAdapter);
                }

            } else {
                ToastShow("用户名或密码出错");
            }
            hideLoading();
        }
    }


    @OnClick(R.id.li_phone)
    void callClick() {
        String number = tvPhone.getText().toString();
        if (StrUtils.IsNotEmpty(number)) {
            //用intent启动拨打电话
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            startActivity(intent);
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
