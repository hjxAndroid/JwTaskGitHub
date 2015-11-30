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
            String myphone = users.getUsername();
            String friendPhone = etPhone.getText().toString();
            if (StrUtils.IsNotEmpty(friendPhone)) {
                  new FinishRefresh(getMy()).execute(myphone,friendPhone);
            }
        }
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

            String result = "0";

            String unid = Utils.getUUid();
            String myPhone = params[0].toString();
            String friendPhone = params[1].toString();

            //添加到自己为主体的好友表
            Friend myself = new Friend();
            myself.setUnid_code(unid);
            myself.setUser_name(myPhone);
            myself.setFriend_name(friendPhone);
            myself.setState(getString(R.string.sended));
            JCloudDB jCloudDB = new JCloudDB();

            if (jCloudDB.save(myself)) {
                //添加到好友为主体的好友表
                Friend friend = new Friend();
                friend.setUnid_code(unid);
                friend.setUser_name(friendPhone);
                friend.setFriend_name(myPhone);
                friend.setState(getString(R.string.sended));
                if (jCloudDB.save(friend)) {
                    result = "1";
                }
            }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                ToastShow("添加好友请求已发出");
                finish();
            } else {
                ToastShow("好友保存出错");
            }
            hideLoading();
        }
    }
}
