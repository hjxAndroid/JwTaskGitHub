package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.ActionItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Sign;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Signed;
import com.jeeweel.syl.jwtask.business.config.jsonclass.V_sign_users;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.o.OUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import api.util.OttUtils;
import api.util.ShaerHelper;
import api.view.CustomDialog;
import api.view.TitlePopup;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ragn on 2015/12/17.
 */
public class CheckSignPersonActivity extends JwListActivity {
    @Bind(R.id.already_signed_change_counts)
    TextView alreadySignedCounts;
    @Bind(R.id.unsign_change_counts)
    TextView unsignCounts;
    @Bind(R.id.listview)
    PullToRefreshListView listview;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_signed_content)
    TextView tvSignedContent;


    List<Signed> mListItems = new ArrayList<Signed>();
    @Bind(R.id.ll_signed_title)
    LinearLayout llSignedTitle;
    @Bind(R.id.ll_signed_content)
    LinearLayout llSignedContent;

    private CommonAdapter commonAdapter;
    private String signedCode;
    private List<Signed> listSigned;
    private int alreadySigned;
    private int unSign;
    private int sum;
    private String[] sumReceiver;
    private List<Sign> listSign;
    private String tvAlreadySigned;
    private String tvUnsign;
    private String signNickName;
    private String signTime;
    private String signLoc;
    private String flag = "0";

    private String alreadySignName;
    private String alreadySignNameSum;
    private String unSignNameSum;
    List<Signed> list;
    List<V_sign_users> listSum;
    List<Signed> listRemain;
    List<Signed> handadd;
    List<Signed> signedList;
    V_sign_users v_sign_nickname;
    Signed signed;
    Sign sign;
    TitlePopup titlePopup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sign_person);
        ButterKnife.bind(this);
        setTitle("签到详情");
        getData();
        initListViewController();
        initView();
    }


    private void initView() {
        titlePopup = new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ActionItem action = new ActionItem(getResources().getDrawable(R.drawable.a1), "分享");
        ActionItem action1 = new ActionItem(getResources().getDrawable(R.drawable.a5), "删除公告");
        titlePopup.addAction(action);
        titlePopup.addAction(action1);
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                if (position == 0) {
                    String tittle = list.get(0).getSign_title();
                    String content = list.get(0).getSign_msg();
                    new ShaerHelper(CheckSignPersonActivity.this, tittle, content);
                } else{
                    showAlertDialog();
                }
            }
        });
        MenuImageView menuImageView = new MenuImageView(getMy());
        menuImageView.setBackgroundResource(R.drawable.more);
        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titlePopup.show(v);
            }
        });
        addMenuView(menuImageView);
    }

    public void showAlertDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("删除签到将会导致尚未看过的人员无法接收到此条签到");
        builder.setTitle("提示");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showLoading();
                new FinishRefreshDeleteSign(getMy()).execute();
            }
        });

        builder.setNegativeButton("否",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }


    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<Signed>(getMy(), mListItems, R.layout.item_sign_person_detail) {
            @Override
            public void convert(ViewHolder helper, Signed item) {

                alreadySignName = item.getNickname();
                alreadySignNameSum += alreadySignName + ",";
                if (StrUtils.IsNotEmpty(item.getNickname())) {
                    helper.setText(R.id.tv_check_user_name, item.getNickname());
                } else {
                    helper.setText(R.id.tv_check_user_name, "姓名");
                }
                if (StrUtils.IsNotEmpty(item.getCreate_time())) {
                    signTime = item.getCreate_time().substring(0, 16);
                    helper.setText(R.id.tv_check_time, signTime);
                } else {
                    helper.setText(R.id.tv_check_time, "未签到，时间未知");
                }
                if (StrUtils.IsNotEmpty(item.getLocation())) {
                    helper.setText(R.id.tv_check_loc, item.getLocation());
                } else {
                    helper.setText(R.id.tv_check_loc, "未签到，地点未知");
                }
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }

    @Override
    public void onListItemClick(int position) {
        if ("0".equals(flag)) {
            Signed signed = (Signed) commonAdapter.getItem(position);
            JwStartActivity(SignedDetailActivity.class, signed);
        }
    }

    //已签
    @OnClick(R.id.already_signed_change_counts)
    void wzxClick() {
        resetAll();
        alreadySignedCounts.setBackgroundResource(R.drawable.shape_blue);
        alreadySignedCounts.setTextColor(getResources().getColor(R.color.white));
        flag = "0";
        dataSourceClear(0, mListItems);
        onListViewHeadRefresh();
    }

    //未签
    @OnClick(R.id.unsign_change_counts)
    void yzxClick() {
        resetAll();
        unsignCounts.setBackgroundResource(R.drawable.shape_blue);
        unsignCounts.setTextColor(getResources().getColor(R.color.white));
        flag = "1";
        dataSourceClear(0, mListItems);
        onListViewHeadRefresh();
    }

    private void getData() {
        signedCode = getIntent().getStringExtra(StaticStrUtils.baseItem);
//        SqlInfo sqlInfo = new SqlInfo();
//        sqlInfo.setSql("SELECT " +
//                "*" +
//                " FROM " +
//                " signed " +
//                " where sign_code= ? " +
//                " GROUP BY " +
//                " sign_code, " +
//                " sign_user_code ");
//        sqlInfo.addValue(signedCode);
//        String sql = sqlInfo.getBuildSql();
//        JCloudDB jCloudDB = new JCloudDB();
//        jCloudDB.findAllBySql(sql, new FindAllListener<Signed>() {
//            @Override
//            public void onStart() {
//                showLoading();
//            }
//
//            @Override
//            public void onSuccess(List<Signed> t) {
//                if (ListUtils.IsNotNull(t)) {
//                    alreadySigned = t.size();
//                    tvAlreadySigned = "" + alreadySigned;
//
//                } else {
//                    tvAlreadySigned = "0";
//                }
//                alreadySignedCounts.setText("已签到" + tvAlreadySigned + "人");
//
//            }
//
//            @Override
//            public void onFinish() {
//                hideLoading();
//            }
//        });
//
//        String sqlSign = " sign_code = " +
//                StrUtils.QuotedStr(signedCode);
//        jCloudDB.findAllByWhere(sqlSign, new FindAllListener<Sign>() {
//            @Override
//            public void onStart() {
//                showLoading();
//            }
//
//            @Override
//            public void onSuccess(List<Sign> t) {
//                if (ListUtils.IsNotNull(t)) {
//                    sign = t.get(0);
//                    String receiceCode = sign.getReceive_code();
//                    if (receiceCode.contains(",")) {
//                        sumReceiver = receiceCode.split(",");
//                        sum = sumReceiver.length;
//                        unSign = sum - alreadySigned;
//                        tvUnsign = "" + unSign;
//                    } else {
//                        sum = 1;
//                        unSign = sum - alreadySigned;
//                        tvUnsign = "" + unSign;
//                    }
//                }
//                unsignCounts.setText("未签到" + tvUnsign + "人");
//            }
//
//            @Override
//            public void onFinish() {
//                hideLoading();
//            }
//        });

        new FinishRefreshCounts(getMy()).execute();
    }


    @Override
    public void onListViewHeadRefresh() {
        showLoading();
        new FinishRefresh(getMy()).execute();
    }

    @Override
    public void onListViewFooterRefresh() {
        showLoading();
        new FinishRefresh(getMy()).execute();
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
            SqlInfo sqlInfo1 = new SqlInfo();
//            sqlInfo1.setSql("SELECT * " +
////                    " nickname, " +
////                    " create_time, " +
////                    " location," +
////                    " sign_user_code" +
//                    " FROM " +
//                    " signed " +
//                    " where sign_code= ? " +
//                    " GROUP BY " +
//                    " sign_code, " +
//                    " sign_user_code ");
//            SELECT * from signed where create_time in (select max(create_time) from signed WHERE sign_code = '8b9c09c619674b798a7bcc75390e28cd' group by sign_user_code)
            sqlInfo1.setSql("SELECT * FROM signed where create_time in (select max(create_time) from signed WHERE sign_code =?" +
                    " group by sign_user_code ) " + " group by sign_user_code ");
            sqlInfo1.addValue(signedCode);
            String sql1 = sqlInfo1.getBuildSql();
            try {
                list = jCloudDB.findAllBySql(Signed.class, sql1);
                mListItems.clear();
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }


            SqlInfo sqlInfo2 = new SqlInfo();
            sqlInfo2.setSql("SELECT * " +
//                    " nickname, " +
//                    " create_time," +
//                    " user_code " +
                    " FROM " +
                    " v_sign_users " +
                    " where sign_code= ? " +
                    "GROUP BY user_code");
            sqlInfo2.addValue(signedCode);
            String sql2 = sqlInfo2.getBuildSql();
            try {
                listSum = jCloudDB.findAllBySql(V_sign_users.class, sql2);
                handadd = new ArrayList<Signed>();
                signedList = new ArrayList<Signed>();
                for (int i = 0; i < listSum.size(); i++) {
                    v_sign_nickname = listSum.get(i);
                    signed = new Signed();
                    signed.setNickname(v_sign_nickname.getNickname());
                    signed.setCreate_time("");
                    signed.setLocation("");
                    signed.setSign_code(v_sign_nickname.getSign_code());
                    signed.setSign_user_code(v_sign_nickname.getUser_code());
                    handadd.add(signed);
                    signedList.add(signed);

                }
                listRemain = removeList(handadd, list, signedList);
                mListItems.clear();


                for (Signed signed : list) {
                    //取头像
                    String signedCode = signed.getSign_code();
                    String uuid = signed.getUuid();
                    if (StrUtils.IsNotEmpty(uuid)) {
                        String sSql = "pic_code=?";
                        SqlInfo sqlInfo = new SqlInfo();
                        sqlInfo.setSql(sSql);
                        sqlInfo.addValue(uuid);
                        sSql = sqlInfo.getBuildSql();
                        List<Picture> pictureList = jCloudDB.findAllByWhere(Picture.class, sSql);
                        if (ListUtils.IsNotNull(pictureList)) {
                            StringBuffer stringBuffer = new StringBuffer();
                            for (int i = 0; i < pictureList.size(); i++) {
                                stringBuffer.append(pictureList.get(i).getPic_road() + ",");
                            }
                            signed.setPictureListSting(stringBuffer.toString());
                            Picture picture = pictureList.get(0);
                            String path = picture.getPic_road();
                            if (StrUtils.IsNotEmpty(path)) {
                                //存头像
                                signed.setPhoto_code(path);
                            }
                        }
                    }
                }
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            if ("0".equals(flag)) {
                if (ListUtils.IsNotNull(list)) {
                    result = "1";
                } else {
                    result = "0";
                }
            } else if ("1".equals(flag)) {
                if (ListUtils.IsNotNull(listRemain)) {
                    result = "1";
                } else {
                    result = "0";
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (ListUtils.IsNotNull(list)) {
                tvTitle.setText(list.get(0).getSign_title());
                tvSignedContent.setText(list.get(0).getSign_msg());
            } else {
                llSignedTitle.setVisibility(View.GONE);
                llSignedContent.setVisibility(View.GONE);
            }
            if (result.equals("1")) {
                if ("0".equals(flag)) {
                    mListItems.addAll(list);
                    commonAdapter.notifyDataSetChanged();
                } else if ("1".equals(flag)) {
                    mListItems.addAll(listRemain);
                    commonAdapter.notifyDataSetChanged();
                }
            } else {
                commonAdapter.notifyDataSetChanged();
                //没有加载到数据
            }
            listview.onRefreshComplete();
            hideLoading();
        }
    }


    private class FinishRefreshCounts extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefreshCounts(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }


        @Override
        protected String doInBackground(String... params) {
            String result = "0";
            SqlInfo sqlInfo = new SqlInfo();
            sqlInfo.setSql("SELECT " +
                    " sign_code, " +
                    " sign_user_code " +
                    " FROM " +
                    " signed " +
                    " where sign_code= ? " +
                    " GROUP BY " +
                    " sign_code, " +
                    " sign_user_code ");
            sqlInfo.addValue(signedCode);
            String sql = sqlInfo.getBuildSql();
            try {

                listSigned = jCloudDB.findAllBySql(Signed.class, sql);
                if (ListUtils.IsNotNull(listSigned)) {
                    alreadySigned = listSigned.size();
                    tvAlreadySigned = "" + alreadySigned;
                } else {
                    tvAlreadySigned = "0";
                }


                listSign = jCloudDB.findAllByWhere(Sign.class,
                        " sign_code = " +
                                StrUtils.QuotedStr(signedCode));
                if (ListUtils.IsNotNull(listSign)) {
                    sign = listSign.get(0);
                    String receiceCode = sign.getReceive_code();
                    if (receiceCode.contains(",")) {
                        sumReceiver = receiceCode.split(",");
                        sum = sumReceiver.length;
                        unSign = sum - alreadySigned;
                        tvUnsign = "" + unSign;
                    } else {
                        sum = 1;
                        unSign = sum - alreadySigned;
                        tvUnsign = "" + unSign;
                    }
                }
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            alreadySignedCounts.setText("已签到" + tvAlreadySigned + "人");
            unsignCounts.setText("未签到" + tvUnsign + "人");
            hideLoading();
        }
    }

    private class FinishRefreshDeleteSign extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefreshDeleteSign(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }


        @Override
        protected String doInBackground(String... params) {
            String result = "0";
            boolean flagSign = false;
            boolean flagSigned = false;
            try {
                flagSign = jCloudDB.deleteByWhere(Sign.class, " sign_code = " + StrUtils.QuotedStr(signedCode));
                flagSigned = jCloudDB.deleteByWhere(Signed.class, " sign_code = " + StrUtils.QuotedStr(signedCode));

            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }
            if (flagSign && flagSigned) {
                result = "1";
            } else {
                result = "0";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if ("1".equals(result)) {
                ToastShow("删除成功");
                finish();
                OttUtils.push("mysign_refresh", "");
            }
        }
    }


    private void resetAll() {
        alreadySignedCounts.setBackgroundResource(R.drawable.shape_white);
        alreadySignedCounts.setTextColor(getResources().getColor(R.color.list_text_color));
        unsignCounts.setBackgroundResource(R.drawable.shape_white);
        unsignCounts.setTextColor(getResources().getColor(R.color.list_text_color));
    }

    public List<Signed> removeList(List<Signed> list1, List<Signed> list2, List<Signed> list3) {
        List<Signed> list;
        Signed signed;
        for (int i = 0; i < list3.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                if (list3.get(i).getSign_user_code().equals(list2.get(j).getSign_user_code())) {
                    list1.remove(list3.get(i));
                    break;
//                    list1.remove(i);
//                    signed = new Signed();
//                    list1.add(i, signed);
                }
            }
        }
//        list = clearEmpty(list1);
        return list1;
    }

    private List<Signed> clearEmpty(List<Signed> list) {
        Signed signed;
        Iterator<Signed> listIterator = list.iterator();
        if (listIterator.hasNext()) {
            signed = listIterator.next();
            if (OUtils.IsNull(signed))
                list.remove(signed);
        }
        return list;
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
