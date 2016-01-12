package com.jeeweel.syl.jwtask.business.main.module.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.JsonElement;
import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.CloudFile;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.jsonclass.ResMsgItem;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Alreadyread;
import com.jeeweel.syl.jwtask.business.config.jsonclass.LSignedCountItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Sign;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Signed;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.photo.GetPicActivity;
import com.jeeweel.syl.jwtask.business.main.module.photo.PhotoActivity;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwutil.DateHelper;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import api.photoview.Bimp;
import api.photoview.FileUtils;
import api.util.Contants;
import api.util.OttUtils;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.jeeweel.syl.lib.api.jwlib.baidumaps.InitLocationSign;

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
    @Bind(R.id.et_remark)
    EditText etRemark;


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
    String signedRemark;
    String uuid;
    String prouserName;

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
    List<Signed> listSigned;
    LSignedCountItem lSignedCounts;
    LocationClient mLocationClient;
    MyLocationListener mMyLocationListener;
    GridView noScrollgridview;
    @Bind(R.id.tv_signed_content)
    TextView tvSignedContent;
    private ScrollView li_fb;
    private Activity context;
    GridAdapter adapter;
    String orgCode = "";
    private Users users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("签到");
        ButterKnife.bind(this);
        initDate();
        initUsers();
        getData();
        context = this;
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mLocationClient.start();
        users = JwAppAplication.getInstance().getUsers();
        initLocation();
        initRight();
        initView();
        new FinishRefreshSignedCounts(getMy()).execute();
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("我的签到");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                JwStartActivity(CheckOutSignActivity.class, signedCode);
            }
        });
        addMenuView(menuTextView);
    }


    private void initView() {
        li_fb = (ScrollView) findViewById(R.id.li_fb);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        adapter = new GridAdapter(SignUpActivity.this);
        adapter.update1();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.bmp.size()) {
                    new PopupWindows(context, li_fb);
                } else {
                    Intent intent = new Intent(context,
                            PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
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
            if (userNick.length() > 2) {
                signUserPic = userNick.substring(userNick.length() - 2, userNick.length());
            } else {
                signUserPic = userNick;
            }
        } else {
            signUserPic = "姓名";
        }
        signUserName = StrUtils.IfNull(userNick, userPhone);
        tvSignUserPic.setText(signUserPic);
        tvSignUserName.setText(signUserName);
    }

    //得到signedCode用于查找签到次数
    private void getData() {
        showLoading();
        orgCode = (String) SharedPreferencesUtils.get(getMy(), Contants.org_code, "");
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
        signed.setNickname(userNick);
        signed.setProuser_name(prouserName);
        signed.setRemark(etRemark.getText().toString());
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
                    prouserName = sign.getProuser_name();
                    tvSignedContent.setText(signedContext);
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
                listSigned = jCloudDB.findAllByWhere(Signed.class,
                        " sign_code = " + StrUtils.QuotedStr(signedCode) + " and sign_user_code = " + StrUtils.QuotedStr(users.getUser_code()));
                if (ListUtils.IsNotNull(listSigned)) {
                    uuid = listSigned.get(0).getUuid();
                    //删除原来的图片
                    jCloudDB.deleteByWhere(Picture.class, " pic_code = " + StrUtils.QuotedStr(uuid));
                } else {
                    uuid = Utils.getUUid();
                }

                signed.setUuid(uuid);
                jCloudDB.save(signed);
                counts = Integer.parseInt(signCounts) + 1;
                signCounts = "" + counts;
                //保存图片表
                for (String sFile : Bimp.drr) {
                    // File file = new File(sFile);
                    CloudFile.upload(sFile, uuid);
                }
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if ("1".equals(result)) {
                ToastShow("签到成功");
            }
            tvSignCounts.setText(signCounts);
        }
    }

    private class FinishRefreshSignedCounts extends AsyncTask<String, Void, String> {
        private Context context;
        JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefreshSignedCounts(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
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
                //查看已签次数
                res = CloudDB.queryRes(sql);
                JsonElement element = res.getData();
                String eleStr = element.toString();
                List<LSignedCountItem> items =
                        JwJSONUtils.getParseArray(eleStr,
                                LSignedCountItem.class);
                lSignedCounts = items.get(0);
                signCounts = lSignedCounts.getMycount();

                if (null != user) {
                    List<Alreadyread> alreadyreadList = jCloudDB.findAllByWhere(Alreadyread.class,
                            "task_code=" + StrUtils.QuotedStr(signedCode) + "and operator_code=" + StrUtils.QuotedStr(user.getUser_code()) + "and org_code=" + StrUtils.QuotedStr(orgCode));
                    if (ListUtils.IsNull(alreadyreadList)) {
                        //已读表未插入，插入到已读表
                        Alreadyread alreadyread = new Alreadyread();
                        alreadyread.setTask_code(signedCode);
                        alreadyread.setOperator_code(user.getUser_code());
                        alreadyread.setOrg_code(orgCode);
                        alreadyread.setOperate_type("1");
                        jCloudDB.save(alreadyread);
                    }
                }
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            OttUtils.push("news_refresh", "");
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

    //相册popwin
    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            super(mContext);

            View view = View
                    .inflate(mContext, R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(GridLayout.LayoutParams.FILL_PARENT);
            setHeight(GridLayout.LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    photo();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context,
                            GetPicActivity.class);
                    startActivity(intent);
                    dismiss();
                }
            });
            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void photo() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStorageDirectory() + "/myimage/");
            if (!dir.exists()) dir.mkdirs();

            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(dir, String.valueOf(System.currentTimeMillis())
                    + ".jpg");
            path = file.getPath();
            Uri imageUri = Uri.fromFile(file);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        } else {
            Toast.makeText(context, "没有储存卡", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.drr.size() < 9 && resultCode == -1) {
                    Bimp.drr.add(path);
                }
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater; // 视图容器
        private int selectedPosition = -1;// 选中的位置
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update1() {
            loading1();
        }

        public int getCount() {
            return (Bimp.bmp.size() + 1);
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        /**
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            //final int coord = position;
            ViewHolder holder = null;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.image.setVisibility(View.VISIBLE);

            if (position == Bimp.bmp.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));

            } else {
                holder.image.setImageBitmap(Bimp.bmp.get(position));
            }

            if (position == 9) {
                holder.image.setVisibility(View.GONE);
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading1() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.drr.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            try {
                                String path = Bimp.drr.get(Bimp.max);
                                System.out.println(path);
                                Bitmap bm = Bimp.revitionImageSize(path);
                                Bimp.bmp.add(bm);
                                String newStr = path.substring(
                                        path.lastIndexOf("/") + 1,
                                        path.lastIndexOf("."));
                                FileUtils.saveBitmap(bm, "" + newStr);
                                Bimp.max += 1;
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } catch (IOException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
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

    protected void onRestart() {
        adapter.update1();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Bimp.bmp.clear();
        Bimp.drr.clear();
        Bimp.max = 0;
        super.onDestroy();
    }
}