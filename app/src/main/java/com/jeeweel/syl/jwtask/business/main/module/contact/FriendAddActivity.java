package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.cengalabs.flatui.views.FlatButton;
import com.cengalabs.flatui.views.FlatEditText;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import java.util.List;

import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendAddActivity extends JwActivity {


    @Bind(R.id.et_phone)
    FlatEditText etPhone;
    @Bind(R.id.button_add)
    FlatButton buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);
        setTitle("添加好友");
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_add)
    void nextClick() {
        List<Users> usersList = JwAppAplication.getInstance().finalDb.findAll(Users.class);
        if (ListUtils.IsNotNull(usersList)) {
            Users users = usersList.get(0);
            String nickname = users.getNickname();
            String myphone = users.getUsername();
            String friendPhone = etPhone.getText().toString();
            if (StrUtils.IsNotEmpty(friendPhone)) {
                  new FinishRefresh(getMy()).execute(nickname,myphone,friendPhone);
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

            //先判断好友是否存在
            List<Users> list = jCloudDB.findAllByWhere(Users.class,
                    "username=" + StrUtils.QuotedStr(friendPhone));


            if(ListUtils.IsNotNull(list)){
                String friendNickname = list.get(0).getNickname();

                //添加到自己为主体的好友表
                Friend myself = new Friend();
                myself.setUnid_code(unid);
                myself.setUser_name(myPhone);
                myself.setUser_nickname(nickname);
                myself.setFriend_name(friendPhone);
                myself.setFriend_nickname(friendNickname);
                myself.setState(0);
                //发送状态
                myself.setSend_state(1);

                if (jCloudDB.save(myself)) {
                    //添加到好友为主体的好友表
                    Friend friend = new Friend();
                    friend.setUnid_code(unid);
                    friend.setUser_name(friendPhone);
                    friend.setUser_nickname(friendNickname);
                    friend.setFriend_name(myPhone);
                    friend.setFriend_nickname(nickname);
                    friend.setState(0);
                    //接受状态
                    myself.setSend_state(0);
                    if (jCloudDB.save(friend)) {
                        result = "1";
                    }
                }

            }else{
                result = "2";
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                ToastShow("添加好友请求已发出");
                finish();
            } else if(result.equals("2")){
                ToastShow("好友不存在");
            }else{
                ToastShow("好友保存出错");
            }
            hideLoading();
        }
    }
}
