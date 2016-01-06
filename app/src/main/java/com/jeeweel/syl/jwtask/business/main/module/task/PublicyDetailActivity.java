package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Alreadyread;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.config.jsonclass.V_publicityunread;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import api.util.Contants;
import api.util.OttUtils;
import api.util.Utils;
import api.viewpage.CBViewHolderCreator;
import api.viewpage.ConvenientBanner;
import api.viewpage.NetworkImageHolderView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicyDetailActivity extends JwActivity {

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_org_name)
    TextView tvOrgName;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.convenientBanner)
    ConvenientBanner convenientBanner;
    @Bind(R.id.tv_wd)
    TextView tvWd;
    @Bind(R.id.tv_yd)
    TextView tvYd;

    private List<String> networkImages;
    V_publicityunread publicity;
    private List<Orgunit> list;

    Users users;
    String orgCode;

    List<Userorg> alreadyreads;
    List<Userorg> unReads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicy_detail);
        ButterKnife.bind(this);
        setTitle("公告详情");
        users = JwAppAplication.getInstance().getUsers();
        orgCode = (String) SharedPreferencesUtils.get(getMy(), Contants.org_code, "");
        setData();
        new FinishRefreshIfIsFounder(getMy()).execute();
    }


    @OnClick(R.id.rl_yd)
    void ydClick() {
        if(ListUtils.IsNotNull(alreadyreads)){
            String json = new Gson().toJson(alreadyreads);
            JwStartActivity(PublicyReadListActivity.class,json);
        }
    }

    @OnClick(R.id.rl_wd)
    void wdClick() {
        if(ListUtils.IsNotNull(unReads)){
            String json = new Gson().toJson(unReads);
            JwStartActivity(PublicyUnReadListActivity.class,json);
        }
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("修改公告");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                JwStartActivity(ChangePublicyActivity.class, publicity);
            }
        });
        addMenuView(menuTextView);
    }

    private void setData() {
        showLoading();
        publicity = (V_publicityunread) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        if (null != publicity) {
            tvTitle.setText(publicity.getPublicity_title());
            tvName.setText(publicity.getNickname());
            tvTime.setText(publicity.getCreate_time());
            String org_name = publicity.getAccept_org_name();
            if (StrUtils.isEmpty(org_name)) {
                org_name = (String) SharedPreferencesUtils.get(getMy(), Contants.org_name, "");
            }
            tvOrgName.setText(org_name);
            tvContent.setText(publicity.getPublicity_content());

            networkImages = new ArrayList<String>();
            String path = publicity.getPictureListSting();

           /* String wd = publicity.getUnread();
            if (StrUtils.IsNotEmpty(wd)) {
                int intWd = Integer.parseInt(wd);
                if (intWd < 0) {
                    intWd = 0;
                }
                tvWd.setText(intWd + "人未读");
            }
            String yd = publicity.getAlread();
            if (StrUtils.IsNotEmpty(yd)) {
                tvYd.setText(yd + "人已读");
            }*/

            if (path != null) {
                String[] st = path.split(",");
                for (int i = 0; i < st.length; i++) {
                    networkImages.add(Utils.getPicUrl() + st[i]);
                }
                initBanner();
            } else {
                LinearLayout li_img = (LinearLayout) findViewById(R.id.li_img);
                li_img.setVisibility(View.GONE);
            }
            new FinishRefresh(getMy()).execute();

        }
    }

    private void initBanner() {
        // 网络加载例子
        convenientBanner
                .setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, networkImages) // 设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(
                        new int[]{R.drawable.ic_page_indicator,
                                R.drawable.ic_page_indicator_focused})
                        // 设置翻页的效果，不需要翻页效果可用不设
                .setPageTransformer(ConvenientBanner.Transformer.DefaultTransformer);
        convenientBanner.startTurning(2000);
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


            try {

                if (null != users && null != publicity) {

                    //已读人数
                    String readSql  = "SELECT t.* from userorg t where t.user_code in (SELECT c.operator_code from alreadyread c where c.org_code = "+StrUtils.QuotedStr(publicity.getAccept_org_code())+" and c.task_code = "+StrUtils.QuotedStr(publicity.getPublicity_code())+")";
                    //查找数据
                    alreadyreads = jCloudDB.findAllBySql(Userorg.class, readSql);
                    removeDuplicate(alreadyreads);

                    //未读人数
                    String sql  = "SELECT t.* from userorg t where t.user_code not in (SELECT c.operator_code from alreadyread c where c.org_code = "+StrUtils.QuotedStr(publicity.getAccept_org_code())+" and c.task_code = "+StrUtils.QuotedStr(publicity.getPublicity_code())+")";
                    //查找数据
                    unReads = jCloudDB.findAllBySql(Userorg.class, sql);
                    removeDuplicate(unReads);


                    List<Alreadyread> alreadyreadList = jCloudDB.findAllByWhere(Alreadyread.class,
                            "task_code=" + StrUtils.QuotedStr(publicity.getPublicity_code()) + "and operator_code=" + StrUtils.QuotedStr(users.getUser_code()) + "and org_code=" + StrUtils.QuotedStr(orgCode));
                    if (ListUtils.IsNull(alreadyreadList)) {
                        //已读表未插入，插入到已读表
                        Alreadyread alreadyread = new Alreadyread();
                        alreadyread.setTask_code(publicity.getPublicity_code());
                        alreadyread.setOperator_code(users.getUser_code());
                        alreadyread.setOrg_code(orgCode);
                        alreadyread.setOperate_type("0");
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
            if (result.equals("1")) {

                if(ListUtils.IsNotNull(alreadyreads)){
                    tvYd.setText(alreadyreads.size() + "人未读");
                }

                if(ListUtils.IsNotNull(unReads)){
                    tvWd.setText(unReads.size() + "人未读");
                }



                OttUtils.push("news_refresh", "");
                OttUtils.push("publicy_refresh", "");
            }
            hideLoading();
        }
    }

    /**
     * 保存到数据库
     */
    private class FinishRefreshIfIsFounder extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefreshIfIsFounder(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "0";
            try {
                list = jCloudDB.findAllByWhere(Orgunit.class, " org_code = " + "\'" + orgCode + "\'" + " and founder_code = " + "\'" + users.getUser_code() + "\'");
                if (ListUtils.IsNotNull(list)) {
                    result = "1";
                } else {
                    result = "0";
                }
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if ("1".equals(result)) {
                initRight();
                hideLoading();
            }
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
