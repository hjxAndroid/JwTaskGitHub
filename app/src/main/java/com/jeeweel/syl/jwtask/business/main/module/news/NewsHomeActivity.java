package com.jeeweel.syl.jwtask.business.main.module.news;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.contact.FriendAddListActivity;
import com.jeeweel.syl.jwtask.business.main.module.contact.FriendListActivity;
import com.jeeweel.syl.jwtask.business.main.module.task.PublicyListActivity;
import com.jeeweel.syl.jwtask.business.main.module.task.SignListActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.DateHelper;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;

import java.util.List;

import api.util.Contants;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewsHomeActivity extends JwActivity {

    @Bind(R.id.tv_publicy_news)
    TextView tvPublicyNews;
    @Bind(R.id.rl_publicy)
    RelativeLayout rlPublicy;
    @Bind(R.id.tv_sign_news)
    TextView tvSignNews;
    @Bind(R.id.rl_sign)
    RelativeLayout rlSign;
    @Bind(R.id.tv_task_news)
    TextView tvTaskNews;
    @Bind(R.id.rl_task)
    RelativeLayout rlTask;
    @Bind(R.id.rl_friend_news)
    TextView rlFriendNews;
    @Bind(R.id.rl_friend)
    RelativeLayout rlFriend;
    @Bind(R.id.tv_pulicy_time)
    TextView tvPulicyTime;
    @Bind(R.id.iv_publicy_num)
    ImageView ivPublicyNum;
    @Bind(R.id.tv_sign_time)
    TextView tvSignTime;
    @Bind(R.id.iv_sign_num)
    ImageView ivSignNum;
    @Bind(R.id.tv_task_time)
    TextView tvTaskTime;
    @Bind(R.id.iv_task_num)
    ImageView ivTaskNum;
    @Bind(R.id.tv_friend_time)
    TextView tvFriendTime;
    @Bind(R.id.iv_friend_num)
    ImageView ivFriendNum;

    private List<Friend> friendList;

    private String myphone;

    private String orgCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_home);
        ButterKnife.bind(this);
        setTitle(getString(R.string.news));
        getData();
    }

    private void getData() {
        String time = Utils.getHourAndM();
        tvFriendTime.setText(time);
        tvTaskTime.setText(time);
        tvSignTime.setText(time);
        tvPulicyTime.setText(time);

        Users users = JwAppAplication.getInstance().getUsers();

        orgCode = (String)SharedPreferencesUtils.get(getMy(), Contants.org_code,"");

        if (null != users) {
            myphone = users.getUsername();
            showLoading();
            new FinishRefresh(getMy()).execute(myphone);
        }
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        JCloudDB jCloudDB;

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

            String phone = params[0].toString();

            try {

                if(StrUtils.isEmpty(orgCode)){
                    //取默认组织
                    List<Userorg> userorgs = jCloudDB.findAllByWhere(Userorg.class,
                            "user_name=" + StrUtils.QuotedStr(phone) + "ORDER BY create_time");
                    if(ListUtils.IsNotNull(userorgs)){
                        SharedPreferencesUtils.save(getMy(),Contants.org_code,userorgs.get(0).getOrg_code());
                        SharedPreferencesUtils.save(getMy(),Contants.org_name,userorgs.get(0).getOrg_name());
                    }
                }

                //请求好友
                friendList = jCloudDB.findAllByWhere(Friend.class,
                        "user_name=" + StrUtils.QuotedStr(phone) + "and read_state=0 " + "ORDER BY create_time DESC");
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                //刷新好友列表
                if (ListUtils.IsNotNull(friendList)) {
                    for (Friend friend : friendList) {
                        int sendState = friend.getSend_state();
                        int state = friend.getState();
                        //假如是接受者，提醒好友添加
                        if (sendState == 0) {
                            //假如是未操作
                            if (state == 0) {
                                rlFriendNews.setText(friend.getFriend_nickname() + "请求添加您为好友");
                                ivFriendNum.setVisibility(View.VISIBLE);
                                break;
                            }
                            //假如是发送者，提醒好友通过
                        } else if (sendState == 1) {
                            //假如是已经同意，提醒好友添加成功
                            if (state == 2) {
                                rlFriendNews.setText("您已经成功添加" + friend.getFriend_nickname() + "为好友");
                                ivFriendNum.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                } else {
                    rlFriendNews.setText("暂无消息");
                    ivFriendNum.setVisibility(View.GONE);
                }
            } else {

            }
            hideLoading();
        }
    }


    @OnClick(R.id.rl_friend)
    void friendClick() {
        JwStartActivity(FriendAddListActivity.class);
    }

    @OnClick(R.id.rl_sign)
    void signInformationClick() {
        JwStartActivity(SignListActivity.class);
    }

    @OnClick(R.id.rl_publicy)
    void publicyClick() {
        JwStartActivity(PublicyListActivity.class);
    }

    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (msg.equals("news_refresh")) {
            new FinishRefresh(getMy()).execute(myphone);
        }
    }
}
