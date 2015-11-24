package com.jeeweel.syl.jwtask.business.main.tab;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.autoupdate.appupdate.NewVersion;
import com.jeeweel.syl.lib.api.core.base.JeeweelApplication;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.otto.LocInfoEvent;
import com.jeeweel.syl.lib.api.core.otto.NeedLocEvent;
import com.jeeweel.syl.lib.api.core.otto.OttoBus;
import com.jeeweel.syl.lib.api.jwlib.baidumaps.BaiduLocationServiceImpl;
import com.jeeweel.syl.lib.api.jwlib.baidumaps.ILocationCallback;
import com.jeeweel.syl.lib.api.jwlib.baidumaps.LocationInfo;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class JwTabActivity extends ActivityGroup {
    private Context context = this;
    public TabHost mTabHost;
    //最多6个分页
    public static final String TAB_MENU1 = "MENU1";
    public static final String TAB_MENU2 = "MENU2";
    public static final String TAB_MENU3 = "MENU3";
    public static final String TAB_MENU4 = "MENU4";
    public static final String TAB_MENU5 = "MENU5";
    public static final String TAB_MENU6 = "MENU6";


    private RadioGroup BtnGroup;
    // 按下返回记录时间，如果下次按返回小于2秒则退出程序
    private long exitTime = 0;

    private JeeweelApplication app;
    private boolean isLoc;
    private BaiduLocationServiceImpl baiduLocationService;
    private BaiduLocationServiceImpl baiduLocationServiceOne;

    //首页,自动更新相关
    private String downloadPath = "";
//    private String downloadPath = "http://192.168.0.45:8080/";
    private String appVsrsion = "android_version.json";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_host);
        init();
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
    }

    public void init() {
        app = JeeweelApplication.getAppSelf();
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this.getLocalActivityManager());
        //初始化tab 事件总线
//        OttoBus.getDefault().register(this);
        initTabs();
        initBtnRaidoGroup();

        RadioButton menu_btn1 = (RadioButton) findViewById(R.id.menu1);
        menu_btn1.setChecked(true);
        initExternal();
    }

    public void autoUpdate() {
        if (StrUtils.IsNotEmpty(downloadPath)) {
            try {
                new NewVersion(context, downloadPath,
                        appVsrsion).checkUpdateVersion();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化第三方配置
     */
    private void initExternal() {
//        PushManager.startWork(getApplicationContext(),
//                PushConstants.LOGIN_TYPE_API_KEY,
//                Utils.getMetaValue(JwTabActivity.this, "api_key"));
//        // Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
//         PushManager.enableLbs(getApplicationContext());
        initLocation();
        initLocationOne();

        //第三方API配置
//        ShareSDK.initSDK(this);
        //这个要注释掉
//        ShareSDK.registerPlatform(MainTabActivity.class);
//        ShareSDK.setConnTimeout(20000);
//        ShareSDK.setReadTimeout(20000);
    }

//    @Override
//    protected void onDestroy() {
//        OttoBus.getDefault().unregister(this);
//        super.onDestroy();
//    }

    /**
     * 初始化Tabs
     */
    public void initTabs() {

    }

    // 定位相关,持续获取持续广播
    public void initLocation() {
         baiduLocationService = new BaiduLocationServiceImpl(this, new ILocationCallback() {
            @Override
            public void callback(LocationInfo locationInfo) {
                OttoBus.getDefault().post(new LocInfoEvent(StaticStrUtils.loc, locationInfo));
            }
        });

    }

    // 定位相关,一次性定位,单纯的获取经纬度
    public void initLocationOne() {
        baiduLocationServiceOne = new BaiduLocationServiceImpl(this, new ILocationCallback() {
            @Override
            public void callback(LocationInfo locationInfo) {
                OttoBus.getDefault().post(new LocInfoEvent(StaticStrUtils.locone,locationInfo));
                baiduLocationServiceOne.locateStop();
                isLoc = true;
            }
        });

    }

    void startLocation() {
        baiduLocationService.locate();
    }
    void stopLocation() {
        baiduLocationService.locateStop();
    }

    void startLocationOne() {
        isLoc = false;
        baiduLocationServiceOne.locate();
    }

    @Subscribe
    public void OnNeedLoc(NeedLocEvent needLocEvent) {
        String msg = StrUtils.IsNull(needLocEvent.getMsg());
        //正常定位
        if (msg.equals(StaticStrUtils.loc)) {
            startLocation();
        } else if (msg.equals(StaticStrUtils.locone)) { //定位成功获取到地址后关闭定位
            startLocationOne();
        } else if (msg.equals(StaticStrUtils.locstop)) {
           stopLocation();
        }
    }

    //此时在 TabActivity无法监听keyevent，onKeyDown冲突了。
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0 )   {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                // Toast提示再按一次退出程序
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(
                                R.string.Again_according_to_exit_the_program),
                        Toast.LENGTH_SHORT).show();
                // 保存当前系统currentTimeMillis
                exitTime = System.currentTimeMillis();
            } else {
//                PushManager.stopWork(getApplicationContext());
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }



    /**
     * @param
     * @return void 返回类型
     * @throws
     * @Title: setAllRadioButtondNoHig
     * @Description: 设置所有按钮不高亮
     */
    private void setAllRadioButtondNoHig() {
        setRadioButtondrawableTop((RadioButton) findViewById(R.id.menu1),
                getResources().getDrawable(R.drawable.menu1_n));
        setRadioButtondrawableTop((RadioButton) findViewById(R.id.menu2),
                getResources().getDrawable(R.drawable.menu4_n));
        setRadioButtondrawableTop((RadioButton) findViewById(R.id.menu3),
                getResources().getDrawable(R.drawable.menu3_n));
        setRadioButtondrawableTop((RadioButton) findViewById(R.id.menu4),
                getResources().getDrawable(R.drawable.menu4_n));
        setRadioButtondrawableTop((RadioButton) findViewById(R.id.menu5),
                getResources().getDrawable(R.drawable.menu5_n));
        setRadioButtondrawableTop((RadioButton) findViewById(R.id.menu6),
                getResources().getDrawable(R.drawable.menu6_n));

    }

    private void setRadioButtondrawableTop(RadioButton radioButton,
                                           Drawable drawableTop) {
        if (radioButton!=null) {
            drawableTop.setBounds(0, 0, drawableTop.getIntrinsicWidth(),
                    drawableTop.getIntrinsicHeight());
            radioButton.setCompoundDrawables(null, drawableTop, null, null);
        }
    }

    /**
     * @param @param now
     * @param @param next
     * @param @param tag 设定
     * @return void 返回类型
     * @throws
     * @Title: setCurrentTabWithAnim
     * @Description: TabBar有动画效果切换
     */
    public void setCurrentTabWithAnim(int now, int next, String tag) {
        // 这个方法是关键，用来判断动画滑动的方向
        if (now > next) {
            // MainTabHost.getCurrentView().startAnimation(
            // AnimationUtils.loadAnimation(this, R.anim.push_right_out));
            mTabHost.setCurrentTabByTag(tag);
            // MainTabHost.getCurrentView().startAnimation(
            // AnimationUtils.loadAnimation(this, R.anim.push_right_in));
        } else {
            // MainTabHost.getCurrentView().startAnimation(
            // AnimationUtils.loadAnimation(this, R.anim.push_left_out));
            mTabHost.setCurrentTabByTag(tag);
            // MainTabHost.getCurrentView().startAnimation(
            // AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        }
    }

    private void initBtnRaidoGroup() {
        BtnGroup = (RadioGroup) findViewById(R.id.main_radio);

        BtnGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                setAllRadioButtondNoHig();
                int currentTab = mTabHost.getCurrentTab();
                switch (checkedId) {
                    case R.id.menu1:
                        setCurrentTabWithAnim(currentTab, 0, TAB_MENU1);
                        // 设置按钮高亮（替换图片）
                        setRadioButtondrawableTop(
                                (RadioButton) findViewById(checkedId),
                                getResources().getDrawable(R.drawable.menu1_p));
                        break;
                    case R.id.menu2:
                        setCurrentTabWithAnim(currentTab, 0, TAB_MENU2);
                        // 设置按钮高亮（替换图片）
                        setRadioButtondrawableTop(
                                (RadioButton) findViewById(checkedId),
                                getResources().getDrawable(R.drawable.menu4_p));
                        break;

                    case R.id.menu3:
                        setCurrentTabWithAnim(currentTab, 0, TAB_MENU3);
                        setRadioButtondrawableTop(
                                (RadioButton) findViewById(checkedId),
                                getResources().getDrawable(R.drawable.menu3_p));
                        break;
                    case R.id.menu4:
                        setCurrentTabWithAnim(currentTab, 0, TAB_MENU4);
                        setRadioButtondrawableTop(
                                (RadioButton) findViewById(checkedId),
                                getResources().getDrawable(R.drawable.menu4_p));
                        break;
                    case R.id.menu5:
                        setCurrentTabWithAnim(currentTab, 0, TAB_MENU5);
                        setRadioButtondrawableTop(
                                (RadioButton) findViewById(checkedId),
                                getResources().getDrawable(R.drawable.menu5_p));
                        break;
                    case R.id.menu6:
                        setCurrentTabWithAnim(currentTab, 0, TAB_MENU6);
                        setRadioButtondrawableTop(
                                (RadioButton) findViewById(checkedId),
                                getResources().getDrawable(R.drawable.menu6_p));
                        break;
                }
            }
        });
    }

    /**
     * @param @return 设定
     * @return Integer 返回类型
     * @throws
     * @Title: getAppVerison
     * @Description: 获取APP版本信息
     */
    public Integer getAppVerison() {
        Integer verInteger = 0;
        PackageManager manager = JwTabActivity.this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(
                    JwTabActivity.this.getPackageName(), 0);
            // 版本名
            // String appVersion = info.versionName;
            // 版本号
            verInteger = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch blockd
            e.printStackTrace();
        }
        return verInteger;
    }


    /**
     * @param @param  jsonString
     * @param @return 设定
     * @return boolean 返回类型
     * @throws
     * @Title: checkAppVersion
     * @Description: 对比服务器和本地的版本
     */
    private boolean checkAppVersion(String jsonString) {
        boolean isUpdate = false;
        if (jsonString != null && !jsonString.equals("")) {
            // 解决2.x JSON无法加载问题 2.x没有对下载的JSON： UTF8 BOM头
            String jsString = jsonString.replace("\ufeff", "");
            Integer sumInteger = 0;
            try {
                JSONObject jsonObject = new JSONObject(jsString);
                sumInteger = jsonObject.getInt("sum");
                if (sumInteger > 0) {
                    // 得到主item的所有数据
                    JSONArray jsonItems = (JSONArray) jsonObject.get("item");
                    JSONObject Childjo = (JSONObject) jsonItems.get(0);
                    Integer serverVerInteger = 0;
                    serverVerInteger = Childjo.getInt("no");
                    Integer levelInteger = 0;
                    levelInteger = Childjo.getInt("level");
                    String down_load_url = "";
                    down_load_url = URLDecoder.decode(
                            Childjo.getString("down_load_url"), "utf-8");
                    if (serverVerInteger > 0) {
                        Integer appVerInteger = 0;
                        appVerInteger = getAppVerison();
                        if (appVerInteger > 0) {
                            if (serverVerInteger > appVerInteger) {
//                                showUpdateDialog(down_load_url,levelInteger);
                            }
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return isUpdate;
    }


    public TabHost getmTabHost() {
        return mTabHost;
    }

    public void setmTabHost(TabHost mTabHost) {
        this.mTabHost = mTabHost;
    }

    public RadioGroup getBtnGroup() {
        return BtnGroup;
    }

    public void setBtnGroup(RadioGroup btnGroup) {
        BtnGroup = btnGroup;
    }

    public long getExitTime() {
        return exitTime;
    }

    public void setExitTime(long exitTime) {
        this.exitTime = exitTime;
    }

    public JeeweelApplication getApp() {
        return app;
    }

    public void setApp(JeeweelApplication app) {
        this.app = app;
    }

    public boolean isLoc() {
        return isLoc;
    }

    public void setIsLoc(boolean isLoc) {
        this.isLoc = isLoc;
    }

    public BaiduLocationServiceImpl getBaiduLocationService() {
        return baiduLocationService;
    }

    public void setBaiduLocationService(BaiduLocationServiceImpl baiduLocationService) {
        this.baiduLocationService = baiduLocationService;
    }

    public BaiduLocationServiceImpl getBaiduLocationServiceOne() {
        return baiduLocationServiceOne;
    }

    public void setBaiduLocationServiceOne(BaiduLocationServiceImpl baiduLocationServiceOne) {
        this.baiduLocationServiceOne = baiduLocationServiceOne;
    }

    public static String getTabMenu1() {
        return TAB_MENU1;
    }

    public static String getTabMenu2() {
        return TAB_MENU2;
    }

    public static String getTabMenu3() {
        return TAB_MENU3;
    }

    public static String getTabMenu4() {
        return TAB_MENU4;
    }

    public static String getTabMenu5() {
        return TAB_MENU5;
    }

    public static String getTabMenu6() {
        return TAB_MENU6;
    }

    public String getAppVsrsion() {
        return appVsrsion;
    }

    public void setAppVsrsion(String appVsrsion) {
        this.appVsrsion = appVsrsion;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}
