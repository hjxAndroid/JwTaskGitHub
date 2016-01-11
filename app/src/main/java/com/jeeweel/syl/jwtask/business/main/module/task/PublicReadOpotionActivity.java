package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;

import com.google.gson.Gson;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Alreadyread;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Sign;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Signed;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.config.jsonclass.V_publicityunread;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicReadOpotionActivity extends JwActivity {

    @Bind(R.id.bt_yd)
    Button btYd;
    @Bind(R.id.bt_wd)
    Button btWd;
    List<Signed> alreadySigned;
    List<Sign> usSign;
    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_read_opotion);
        ButterKnife.bind(this);
        setTitle("签到列表");
//        users = JwAppAplication.getInstance().getUsers();
//        if (users != null) {
//            showLoading();
//            new SignPerson(getMy()).execute();
//        }
    }

    @OnClick(R.id.bt_yd)
    void ydClick() {
        JwStartActivity(AlreadySignedActivity.class);

    }

    @OnClick(R.id.bt_wd)
    void wdClick() {
        JwStartActivity(UnSignActivity.class);

    }

    /**
     * 查看总人数
     */
    private class SignPerson extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public SignPerson(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            try {
                alreadySigned = jCloudDB.findAllBySql(Signed.class, "SELECT b.* from signed b where b.sign_code in(SELECT t.sign_code from sign t where t.receive_code LIKE '%cead7d048cea4ba2b6766bc7b17df8d2%') and b.sign_user_code = " +
                        StrUtils.QuotedStr(users.getUser_code()));
                removeDuplicateSigned(alreadySigned);
                usSign = jCloudDB.findAllBySql(Sign.class, "SELECT t.* from sign t where t.sign_code not in (SELECT b.sign_code from signed b where b.sign_code in(SELECT t.sign_code from sign t where t.receive_code LIKE '%cead7d048cea4ba2b6766bc7b17df8d2%') and b.sign_user_code = " + StrUtils.QuotedStr(users.getUser_code()) + ")");
                removeDuplicateSign(usSign);
                if (ListUtils.IsNotNull(alreadySigned)) {
                    result = "1";
                } else {
                    result = "0";
                }
                if (ListUtils.IsNotNull(usSign)) {
                    result = "1";
                } else {
                    result = "0";
                }
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
        }

    }

    /**
     * 去除多余元素
     *
     * @param list
     */
    public void removeDuplicateSigned(List<Signed> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getSign_code().equals(list.get(i).getSign_code())) {
                    list.remove(j);
                }
            }
        }
    }

    /**
     * 去除多余元素
     *
     * @param list
     */
    public void removeDuplicateSign(List<Sign> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getSign_code().equals(list.get(i).getSign_code())) {
                    list.remove(j);
                }
            }
        }
    }
}
