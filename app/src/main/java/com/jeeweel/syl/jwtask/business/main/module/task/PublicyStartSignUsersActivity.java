package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.jeeweel.syl.lib.api.core.otto.OttoBus;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import api.adapter.CheckAdapter;
import api.adapter.StrartSignAdapter;
import api.util.Contants;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ragn on 2016/1/5.
 */
public class PublicyStartSignUsersActivity extends JwActivity {

    List<Userorg> mListItems = new ArrayList<Userorg>();
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.cb_all)
    CheckBox cbAll;

    private StrartSignAdapter strartSignAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<Userorg> list;
    private Orgunit orgunit;
    private Userorg userorg;

    /**
     * 用于判断是从哪请求过来的
     */
    private String tag = "";


    private Userdept userdept;

    private int checkNum; // 记录选中的条目数量
    private TextView tv_show;// 用于显示选中的条目数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicy_users_list);
        orgunit = (Orgunit) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        if (null != orgunit) {
            setTitle(orgunit.getOrg_name());
        }
        tag = getIntent().getStringExtra("tag");
        ButterKnife.bind(this);
        initRight();
        initListView();
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                if (cbAll.isChecked()) {
                    // 遍历list的长度，将MyAdapter中的map值全部设为true
                    for (int i = 0; i < list.size(); i++) {
                        strartSignAdapter.getIsSelected().put(i, true);
                    }
                    // 数量设为list的长度
                    checkNum = list.size();
                    // 刷新listview和TextView的显示
                    strartSignAdapter.notifyDataSetChanged();

                } else {
                    // 遍历list的长度，将已选的按钮设为未选
                    for (int i = 0; i < list.size(); i++) {
                        if (strartSignAdapter.getIsSelected().get(i)) {
                            strartSignAdapter.getIsSelected().put(i, false);
                            checkNum--;// 数量减1
                        }
                    }
                    // 刷新listview和TextView的显示
                    strartSignAdapter.notifyDataSetChanged();

                }
            }
        });
    }


    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                List<Userorg> userorgs = new ArrayList<Userorg>();
                HashMap<Integer, Boolean> isSelected = strartSignAdapter.getIsSelected();
                for (int i = 0; i < isSelected.size(); i++) {
                    if (isSelected.get(i)) {
                        userorgs.add(mListItems.get(i));
                    }
                }
                Gson gson = new Gson();
                String json = gson.toJson(userorgs);

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
        showLoading();
        new FinishRefresh(getMy()).execute();
    }


    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private int mode = 0;
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

            if (null != orgunit) {
                try {
                    list = jCloudDB.findAllByWhere(Userorg.class,
                            " org_code = " + StrUtils.QuotedStr(orgunit.getOrg_code()));
                    mListItems.clear();
                    removeDuplicate(list);
                } catch (CloudServiceException e) {
                    e.printStackTrace();
                }

                if (ListUtils.IsNotNull(list)) {
                    for (Userorg userorgs : list) {
                        //取头像
                        userorg = userorgs;
                        String user_code = userorg.getUser_code();

                        String sSql = "pic_code=?";
                        SqlInfo sqlInfo = new SqlInfo();
                        sqlInfo.setSql(sSql);
                        sqlInfo.addValue(user_code);
                        sSql = sqlInfo.getBuildSql();
                        List<Picture> pictureList = null;
                        try {
                            pictureList = jCloudDB.findAllByWhere(Picture.class, sSql);
                        } catch (CloudServiceException e) {
                            e.printStackTrace();
                        }
                        if (ListUtils.IsNotNull(pictureList)) {
                            Picture picture = pictureList.get(0);
                            String path = picture.getPic_road();
                            if (StrUtils.IsNotEmpty(path)) {
                                //存头像
                                userorg.setPhoto_code(path);
                            }
                        }
                    }

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
                mListItems.addAll(list);
                strartSignAdapter = new StrartSignAdapter(mListItems, getMy());
                listview.setAdapter(strartSignAdapter);
            } else {
                //没有加载到数据
            }
            hideLoading();
        }
    }

    /**
     * 分页增数
     */

    private void setPage(boolean tag) {
        if (tag) {
            pageStart = 0;
            pageEnd = 10;
        } else {
            pageStart += addNum;
            pageEnd += addNum;
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


    /**
     * 去除多余元素
     *
     * @param list
     */
    public void removeDuplicate(List<Userorg> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getUser_code().equals(list.get(i).getUser_code())) {
                    list.remove(j);
                }
            }
        }
    }
}
