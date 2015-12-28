package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Publicity;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.config.jsonclass.V_publicityunread;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;

import java.util.List;

import api.view.GridNoScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ragn on 2015/12/24.
 */
public class ChangePublicyActivity extends JwActivity {
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.li)
    LinearLayout li;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.noScrollgridview)
    GridNoScrollView noScrollgridview;
    @Bind(R.id.li_fb)
    LinearLayout liFb;

    private List<Publicity> list;
    private V_publicityunread vPublicityunrea;
    private Publicity publicity;
    private String title;
    private String content;
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_publicy);
        ButterKnife.bind(this);
        users = JwAppAplication.getInstance().getUsers();
        getDate();
        initRight();
    }


    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成修改");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                title = etTitle.getText().toString();
                content = etContent.getText().toString();
                new FinishRefreshChangeClick(getMy()).execute();
            }
        });
        addMenuView(menuTextView);
    }


    private void getDate() {
        vPublicityunrea = (V_publicityunread) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
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
            try {
                list = jCloudDB.findAllByWhere(Publicity.class, " publicity_code = " + "\'" + vPublicityunrea.getPublicity_code() + "\'" + " and prouser_code = " + "\'" + users.getUser_code() + "\'");
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
                publicity = list.get(0);
                etTitle.setText(publicity.getPublicity_title());
                etContent.setText(publicity.getPublicity_content());
            }
            hideLoading();
        }
    }

    /**
     * 保存到数据库
     */
    private class FinishRefreshChangeClick extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefreshChangeClick(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";
            SqlInfo sqlInfo = new SqlInfo();
            sqlInfo.setSql(" update publicity set publicity_title = ? , publicity_content = ? where publicity_code = ? and prouser_code = ?");
            sqlInfo.addValue(title);
            sqlInfo.addValue(content);
            sqlInfo.addValue(vPublicityunrea.getPublicity_code());
            sqlInfo.addValue(users.getUser_code());
            String sql = sqlInfo.getBuildSql();
            try {
                if (!(CloudDB.execSQL(sql))) {
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
                ToastShow("修改成功");
            } else {
                ToastShow("修改失败");
            }
            finish();
            hideLoading();
        }
    }

}
