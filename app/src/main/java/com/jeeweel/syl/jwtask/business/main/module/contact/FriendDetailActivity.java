package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import java.util.List;

import api.view.ListNoScrollView;
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

    private List<Users> usersList;

    private List<Userorg> userorgs;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        ButterKnife.bind(this);
        setTitle("用户信息");
        getData();
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
}
