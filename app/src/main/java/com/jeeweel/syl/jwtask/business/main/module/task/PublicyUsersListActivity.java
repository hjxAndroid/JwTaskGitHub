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
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import api.adapter.CheckAdapter;
import api.util.OttUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PublicyUsersListActivity extends JwActivity {
    List<Userdept> mListItems = new ArrayList<Userdept>();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicy_users_list);
        userdept = (Userdept) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        if (null != userdept) {
            setTitle(userdept.getDept_name());
        }
        ButterKnife.bind(this);
        initRight();
        initListView();
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                if (cbAll.isChecked()) {
                    // 遍历list的长度，将MyAdapter中的map值全部设为true
                    for (int i = 0; i < list.size(); i++) {
                        checkAdapter.getIsSelected().put(i, true);
                    }
                    // 数量设为list的长度
                    checkNum = list.size();
                    // 刷新listview和TextView的显示
                    checkAdapter.notifyDataSetChanged();

                } else {
                    // 遍历list的长度，将已选的按钮设为未选
                    for (int i = 0; i < list.size(); i++) {
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


    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成");
        menuTextView.setTextColor(getResources().getColor(R.color.white));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                List<Userdept> userdepts = new ArrayList<Userdept>();
                HashMap<Integer, Boolean> isSelected = checkAdapter.getIsSelected();
                for (int i = 0; i < isSelected.size(); i++) {
                    if(isSelected.get(i)){
                        userdepts.add(mListItems.get(i));
                    }
                }
                Gson gson = new Gson();
                String json = gson.toJson(userdepts);
                OttUtils.push("pulbicy_users",json);
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

            if (null != userdept) {
                try {
                    list = jCloudDB.findAllByWhere(Userdept.class,
                            "dept_code = " + StrUtils.QuotedStr(userdept.getDept_code()));
                } catch (CloudServiceException e) {
                    e.printStackTrace();
                }

                if (ListUtils.IsNotNull(list)) {
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
                checkAdapter = new CheckAdapter(mListItems, getMy());
                listview.setAdapter(checkAdapter);
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
}
