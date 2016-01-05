package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;

import com.google.gson.Gson;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Alreadyread;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.V_publicityunread;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicReadOpotionActivity extends JwActivity {

    @Bind(R.id.bt_yd)
    Button btYd;
    @Bind(R.id.bt_wd)
    Button btWd;
    V_publicityunread publicity;
    List<Userorg> orgunits;
    List<Userorg> alreadyreads;
    List<Userorg> unReads = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_read_opotion);
        ButterKnife.bind(this);
        setTitle("已读未读情况");
        publicity = (V_publicityunread)getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        showLoading();
        new OrgPerson(getMy()).execute();
    }

    @OnClick(R.id.bt_yd)
    void ydClick() {
        if(ListUtils.IsNotNull(alreadyreads)){
            String json = new Gson().toJson(alreadyreads);
            JwStartActivity(PublicyReadListActivity.class,json);
        }
    }

    @OnClick(R.id.bt_wd)
    void wdClick() {
        if(ListUtils.IsNotNull(unReads)){
            String json = new Gson().toJson(unReads);
            JwStartActivity(PublicyUnReadListActivity.class,json);
        }
    }
    /**
     * 查看总人数
     */
    private class OrgPerson extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public OrgPerson(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            if (null != publicity) {
                try {
//                    //总人数
//                    orgunits = jCloudDB.findAllByWhere(Userorg.class,
//                            "org_code = " + StrUtils.QuotedStr(publicity.getAccept_org_code()));
                    //已读人数
//                    alreadyreads = jCloudDB.findAllByWhere(Alreadyread.class,
//                            "org_code = " + StrUtils.QuotedStr(publicity.getAccept_org_code()) +"and task_code=" +StrUtils.QuotedStr(publicity.getPublicity_code()));


                    //已读人数
                    String readSql  = "SELECT t.* from userorg t where t.user_code in (SELECT c.operator_code from alreadyread c where c.org_code = "+StrUtils.QuotedStr(publicity.getAccept_org_code())+" and c.task_code = "+StrUtils.QuotedStr(publicity.getPublicity_code())+")";
                    //查找数据
                    alreadyreads = jCloudDB.findAllBySql(Userorg.class, readSql);
                    removeDuplicate(alreadyreads);

                    //未读人数
//                    for (int i = 0; i < orgunits.size(); i++) {
//                        for (int j = 0 ; j< alreadyreads.size();j++) {
//                            if (orgunits.get(i).getUser_code().equals(alreadyreads.get(i).getOperator_code())) {
//                                unReads.add(orgunits.get(i));
//                            }
//                        }
//                    }
                    //未读人数
                    String sql  = "SELECT t.* from userorg t where t.user_code not in (SELECT c.operator_code from alreadyread c where c.org_code = "+StrUtils.QuotedStr(publicity.getAccept_org_code())+" and c.task_code = "+StrUtils.QuotedStr(publicity.getPublicity_code())+")";
                    //查找数据
                    unReads = jCloudDB.findAllBySql(Userorg.class, sql);
                    removeDuplicate(unReads);
                } catch (CloudServiceException e) {
                    result = "0";
                    e.printStackTrace();
                }

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
        }

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
