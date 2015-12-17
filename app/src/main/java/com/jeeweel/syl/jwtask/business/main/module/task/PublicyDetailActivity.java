package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Alreadyread;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.config.jsonclass.V_publicityunread;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import api.util.Contants;
import api.util.Utils;
import api.viewpage.CBViewHolderCreator;
import api.viewpage.ConvenientBanner;
import api.viewpage.NetworkImageHolderView;
import butterknife.Bind;
import butterknife.ButterKnife;

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

    Users users;
    String orgCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicy_detail);
        ButterKnife.bind(this);
        setTitle("公告详情");
        users = JwAppAplication.getInstance().getUsers();
        orgCode = (String) SharedPreferencesUtils.get(getMy(), Contants.org_code, "");
        setData();
    }

    private void setData() {
        publicity = (V_publicityunread) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        if (null != publicity) {
            tvTitle.setText(publicity.getPublicity_title());
            tvName.setText(publicity.getNickname());
            tvTime.setText(publicity.getCreate_time());
            tvOrgName.setText(publicity.getAccept_org_name());
            tvContent.setText(publicity.getPublicity_content());

            networkImages = new ArrayList<String>();
            String path = publicity.getPictureListSting();

            String wd = publicity.getUnread();
            if(StrUtils.IsNotEmpty(wd)){
                tvWd.setText(wd+"人未读");
            }
            String yd = publicity.getAlread();
            if(StrUtils.IsNotEmpty(yd)){
                tvYd.setText(yd+"人未读");
            }

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

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        // 停止翻页
        convenientBanner.stopTurning();
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

                    if(null!=users){
                        List<Alreadyread> alreadyreadList = jCloudDB.findAllByWhere(Alreadyread.class,
                                "task_code=" + StrUtils.QuotedStr(publicity.getPublicity_code()) + "and password=" + StrUtils.QuotedStr(users.getUser_code()) + "and username=" + StrUtils.QuotedStr(orgCode));
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
                ToastShow("任务发布成功");
                finish();
            } else {
                ToastShow("保存失败");
            }
            hideLoading();
        }
    }
}
