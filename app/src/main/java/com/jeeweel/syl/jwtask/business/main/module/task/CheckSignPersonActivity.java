package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.callback.FindAllListener;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Sign;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Signed;
import com.jeeweel.syl.jwtask.business.config.jsonclass.V_sign_users;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ragn on 2015/12/17.
 */
public class CheckSignPersonActivity extends JwListActivity {
    @Bind(R.id.already_signed_change_counts)
    TextView alreadySignedCounts;
    @Bind(R.id.unsign_change_counts)
    TextView unsignCounts;
    @Bind(R.id.listview)
    PullToRefreshListView listview;


    List<Signed> mListItems = new ArrayList<Signed>();
    private CommonAdapter commonAdapter;
    private String signedCode;
    private List<Signed> listSigned;
    private int alreadySigned;
    private int unSign;
    private int sum;
    private String[] sumReceiver;
    private List<Sign> listSign;
    private String tvAlreadySigned;
    private String tvUnsign;
    private String signNickName;
    private String signTime;
    private String signLoc;
    private String flag = "0";

    private String alreadySignName;
    private String alreadySignNameSum;
    private String unSignNameSum;
    List<Signed> list;
    List<V_sign_users> listSum;
    List<Signed> listRemain;
    List<Signed> handadd;
    V_sign_users v_sign_nickname;
    Signed signed;
    Sign sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sign_person);
        ButterKnife.bind(this);
        setTitle("签到人数");
        getData();
        initListViewController();
    }


    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<Signed>(getMy(), mListItems, R.layout.item_sign_person_detail) {
            @Override
            public void convert(ViewHolder helper, Signed item) {

                alreadySignName = item.getNickname();
                alreadySignNameSum += alreadySignName + ",";
                if (StrUtils.IsNotEmpty(item.getNickname())) {
                    helper.setText(R.id.tv_check_user_name, item.getNickname());
                } else {
                    helper.setText(R.id.tv_check_user_name, "姓名");
                }
                if (StrUtils.IsNotEmpty(item.getCreate_time())) {
                    signTime = item.getCreate_time().substring(0, 16);
                    helper.setText(R.id.tv_check_time, signTime);
                } else {
                    helper.setText(R.id.tv_check_time, "未签到，时间未知");
                }
                if (StrUtils.IsNotEmpty(item.getLocation())) {
                    helper.setText(R.id.tv_check_loc, item.getLocation());
                } else {
                    helper.setText(R.id.tv_check_loc, "未签到，地点未知");
                }
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }

    @OnClick(R.id.already_signed_change_counts)
    void wzxClick() {
        resetAll();
        alreadySignedCounts.setBackgroundResource(R.drawable.shape_blue);
        alreadySignedCounts.setTextColor(getResources().getColor(R.color.white));
        flag = "0";
        dataSourceClear(0, mListItems);
        onListViewHeadRefresh();
    }

    @OnClick(R.id.unsign_change_counts)
    void yzxClick() {
        resetAll();
        unsignCounts.setBackgroundResource(R.drawable.shape_blue);
        unsignCounts.setTextColor(getResources().getColor(R.color.white));
        flag = "1";
        dataSourceClear(0, mListItems);
        onListViewHeadRefresh();
    }

    private void getData() {
        signedCode = getIntent().getStringExtra(StaticStrUtils.baseItem);
        SqlInfo sqlInfo = new SqlInfo();
        sqlInfo.setSql("SELECT " +
                "*" +
                " FROM " +
                " signed " +
                " where sign_code= ? " +
                " GROUP BY " +
                " sign_code, " +
                " sign_user_code ");
        sqlInfo.addValue(signedCode);
        String sql = sqlInfo.getBuildSql();
        JCloudDB jCloudDB = new JCloudDB();
        jCloudDB.findAllBySql(sql, new FindAllListener<Signed>() {
            @Override
            public void onStart() {
                showLoading();
            }

            @Override
            public void onSuccess(List<Signed> t) {
                if (ListUtils.IsNotNull(t)) {
                    alreadySigned = t.size();
                    tvAlreadySigned = "" + alreadySigned;

                } else {
                    tvAlreadySigned = "0";
                }
                alreadySignedCounts.setText("已经签到" + tvAlreadySigned + "人");

            }

            @Override
            public void onFinish() {
                hideLoading();
            }
        });

        String sqlSign = " sign_code = " +
                StrUtils.QuotedStr(signedCode);
        jCloudDB.findAllByWhere(sqlSign, new FindAllListener<Sign>() {
            @Override
            public void onStart() {
                showLoading();
            }

            @Override
            public void onSuccess(List<Sign> t) {
                if (ListUtils.IsNotNull(t)) {
                    sign = t.get(0);
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
                unsignCounts.setText("未签到" + tvUnsign + "人");
            }

            @Override
            public void onFinish() {
                hideLoading();
            }
        });

        /*new FinishRefreshCounts(getMy()).execute();*/
    }


    @Override
    public void onListViewHeadRefresh() {
        showLoading();
        new FinishRefresh(getMy()).execute();
    }

    @Override
    public void onListViewFooterRefresh() {
        showLoading();
        new FinishRefresh(getMy()).execute();
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

            String result = "1";
            SqlInfo sqlInfo1 = new SqlInfo();
            sqlInfo1.setSql("SELECT " +
                    " nickname, " +
                    " create_time, " +
                    " location " +
                    " FROM " +
                    " signed " +
                    " where sign_code= ? " +
                    " GROUP BY " +
                    " sign_code, " +
                    " sign_user_code ");
            sqlInfo1.addValue(signedCode);
            String sql1 = sqlInfo1.getBuildSql();
            try {

                list = jCloudDB.findAllBySql(Signed.class, sql1);
                mListItems.clear();
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }


            SqlInfo sqlInfo2 = new SqlInfo();
            sqlInfo2.setSql("SELECT " +
                    " nickname, " +
                    " create_time " +
                    " FROM " +
                    " v_sign_users " +
                    " where sign_code= ? ");
            sqlInfo2.addValue(signedCode);
            String sql2 = sqlInfo2.getBuildSql();
            try {
                listSum = jCloudDB.findAllBySql(V_sign_users.class, sql2);
                handadd = new ArrayList<Signed>();
                for (int i = 0; i < listSum.size(); i++) {
                    v_sign_nickname = listSum.get(i);
                    signed = new Signed();
                    signed.setNickname(v_sign_nickname.getNickname());
                    signed.setCreate_time("");
                    signed.setLocation("");
                    handadd.add(signed);
                }
                listRemain = removeList(handadd, list);
                mListItems.clear();
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            if ("0".equals(flag)) {
                if (ListUtils.IsNotNull(list)) {
                    result = "1";
                } else {
                    result = "0";
                }
            } else if ("1".equals(flag)) {
                if (ListUtils.IsNotNull(listRemain)) {
                    result = "1";
                } else {
                    result = "0";
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                if ("0".equals(flag)) {
                    mListItems.addAll(list);
                    commonAdapter.notifyDataSetChanged();
                } else if ("1".equals(flag)) {
                    mListItems.addAll(listRemain);
                    commonAdapter.notifyDataSetChanged();
                }
            } else {
                commonAdapter.notifyDataSetChanged();
                //没有加载到数据
            }
            listview.onRefreshComplete();
            hideLoading();
        }
    }


    private class FinishRefreshCounts extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefreshCounts(Context context) {
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


    private void resetAll() {
        alreadySignedCounts.setBackgroundResource(R.drawable.shape_white);
        alreadySignedCounts.setTextColor(getResources().getColor(R.color.list_text_color));
        unsignCounts.setBackgroundResource(R.drawable.shape_white);
        unsignCounts.setTextColor(getResources().getColor(R.color.list_text_color));
    }

    public List<Signed> removeList(List<Signed> list1, List<Signed> list2) {
        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                if (list1.get(i).getNickname().equals(list2.get(j).getNickname())) {
                    list1.remove(i);
                }
            }
        }
        return list1;
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
