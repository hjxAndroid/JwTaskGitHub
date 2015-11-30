package com.jeeweel.syl.jwtask.business.main.module.news;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.contact.FriendAddListActivity;
import com.jeeweel.syl.jwtask.business.main.module.contact.FriendListActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.DateHelper;

import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_home);
        ButterKnife.bind(this);
        setTitle(getString(R.string.news));
        getData();
    }

    private void getData() {
        String time = DateHelper.getHourAndM();
        tvFriendTime.setText(time);
        tvTaskTime.setText(time);
        tvSignTime.setText(time);
        tvPulicyTime.setText(time);

        Users users = JwAppAplication.getInstance().users;
        if (null != users) {
            String phone = users.getUsername();
            new FinishRefresh(getMy()).execute(phone);
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
            //请求好友
            friendList = jCloudDB.findAllByWhere(Friend.class,
                    "user_name=" + StrUtils.QuotedStr(phone) + " ORDER BY create_time DESC");

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                //刷新好友列表
                if (ListUtils.IsNotNull(friendList)) {
                    Friend friend = friendList.get(0);
                    String friendNickname = friend.getFriend_nickname();
                    int state = friend.getSend_state();
                    //0为接受状态  1为发送状态不展示
                    if(state==0){
                        if (StrUtils.IsNotEmpty(friendNickname)) {
                            rlFriendNews.setText(friendNickname + "请求添加您为好友");
                            ivFriendNum.setVisibility(View.VISIBLE);
                        }
                    }
                } else {

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
}
