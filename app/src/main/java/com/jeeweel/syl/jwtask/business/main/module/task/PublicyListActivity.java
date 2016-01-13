package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Publicity;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.config.jsonclass.V_publicityunread;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import api.util.Contants;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;

public class PublicyListActivity extends JwListActivity {
    List<V_publicityunread> mListItems = new ArrayList<V_publicityunread>();

    @Bind(R.id.listview)
    PullToRefreshListView listview;
    private CommonAdapter commonAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<V_publicityunread> list;

    private Users users;

    /**
     * 用于判断是从哪请求过来的
     */
    private String tag = "";

    private String orgcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        setTitle("公告列表");
        users = JwAppAplication.getInstance().getUsers();
        ButterKnife.bind(this);
        orgcode = (String) SharedPreferencesUtils.get(getMy(), Contants.org_code, "");
        if (StrUtils.isEmpty(orgcode)) {
            ToastShow("您尚未加入组织");
        } else {
            initView();
            initListViewController();
        }
    }

    private void initView() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("发布");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLoading();
                new CheckOrg(getMy()).execute();
            }
        });
        addMenuView(menuTextView);
    }

    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<V_publicityunread>(getMy(), mListItems, R.layout.item_publicy) {
            @Override
            public void convert(ViewHolder helper, V_publicityunread item) {
                helper.setText(R.id.tv_name, item.getNickname());
                helper.setText(R.id.tv_title, item.getPublicity_title());
                helper.setText(R.id.tv_time, item.getCreate_time());
                ImageView iv_photo = helper.getImageView(R.id.iv_xz);
                JwImageLoader.displayImage(Utils.getPicUrl() + item.getPhoto_code(), iv_photo);
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }

    public class GetUrl extends AsyncTask<String, Void, String> {
        private Context context;
        private String photo_code;
        private ViewHolder helper;

        public GetUrl(Context context, ViewHolder helper, String photo_code) {
            this.context = context;
            this.photo_code = photo_code;
            this.helper = helper;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "0";
            List<Picture> list;
            try {
                JCloudDB jCloudDB = new JCloudDB();
                String sSql = "pic_code=?";
                SqlInfo sqlInfo = new SqlInfo();
                sqlInfo.setSql(sSql);
                sqlInfo.addValue(photo_code);
                sSql = sqlInfo.getBuildSql();
                list = jCloudDB.findAllByWhere(Picture.class, sSql);
                if (ListUtils.IsNotNull(list)) {
                    Picture picture = list.get(0);
                    String path = picture.getPic_road();
                    String uri = Utils.getPicUrl() + path;
                    if (com.jeeweel.syl.jcloudlib.db.utils.StrUtils.IsNotEmpty(path)) {
                        result = uri;
                    }
                }
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("0")) {

            } else {
                helper.setImageByUrl(R.id.iv_xz, result);
            }
        }
    }

    @Override
    public void onListItemClick(int position) {
        V_publicityunread publicity = (V_publicityunread) commonAdapter.getItem(position);
        JwStartActivity(PublicyDetailActivity.class, publicity);
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
                        list = jCloudDB.findAllByWhere(V_publicityunread.class,
                                "accept_org_code = " + StrUtils.QuotedStr(orgcode)
                                        + " order by create_time desc limit " + pageStart + "," + pageEnd
                        );
                        mListItems.clear();
                    } else {
                        setPage(false);
                        list = jCloudDB.findAllByWhere(V_publicityunread.class,
                                "accept_org_code = " + StrUtils.QuotedStr(orgcode)
                                        + " order by create_time desc limit " + pageStart + "," + pageEnd
                        );
                    }

                    if (ListUtils.IsNotNull(list)) {
                        result = "1";
                        for (V_publicityunread publicity : list) {
                            //取头像
                            String publicy_code = publicity.getPublicity_code();

                            String sSql = "pic_code=?";
                            SqlInfo sqlInfo = new SqlInfo();
                            sqlInfo.setSql(sSql);
                            sqlInfo.addValue(publicy_code);
                            sSql = sqlInfo.getBuildSql();
                            List<Picture> pictureList = jCloudDB.findAllByWhere(Picture.class, sSql);
                            if (ListUtils.IsNotNull(pictureList)) {
                                StringBuffer stringBuffer = new StringBuffer();
                                for (int i = 0; i < pictureList.size(); i++) {
                                    stringBuffer.append(pictureList.get(i).getPic_road() + ",");
                                }
                                publicity.setPictureListSting(stringBuffer.toString());
                                Picture picture = pictureList.get(0);
                                String path = picture.getPic_road();
                                if (StrUtils.IsNotEmpty(path)) {
                                    //存头像
                                    publicity.setPhoto_code(path);
                                }
                            }
                        }
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
            } else {
                //没有加载到数据
            }
            listview.onRefreshComplete();
            hideLoading();
        }
    }


    /**
     * 查看权限
     */
    private class CheckOrg extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;
        List<Orgunit> orgunits = null;

        /**
         * @param context 上下文
         */
        public CheckOrg(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            if (null != users) {
                try {
                    orgunits = jCloudDB.findAllByWhere(Orgunit.class, " org_code = " + "\'" + orgcode + "\'" + " and founder_code = " + "\'" + users.getUser_code() + "\'");

                } catch (CloudServiceException e) {
                    result = "0";
                    e.printStackTrace();
                }

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                if (ListUtils.IsNotNull(orgunits)) {
                    JwStartActivity(PublicyAddActivity.class, orgunits.get(0));
                } else {
                    ToastShow("您不是管理员，没有权限发布公告");
                }
            }
            hideLoading();
        }

    }

    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if ((StrUtils.IsNotEmpty(msg) && msg.equals("publicy_refresh")) || (StrUtils.IsNotEmpty(msg) && msg.equals("publicy_detail"))) {
            new FinishRefresh(getMy(), 0).execute();
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
