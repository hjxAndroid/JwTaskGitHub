package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.JsonElement;
import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.jsonclass.ResMsgItem;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.LSignedCountItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Sign;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Signed;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwutil.DateHelper;
//import com.jeeweel.syl.lib.api.jwlib.baidumaps.InitLocationSign;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ragn on 2015/11/30.
 */
public class SignUpActivity extends JwActivity {

    @Bind(R.id.tv_sign_user_pic)
    TextView tvSignUserPic;
    @Bind(R.id.tv_sign_user_name)
    TextView tvSignUserName;
    @Bind(R.id.tv_sign_title_det)
    TextView tvSignTitleDet;
    @Bind(R.id.tv_sign_counts)
    TextView tvSignCounts;
    @Bind(R.id.tv_sign_week)
    TextView tvSignWeek;
    @Bind(R.id.tv_sign_date)
    TextView tvSignDate;
    @Bind(R.id.tv_sign_time)
    TextView tvSignTime;
    @Bind(R.id.tv_sign_addr)
    TextView tvSignAddr;


    String signUserPic;
    String signUserName;
    String signTitleDet;
    String signCounts;
    String signWeek;
    String signDate;
    String signTime;
    String userNick;
    String userPhone;
    String signedCode;
    String signedContext;
    String signUserCode;

    //地理位置相关字段
    String address;
    // 纬度
    Double latitude;
    // 经度
    Double longitude;


    int counts;
    Users user;
    Sign sign;
    Signed signed = new Signed();
    ResMsgItem res;
    List<Sign> list;
    LSignedCountItem lSignedCounts;
    LocationClient mLocationClient;
    MyLocationListener mMyLocationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("签到");
        ButterKnife.bind(this);
        initDate();
        initUsers();
        getData();
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mLocationClient.start();
        initLocation();
        new FinishRefreshSignedCounts(getMy()).execute();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    //日期初始化
    private void initDate() {
        Date date = new Date();
        counts = DateHelper.getDayOfWeek(date) - 1;
        signTime = DateHelper.getCurrentTime();
        signDate = DateHelper.getCurrentDate();
        signTime = signTime.substring(0, 5);
        switch (counts) {
            case 1:
                signWeek = "星期一";
                break;
            case 2:
                signWeek = "星期二";
                break;
            case 3:
                signWeek = "星期三";
                break;
            case 4:
                signWeek = "星期四";
                break;
            case 5:
                signWeek = "星期五";
                break;
            case 6:
                signWeek = "星期六";
                break;
            case 7:
                signWeek = "星期日";
                break;
            default:
                break;
        }
        tvSignWeek.setText(signWeek + ":");
        tvSignDate.setText(signDate);
        tvSignTime.setText(signTime);
    }

    //初始化用户
    private void initUsers() {
        List<Users> list = JwAppAplication.getInstance().finalDb.findAll(Users.class);
        if (list != null && list.size() > 0) {
            user = list.get(0);
        }
        userNick = user.getNickname();
        userPhone = user.getUsername();
        signUserCode = user.getUser_code();
        if (StrUtils.IsNotEmpty(userNick)) {
            signUserPic = userNick.substring(userNick.length() - 2, userNick.length());
        } else {
            signUserPic = "";
        }
        signUserName = StrUtils.IfNull(userNick, userPhone);
        tvSignUserPic.setText(signUserPic);
        tvSignUserName.setText(signUserName);
    }

    //得到signedCode用于查找签到次数
    private void getData() {
        showLoading();
        signedCode = getIntent().getStringExtra(StaticStrUtils.baseItem);
        new FinishRefresh(getMy()).execute();
    }

    //签到保存信息
    @OnClick(R.id.tv_sign_button)
    void signClick() {
        signed.setSign_msg(signedContext);
        signed.setSign_title(signTitleDet);
        signed.setSign_code(signedCode);
        signed.setSign_user_code(signUserCode);
        signed.setLatitude(latitude);
        signed.setLongtude(longitude);
        signed.setLocation(address);
        new FinishRefreshSigned(getMy()).execute();
    }

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
                if (StrUtils.IsNotEmpty(signedCode)) {
                    list = jCloudDB.findAllByWhere(Sign.class,
                            "sign_code=" + StrUtils.QuotedStr(signedCode));
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
                if (ListUtils.IsNotNull(list)) {
                    sign = list.get(0);
                    signTitleDet = sign.getSign_title();
                    tvSignTitleDet.setText(signTitleDet);
                    signedContext = sign.getSend_context();
                }
            } else {
                ToastShow("用户名或密码出错");
            }
            hideLoading();
        }
    }

    private class FinishRefreshSigned extends AsyncTask<String, Void, String> {
        private Context context;

        /**
         * @param context 上下文
         */
        public FinishRefreshSigned(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            JCloudDB jCloudDB = new JCloudDB();
            try {
                jCloudDB.save(signed);
                counts = Integer.parseInt(signCounts) + 1;
                signCounts = "" + counts;
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            tvSignCounts.setText(signCounts);
        }
    }

    private class FinishRefreshSignedCounts extends AsyncTask<String, Void, String> {
        private Context context;

        /**
         * @param context 上下文
         */
        public FinishRefreshSignedCounts(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";
            SqlInfo sqlInfo = new SqlInfo();
            sqlInfo.setSql("SELECT COUNT(*) mycount " +
                    "FROM signed " +
                    "WHERE sign_code=? and sign_user_code=?");
            sqlInfo.addValue(signedCode);
            sqlInfo.addValue(signUserCode);
            String sql = sqlInfo.getBuildSql();
            try {
                res = CloudDB.queryRes(sql);
                JsonElement element = res.getData();
                String eleStr = element.toString();
                List<LSignedCountItem> items =
                        JwJSONUtils.getParseArray(eleStr,
                                LSignedCountItem.class);
                lSignedCounts = items.get(0);
                signCounts = lSignedCounts.getMycount();
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            tvSignCounts.setText(StrUtils.StrIfNull(signCounts));
        }
    }

    /**
     * 实现实时位置回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                address = location.getAddrStr();
                tvSignAddr.setText(StrUtils.IfNull(address, "地址有误！！！"));
            }
        }
    }
}