package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Sign;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Signed;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.o.OUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ragn on 2015/12/17.
 */
public class CheckSignPersonActivity extends JwActivity {
    @Bind(R.id.already_signed_counts)
    TextView alreadySignedCounts;
    @Bind(R.id.unsign_counts)
    TextView unsignCounts;


    private String signedCode;
    private List<Signed> listSigned;
    private int alreadySigned;
    private int unSign;
    private int sum;
    private String[] sumReceiver;
    private List<Sign> listSign;
    private String tvAlreadySigned;
    private Sign sign;
    private String tvUnsign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sign_person);
        ButterKnife.bind(this);
        setTitle("签到人数");
        getData();
    }


    private void getData() {
        showLoading();
        signedCode = getIntent().getStringExtra(StaticStrUtils.baseItem);
        new FinishRefresh(getMy()).execute();
    }

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
            SqlInfo sqlInfo = new SqlInfo();
            sqlInfo.setSql("SELECT " +
                    " sign_code, " +
                    " sign_user_code " +
                    " FROM " +
                    " signed " +
                    " where sign_code= ? " +
                    " GROUP BY " +
                    " sign_code, " +
                    " sign_user_code ");
            sqlInfo.addValue(signedCode);
            String sql = sqlInfo.getBuildSql();
            try {


                listSigned = jCloudDB.findAllBySql(Signed.class, sql);
                if (ListUtils.IsNotNull(listSigned)) {
                    alreadySigned = listSigned.size();
                    tvAlreadySigned = "" + alreadySigned;
                } else {
                    tvAlreadySigned = "0";
                }


                listSign = jCloudDB.findAllByWhere(Sign.class,
                        " sign_code = " +
                                StrUtils.QuotedStr(signedCode));
                if (ListUtils.IsNotNull(listSign)) {
                    sign = listSign.get(0);
                    String receiceCode = sign.getReceive_code();
                    if (receiceCode.contains(",")) {
                        sumReceiver = receiceCode.split(",");
                        sum = sumReceiver.length;
                        unSign = sum - alreadySigned;
                        tvUnsign = "" + unSign;
                    } else {
                        sum = 1;
                        unSign = sum - alreadySigned;
                        tvUnsign = "" + unSign;
                    }
                }
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            alreadySignedCounts.setText(tvAlreadySigned);
            unsignCounts.setText(tvUnsign);
            hideLoading();
        }
    }
}
