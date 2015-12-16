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
    }


    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("扫一扫");
        menuTextView.setTextColor(getResources().getColor(R.color.white));
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

    private void save(String friendPhone){
        List<Users> usersList = JwAppAplication.getInstance().finalDb.findAll(Users.class);
        if (ListUtils.IsNotNull(usersList)) {
            users = usersList.get(0);
            String nickname = users.getNickname();
            usercode = users.getUser_code();
            String myphone = users.getUsername();
            if (StrUtils.IsNotEmpty(friendPhone)&&StrUtils.IsNotEmpty(nickname)) {
                showLoading();
                new FinishRefresh(getMy()).execute(nickname, myphone, friendPhone);
            }else{
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

            List<Users> friendsList = null;
            try {
                friendsList = jCloudDB.findAllByWhere(Users.class,
                        "username=" + StrUtils.QuotedStr(friendPhone));

                if(null!=friendsList){
                    friendCode = friendsList.get(0).getUser_code();
                }

            } catch (CloudServiceException e) {
                e.printStackTrace();
            }


            //先判断好友是否存在
            List<Users> list = null;
            try {
                list = jCloudDB.findAllByWhere(Users.class,
                        "username=" + StrUtils.QuotedStr(friendPhone));
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

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                pushData();
                ToastShow("添加好友请求已发出");
            } else if (result.equals("2")) {
                ToastShow("好友不存在");
            } else {
                ToastShow("好友保存出错");
            }
            hideLoading();
        }
    }


    public void pushData() {
        if(users!=null){
            if(StrUtils.IsNotEmpty(friendCode)){
               // users.getNickname()+"请求添加您未好友"
                String title = users.getNickname();
                try {
                    title = URLEncoder.encode(title,"utf-8");
                    title = URLEncoder.encode(title,"utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String content = users.getNickname()+"请求添加您未好友";
                try {
                    content = URLEncoder.encode(content,"utf-8");
                    content = URLEncoder.encode(content,"utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String param = "?user_code=" + friendCode+"&title="+title+"&content="+content;
                String apiStr = Utils.getPushUrl()+param;
                JwHttpGet(apiStr, true);
            }
        }
    }

    @Override
    public void HttpSuccess(ResMsgItem resMsgItem) {
        if (OUtils.IsNotNull(resMsgItem)) {
            if (resMsgItem != null) {
                int error = resMsgItem.getStatus();
                String sMsg = resMsgItem.getMsg();
                if (error == 1 || error == 99) {
                    CroutonINFO(sMsg);
                } else {
                }
            }
            finish();
        }
    }

    @Override
    public void HttpFail(String strMsg) {
        super.HttpFail(strMsg);
    }

    @Override
    public void HttpFinish() {
        super.HttpFinish();
    }
}
