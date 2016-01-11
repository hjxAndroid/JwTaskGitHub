package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Sign;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Signed;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import api.util.OttUtils;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ragn on 2015/12/4.
 */
public class SignListActivity extends JwListActivity {
    List<Sign> mListItems = new ArrayList<Sign>();
    @Bind(R.id.listview)
    PullToRefreshListView listview;


    private CommonAdapter commonAdapter;
    private String isUserCode;
    String[] sCodes;
    String buddyCode;
    List<Signed> listSigned;
    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<Sign> list;

    private Users users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_list_view);
        ButterKnife.bind(this);
        setTitle("签到列表");
        users = JwAppAplication.getInstance().users;
        initListViewController();
    }


    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<Sign>(getMy(), mListItems, R.layout.item_sign_information) {
            @Override
            public void convert(ViewHolder helper, Sign item) {
                String sign_code = item.getSign_code();
                ImageView imageView = helper.getImageView(R.id.iv_xz);
                JwImageLoader.displayImage(Utils.getPicUrl() + item.getPhoto_code(), imageView);

                buddyCode = item.getReceive_code();
                if (buddyCode.contains(",")) {
                    sCodes = buddyCode.split(",");
                    for (int i = 0; i < sCodes.length; i++) {
                        isUserCode = sCodes[i];
                        if (isUserCode.equals(users.getUser_code())) {
                            helper.setText(R.id.tv_sign_title, item.getSign_title());
                            helper.setText(R.id.tv_name, item.getProuser_name());
                            helper.setText(R.id.tv_sign_time, item.getCreate_time().substring(0, 16));
                        }
                    }
                } else {
                    isUserCode = buddyCode;
                    if (isUserCode.equals(users.getUser_code())) {
                        helper.setText(R.id.tv_sign_title, item.getSign_title());
                        helper.setText(R.id.tv_name, item.getProuser_name());
                        helper.setText(R.id.tv_sign_time, item.getCreate_time().substring(0, 16));
                    }
                }
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }

    @Override
    public void onListItemClick(int position) {
        Sign sign = (Sign) commonAdapter.getItem(position);
        JwStartActivity(SignUpActivity.class, sign.getSign_code());
    }

    @Override
    public void onListViewHeadRefresh() {
        showLoading();
        new FinishRefresh(getMy(), 0).execute();
    }

    @Override
    public void onListViewFooterRefresh() {
        showLoading();
        new FinishRefresh(getMy(), 1).execute();
    }

    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private int mode = 0;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context, int mode) {
            this.context = context;
            this.mode = mode;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "0";

            if (null != users) {
                try {
                    if (mode == 0) {
                        setPage(true);
                        list = jCloudDB.findAllByWhere(Sign.class,
                                "receive_code like"
                                        + StrUtils.QuotedStrLike(users.getUser_code())
                                        + " ORDER BY create_time DESC " +
                                        " limit " +
                                        pageStart +
                                        "," +
                                        pageEnd);
                        mListItems.clear();
                    } else {
                        setPage(false);
                        list = jCloudDB.findAllByWhere(Sign.class,
                                "receive_code like" +
                                        StrUtils.QuotedStrLike(users.getUser_code()) +
                                        " ORDER BY create_time DESC " +
                                        " limit " +
                                        pageStart +
                                        "," +
                                        pageEnd);
                    }
                    if (ListUtils.IsNotNull(list)) {
                        result = "1";
                        for (Sign sign : list) {
                            //取头像
                            String prouser_code = sign.getProuser_code();

                            String sSql = "pic_code=?";
                            SqlInfo sqlInfo = new SqlInfo();
                            sqlInfo.setSql(sSql);
                            sqlInfo.addValue(prouser_code);
                            sSql = sqlInfo.getBuildSql();
                            List<Picture> pictureList = jCloudDB.findAllByWhere(Picture.class, sSql);
                            if (ListUtils.IsNotNull(pictureList)) {
                                Picture picture = pictureList.get(0);
                                String path = picture.getPic_road();
                                if (StrUtils.IsNotEmpty(path)) {
                                    //存头像
                                    sign.setPhoto_code(path);
                                }
                            }
                        }
//                        for (int i = 0; i < list.size(); i++) {
//                            Sign sign = list.get(i);
//                            List<Signed> listSigned = jCloudDB.findAllBySql(Signed.class, " sign_code = " +
//                                    StrUtils.QuotedStr(sign.getSign_code()) +
//                                    " and sign_user_code = " + StrUtils.QuotedStr(users.getUser_code()));
//                        }


                    } else {
                        result = "0";
                    }
                } catch (CloudServiceException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                mListItems.addAll(list);
                commonAdapter.notifyDataSetChanged();
            }
            listview.onRefreshComplete();
            hideLoading();
        }
    }

    private void setPage(boolean tag) {
        if (tag) {
            pageStart = 0;
            pageEnd = 10;
        } else {
            pageStart += addNum;
            pageEnd += addNum;
        }
    }


    private class FinishRefreshIsSigned extends AsyncTask<String, Void, String> {
        private Context context;
        private String sign_code;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefreshIsSigned(Context context, String sign_code) {
            this.context = context;
            this.sign_code = sign_code;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "0";
            try {
                listSigned = jCloudDB.findAllBySql(Signed.class, " sign_code = " +
                        StrUtils.QuotedStr(sign_code) +
                        " and sign_user_code = " + StrUtils.QuotedStr(users.getUser_code()));
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    private class FinishRefreshChangeState extends AsyncTask<String, Void, String> {
        private Context context;

        /**
         * @param context 上下文
         */
        public FinishRefreshChangeState(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "0";

            String sql = "update sign set read_state = '1' where receive_code =" + "\'" + users.getUser_code() + "\'";
            try {
                CloudDB.execSQL(sql);
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            OttUtils.push("news_refresh", "");
        }
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

