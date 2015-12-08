package com.jeeweel.syl.jwtask.business.main.tab;

import android.content.Intent;
import android.os.Bundle;

import com.jeeweel.syl.jwtask.business.main.module.contact.ContactHomeActivity;
import com.jeeweel.syl.jwtask.business.main.module.more.MoreHomeActivity;
import com.jeeweel.syl.jwtask.business.main.module.news.NewsHomeActivity;
import com.jeeweel.syl.jwtask.business.main.module.task.TaskHomeActivity;

//import com.special.ResideMenu.ResideMenu;
//import com.special.ResideMenu.ResideMenuItem;


public class TabHostActivity extends JwTabActivity {

    public static TabHostActivity self;
    //ResideMenu resideMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public TabHostActivity getSelf() {
        return self;
    }

    public void setSelf(TabHostActivity self) {
        this.self = self;
    }

    public void initTabs() {
        self = this;
//        // 添加日志列表的tab,注意下面的setContent中的代码.是这个需求实现的关键
        mTabHost.addTab(mTabHost.newTabSpec(TAB_MENU1)
                .setIndicator(TAB_MENU1)
                .setContent(new Intent(this, NewsHomeActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_MENU2)
                .setIndicator(TAB_MENU2)
                .setContent(new Intent(this, TaskHomeActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_MENU3)
                .setIndicator(TAB_MENU3)
                .setContent(new Intent(this, ContactHomeActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_MENU4)
                .setIndicator(TAB_MENU4)
                .setContent(new Intent(this, MoreHomeActivity.class)));
//        mTabHost.addTab(mTabHost.newTabSpec(TAB_MENU2)
//                .setIndicator(TAB_MENU2)
//                .setContent(new Intent(this, TabMenu2Activity.class)));
//        mTabHost.addTab(mTabHost.newTabSpec(TAB_MENU3)
//                .setIndicator(TAB_MENU3)
//                .setContent(new Intent(this, TabMenu3Activity.class)));
//        mTabHost.addTab(mTabHost.newTabSpec(TAB_MENU4)
//                .setIndicator(TAB_MENU4)
//                .setContent(new Intent(this, TabMenu4Activity.class)));

//        initMenu();
//        setDownloadPath("http://117.27.234.237:8080/hospital180/doc/");
//        autoUpdate();
//        OttoBus.getDefault().register(this);
    }

//    public void initMenu() {
//        resideMenu = new ResideMenu(this);
//        resideMenu.setBackground(R.drawable.menu_background);
//        //禁用手势,防止控件冲突
//        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
////        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
//        resideMenu.attachToActivity(this);
//
//        // create menu items;
//        String titles[] = {  "设置" };
//        int icon[] = {  R.drawable.icon_settings };
//
//        for (int i = 0; i < titles.length; i++){
//            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
//            item.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getSelf(), MoreActivity.class);
//                    startActivity(intent);
//                }
//            });
////            item.setOnClickListener(this);
//            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
//        }
//    }

/*    public void OnOpenMenu() {
        resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }*/


}
