package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.News;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Sign;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Signed;
import com.jeeweel.syl.jwtask.business.config.jsonclass.SignedPictures;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import java.util.ArrayList;
import java.util.List;

import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ragn on 2016/1/11.
 */
public class AlreadySignedActivity extends JwListActivity {
    List<SignedPictures> alreadySigned;
    @Bind(R.id.listview)
    PullToRefreshListView listview;
    List<SignedPictures> mListItems = new ArrayList<SignedPictures>();
    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    CommonAdapter commonAdapter;
    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_signed);
        ButterKnife.bind(this);
        users = JwAppAplication.getInstance().getUsers();
        initListViewController();
    }

    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<SignedPictures>(getMy(), mListItems, R.layout.item_sign_information) {
            @Override
            public void convert(ViewHolder helper, SignedPictures item) {
                ImageView imageView = helper.getImageView(R.id.iv_xz);
                JwImageLoader.displayImage(Utils.getPicUrl() + item.getPic_road(), imageView);
                helper.setText(R.id.tv_sign_title, item.getSign_title());
                helper.setText(R.id.tv_name, item.getProuser_name());
                helper.setText(R.id.tv_sign_time, item.getCreate_time().substring(0, 16));
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }

    @Override
    public void onListItemClick(int position) {
        SignedPictures signedPictures = (SignedPictures) commonAdapter.getItem(position);
        JwStartActivity(SignUpActivity.class, signedPictures.getSign_code());
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
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;
        private int mode = 0;


        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context, int mode) {
            this.context = context;
            jCloudDB = new JCloudDB();
            this.mode = mode;
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            if (null != users) {
                try {

                    //创建视图
                    String sql = "create or replace view v_signed_pic " +
                            " as ( SELECT " +
                            " signed. *, picture.pic_road " +
                            " FROM " +
                            " signed " +
                            " LEFT JOIN picture ON signed.uuid = picture.pic_code " +
                            " WHERE " +
                            " signed.sign_user_code IN ( " +
                            " SELECT " +
                            " b.sign_user_code " +
                            " FROM " +
                            " signed b " +
                            " WHERE " +
                            " b.sign_code IN ( " +
                            " SELECT " +
                            " t.sign_code " +
                            " FROM " +
                            " sign t " +
                            " WHERE " +
                            " t.receive_code LIKE " +
                            StrUtils.QuotedStrLike(users.getUser_code()) +
                            " ) " +
                            " AND b.sign_user_code = " +
                            StrUtils.QuotedStr(users.getUser_code()) +
                            " ) " + "and signed.sign_user_code = "+ StrUtils.QuotedStr(users.getUser_code()) +
                            "ORDER BY " +
                            " create_time DESC) ";
                    CloudDB.execSQL(sql);

                    if (mode == 0) {
                        setPage(true);
                        // String deletSql = "DROP TABLE tmp" + users.getUser_code();
                        //cv CloudDB.execSQL(deletSql);

//                        alreadySigned = jCloudDB.findAllBySql(SignedPictures.class, "SELECT signed.*,picture.pic_road from signed left join picture on signed.sign_user_code = picture.pic_code where signed.sign_code in (SELECT b.sign_code from signed b where b.sign_code in(SELECT t.sign_code from sign t where t.receive_code LIKE " + StrUtils.QuotedStrLike(users.getUser_code()) + ") and b.sign_user_code = " +
//                                StrUtils.QuotedStr(users.getUser_code()) +
//                                ")" +
//                                " ORDER BY create_time DESC " +
//                                " limit " +
//                                pageStart +
//                                "," +
//                                pageEnd);
                        alreadySigned = jCloudDB.findAllBySql(SignedPictures.class, " SELECT " +
                                " * " +
                                " FROM " +
                                " v_signed_pic " +
                                " GROUP BY " +
                                " sign_code " +
                                " LIMIT " + pageStart +
                                "," +
                                pageEnd);
                        mListItems.clear();
                    } else {
                        setPage(false);
                        alreadySigned = jCloudDB.findAllBySql(SignedPictures.class, " SELECT " +
                                " * " +
                                " FROM " +
                                " v_signed_pic " +
                                " GROUP BY " +
                                " sign_code " +
                                " LIMIT " + pageStart +
                                "," +
                                pageEnd);
                    }
                    if (ListUtils.IsNotNull(alreadySigned)) {
                        result = "1";
                    } else {
                        result = "0";
                    }
                } catch (CloudServiceException e) {
                    result = "0";
                    e.printStackTrace();
                }
                String deletSql = "DROP View v_signed_pic";
                try {
                    CloudDB.execSQL(deletSql);
                } catch (CloudServiceException e) {
                    e.printStackTrace();
                }

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                mListItems.addAll(alreadySigned);
                commonAdapter.notifyDataSetChanged();
            }
            listview.onRefreshComplete();
            hideLoading();
        }
    }

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
     * 去除多余元素
     *
     * @param list
     */
    public void removeDuplicateSigned(List<SignedPictures> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getSign_code().equals(list.get(i).getSign_code())) {
                    list.remove(j);
                }
            }
        }
    }
}
