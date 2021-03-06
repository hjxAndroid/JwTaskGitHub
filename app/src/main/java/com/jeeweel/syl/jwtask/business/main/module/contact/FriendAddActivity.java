package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.basic.JwCaptureActivity;
import com.jeeweel.syl.lib.api.config.ApiUrlUtil;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.config.publicjsonclass.ResMsgItem;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.integer.IntUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.o.OUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.net.URLEncoder;
import java.util.List;

import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendAddActivity extends JwActivity {


    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.bt_add)
    Button btAdd;

    String usercode;

    private int scan_code = 1;
    private String friendphone;
    String friendCode = "";
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);
        setTitle("添加好友");
        ButterKnife.bind(this);
        initRight();
        users = JwAppAplication.getInstance().getUsers();
    }


    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("扫一扫");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(FriendAddActivity.this, JwCaptureActivity.class);
                startActivityForResult(intent, scan_code);
            }
        });
        addMenuView(menuTextView);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == scan_code) {
            if (OUtils.IsNotNull(intent)) {
                friendphone = intent.getStringExtra("SCAN_RESULT");
                if (StrUtils.IsNotEmpty(friendphone)) {
                    save(friendphone);
                }
            }
        }

    }

    @OnClick(R.id.bt_add)
    void nextClick() {
        String friendPhone = etPhone.getText().toString();
        save(friendPhone);
    }

    private void save(String friendPhone) {
        if (null!=users) {
            String nickname = users.getNickname();
            usercode = users.getUser_code();
            String myphone = users.getUsername();
            if (StrUtils.IsNotEmpty(friendPhone) && StrUtils.IsNotEmpty(nickname)) {
                showLoading();
                new FinishRefresh(getMy()).execute(nickname, myphone, friendPhone);
            } else {
                ToastShow("您要先完善信息，才能添加好友");
            }
        }
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

            String result = "0";

            String unid = Utils.getUUid();
            String nickname = params[0].toString();
            String myPhone = params[1].toString();

            String friendPhone = params[2].toString();

            //先判断是否已经添加了好友
            List<Friend> friendsList = null;
            try {
                friendsList = jCloudDB.findAllByWhere(Friend.class,
                        "user_name = " + myPhone + " and friend_name=" + StrUtils.QuotedStr(friendPhone));
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

    @Override
    public void HttpSuccess(ResMsgItem resMsgItem) {
        finish();
    }

    @Override
    public void HttpFail(String strMsg) {
        finish();
        super.HttpFail(strMsg);
    }

    @Override
    public void HttpFinish() {
        finish();
        super.HttpFinish();
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

