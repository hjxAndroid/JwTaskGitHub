package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.UserdeptItem;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.jeeweel.syl.lib.api.core.otto.OttoBus;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import api.adapter.CheckAdapter;
import api.util.Contants;
import api.util.OttUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SelectedActivity extends JwActivity {
    List<UserdeptItem> mListItems = new ArrayList<UserdeptItem>();
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.cb_all)
    CheckBox cbAll;

    private CheckAdapter checkAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<Userdept> list;

    /**
     * 用于判断是从哪请求过来的
     */
    private String tag = "";


    private Userdept userdept;

    private int checkNum; // 记录选中的条目数量
    private TextView tv_show;// 用于显示选中的条目数量

    String json = "";
    List<UserdeptItem> userdepts = new ArrayList<>();
    List<UserdeptItem> userdeptnews = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);
        setTitle("选择接收人");
        tag = getIntent().getStringExtra(StaticStrUtils.baseItem);
        json = getIntent().getStringExtra("data");
        if(StrUtils.IsNotEmpty(json)){
            userdepts = JwJSONUtils.getParseArray(json, Userdept.class);
        }
        ButterKnife.bind(this);
        initRight();
        initListView();
    }

    @OnClick(R.id.li_add)
    void AddClick() {
        Intent intent = new Intent(getMy(),PublicyContactHomeActivity.class);
        intent.putExtra(StaticStrUtils.baseItem,tag);
        intent.putExtra("data",json);
        startActivity(intent);
        finish();
    }


    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                List<UserdeptItem> userdepts = new ArrayList<UserdeptItem>();
                HashMap<Integer, Boolean> isSelected = checkAdapter.getIsSelected();
                for (int i = 0; i < isSelected.size(); i++) {
                    if (isSelected.get(i)) {
                        userdepts.add(mListItems.get(i));
                    }
                }
                Gson gson = new Gson();
                String json = gson.toJson(userdepts);

                ActivityMsgEvent activityMsgEvent = new ActivityMsgEvent();
                activityMsgEvent.setMsg(tag);
                activityMsgEvent.setParam1(json);
                OttoBus.getDefault().post(activityMsgEvent);

                finish();
            }
        });
        addMenuView(menuTextView);
    }


    public void initListView() {

        mListItems.addAll(userdepts);
        checkAdapter = new CheckAdapter(mListItems,userdeptnews,getMy());
        listview.setAdapter(checkAdapter);
        cbAll.setChecked(true);
        // 遍历list的长度，将MyAdapter中的map值全部设为true
        for (int i = 0; i < mListItems.size(); i++) {
            checkAdapter.getIsSelected().put(i, true);
        }
        // 数量设为list的长度
        checkNum = mListItems.size();
        // 刷新listview和TextView的显示
        checkAdapter.notifyDataSetChanged();



        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                if (cbAll.isChecked()) {
                    // 遍历list的长度，将MyAdapter中的map值全部设为true
                    for (int i = 0; i < mListItems.size(); i++) {
                        checkAdapter.getIsSelected().put(i, true);
                    }
                    // 数量设为list的长度
                    checkNum = mListItems.size();
                    // 刷新listview和TextView的显示
                    checkAdapter.notifyDataSetChanged();

                } else {
                    // 遍历list的长度，将已选的按钮设为未选
                    for (int i = 0; i < mListItems.size(); i++) {
                        if (checkAdapter.getIsSelected().get(i)) {
                            checkAdapter.getIsSelected().put(i, false);
                            checkNum--;// 数量减1
                        }
                    }
                    // 刷新listview和TextView的显示
                    checkAdapter.notifyDataSetChanged();

                }
            }
        });
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
