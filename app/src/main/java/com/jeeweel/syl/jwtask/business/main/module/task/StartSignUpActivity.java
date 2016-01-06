package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Sign;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.basic.GetUserPicture;
import com.jeeweel.syl.jwtask.business.main.module.contact.DeptSelectFriendListActivity;
import com.jeeweel.syl.lib.api.config.publicjsonclass.ResMsgItem;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwutil.DateHelper;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import api.adapter.SignAdapter;
import api.util.Contants;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ragn on 2015/11/27.
 */
public class StartSignUpActivity extends JwActivity {
    @Bind(R.id.tv_username)
    TextView tvUserName;
    @Bind(R.id.tv_week)
    TextView tvWeek;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_context)
    EditText etContext;
    @Bind(R.id.grid_view_start)
    GridView gridViewStart;

    String userPic;
    String userName;
    String userPhone;
    String tvStartWeek;
    String tvStartDate;
    String tvStartTime;
    String etStartTitle;
    String etStartContext;
    String sign_code;
    String userNick;
    String buddyCode;
    List<Friend> friendList = new ArrayList<Friend>();
    // private CommonAdapter commonAdapter;
    int dayOfWeek;
    Users user;
    Sign sign;
    Friend friend;
    JCloudDB jCloudDB;
    SignAdapter signAdapter;
    String fName;
    @Bind(R.id.iv_user_pic)
    ImageView ivUserPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_sign_up);
        ButterKnife.bind(this);
        setTitle("发起签到");
        initDate();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @OnClick(R.id.start_sign_button)
    void startSign() {
        buddyCode = saveFcode();
        fName = saveFname();
        etStartTitle = etTitle.getText().toString();
        etStartContext = etContext.getText().toString();
        if (StrUtils.IsNotEmpty(etStartTitle) && StrUtils.IsNotEmpty(etStartContext)) {
            sign.setSign_title(etStartTitle);
            sign.setSend_context(etStartContext);
            sign.setRead_state("0");
            sign.setReceive_name(fName);
            sign.setReceive_code(buddyCode);
            new saveSignInformaiton(getMy()).execute();
        } else {
            ToastShow("内容或标题不能为空");
        }
    }

    private void initView() {
        friend = new Friend();
        friend.setFriend_nickname("123");
        friend.setContent("1");
        friendList.add(friend);

        signAdapter = new SignAdapter(StartSignUpActivity.this, friendList);
        gridViewStart.setAdapter(signAdapter);
        gridViewStart.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long l) {
                if (postion == friendList.size() - 1) {
                    JwStartActivity(PublicyContactHomeActivity.class, Contants.fzr);
                }
            }
        });
    }


    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (StrUtils.IsNotEmpty(msg) && msg.equals(Contants.fzr)) {
            String json = activityMsgEvent.getParam1();
            if (StrUtils.IsNotEmpty(json)) {
                List<Userdept> userdepts = JwJSONUtils.getParseArray(json, Userdept.class);
                List<Friend> friends = new ArrayList<Friend>();
                for (Userdept userdept : userdepts) {
                    Friend friend = new Friend();
                    friend.setUser_code(userdept.getUser_code());
                    friend.setFriend_code(userdept.getUser_code());
                    friend.setFriend_nickname(userdept.getNickname());
                    friend.setPhoto_code(userdept.getPhoto_code());
                    friends.add(friend);
                }
                friendList.clear();
                friendList.addAll(friends);
                Friend newfriend = new Friend();
                newfriend.setContent("1");
                newfriend.setFriend_nickname("123");
                friendList.add(newfriend);
                signAdapter.notifyDataSetChanged();
            }else{
                json = activityMsgEvent.getParam();
                if (StrUtils.IsNotEmpty(json)) {
                    List<Friend> friends = JwJSONUtils.getParseArray(json, Friend.class);
                    friendList.clear();
                    friendList.addAll(friends);
                    Friend newfriend = new Friend();
                    newfriend.setContent("1");
                    newfriend.setFriend_nickname("123");
                    friendList.add(newfriend);
                    signAdapter.notifyDataSetChanged();
                }
            }
        } else if (StrUtils.IsNotEmpty(msg) && msg.equals(Contants.start_sign_org)) {
            String json = activityMsgEvent.getParam1();
            if (StrUtils.IsNotEmpty(json)) {
                List<Userdept> userdepts = JwJSONUtils.getParseArray(json, Userdept.class);
                List<Friend> friends = new ArrayList<Friend>();
                for (Userdept userdept : userdepts) {
                    Friend friend = new Friend();
                    friend.setUser_code(userdept.getUser_code());
                    friend.setFriend_code(userdept.getUser_code());
                    friend.setFriend_nickname(userdept.getNickname());
                    friend.setPhoto_code(userdept.getPhoto_code());
                    friends.add(friend);
                }
                friendList.clear();
                friendList.addAll(friends);
                Friend newfriend = new Friend();
                newfriend.setContent("1");
                newfriend.setFriend_nickname("123");
                friendList.add(newfriend);
                signAdapter.notifyDataSetChanged();
            }
        }

    }

    //日期初始化
    private void initDate() {
        sign = new Sign();
        sign_code = Utils.getUUid();
        sign.setSign_code(sign_code);
        List<Users> list = JwAppAplication.getInstance().finalDb.findAll(Users.class);
        if (list != null && list.size() > 0) {
            user = list.get(0);
            new GetUserPicture(getMy(), ivUserPic, user.getUser_code()).execute();

            userNick = user.getNickname();
            userPhone = user.getUsername();
            if (StrUtils.IsNotEmpty(userNick)) {
                if (userNick.length() > 2) {
                    userPic = userNick.substring(userNick.length() - 2, userNick.length());
                } else {
                    userPic = userNick;
                }
            } else {
                userPic = "姓名";
            }
            userName = StrUtils.IfNull(userNick, userPhone);
            tvUserName.setText(userName);

            sign.setProuser_name(userName);
            sign.setProuser_code(user.getUser_code());
        }


        Date date = new Date();
        dayOfWeek = DateHelper.getDayOfWeek(date) - 1;
        tvStartTime = DateHelper.getCurrentTime();
        tvStartDate = DateHelper.getCurrentDate();
        tvStartTime = tvStartTime.substring(0, 5);
        switch (dayOfWeek) {
            case 0:
                tvStartWeek = "星期日";
                break;
            case 1:
                tvStartWeek = "星期一";
                break;
            case 2:
                tvStartWeek = "星期二";
                break;
            case 3:
                tvStartWeek = "星期三";
                break;
            case 4:
                tvStartWeek = "星期四";
                break;
            case 5:
                tvStartWeek = "星期五";
                break;
            case 6:
                tvStartWeek = "星期六";
                break;
            default:
                break;
        }
        tvWeek.setText(tvStartWeek + ":");
        tvDate.setText(tvStartDate);
        tvTime.setText(tvStartTime);
    }


    private class saveSignInformaiton extends AsyncTask<String, Void, String> {
        private Context context;
        String result = "1";

        /**
         * @param context 上下文
         */
        public saveSignInformaiton(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            jCloudDB = new JCloudDB();
            try {
                jCloudDB.save(sign);
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                pushData();
                ToastShow("发起签到成功");
            }
        }
    }


    private String saveFcode() {
        String fCode = "";
        for (int i = 0; i < friendList.size() - 1; i++) {
            fCode = fCode + friendList.get(i).getFriend_code() + ",";
        }
        if (StrUtils.IsNotEmpty(fCode)) {
            fCode = fCode.substring(0, fCode.length() - 1);
        }
        return fCode;
    }

    private String saveFname() {
        String fName = "";
        for (int i = 0; i < friendList.size() - 1; i++) {
            fName = fName + friendList.get(i).getFriend_name() + ",";
        }
        if (StrUtils.IsNotEmpty(fName)) {
            fName = fName.substring(0, fName.length() - 1);
        }
        return fName;
    }


    public void pushData() {
        if (sign != null) {
            String title = sign.getSign_title();
            String content = sign.getSend_context();

            String all_code = sign.getReceive_code();

            if (StrUtils.IsNotEmpty(title) && StrUtils.IsNotEmpty(content)) {
                try {
                    title = URLEncoder.encode(URLEncoder.encode(title, "utf-8"), "utf-8");
                    content = URLEncoder.encode(URLEncoder.encode(content, "utf-8"), "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String param = "?all_code=" + all_code + "&title=" + title + "&content=" + content;
            String apiStr = Utils.getPublicUrl() + param;
            JwHttpGet(apiStr, true);
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
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
