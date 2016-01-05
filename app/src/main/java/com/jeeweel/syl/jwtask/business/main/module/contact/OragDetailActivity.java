package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Dept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.more.MineActivity;
import com.jeeweel.syl.jwtask.business.main.module.more.MineEditnameActivity;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;

import java.util.List;

import api.util.Contants;
import api.util.OttUtils;
import api.view.CustomDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OragDetailActivity extends JwActivity {

    @Bind(R.id.tv_zzm)
    TextView tvZzm;
    @Bind(R.id.tv_cjr)
    TextView tvCjr;
    @Bind(R.id.tv_cjsj)
    TextView tvCjsj;
    @Bind(R.id.bt_add)
    Button btAdd;


    Orgunit orgunit;

    String orgCode;
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orag_detail);
        ButterKnife.bind(this);
        setTitle("组织详情");
        users = JwAppAplication.getInstance().getUsers();
        orgunit = (Orgunit) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        getData();
        initRight();
    }

    private void getData() {
        if (null != orgunit) {
            orgCode = orgunit.getOrg_code();
            showLoading();
            new GetData(getMy()).execute();
        }
    }

    @OnClick(R.id.bt_add)
    void addClick() {
        Intent intent = new Intent(getMy(), AddDeptActivity.class);
        intent.putExtra(StaticStrUtils.baseItem, orgunit.getOrg_name());
        intent.putExtra("org_code", orgunit.getOrg_code());
        startActivity(intent);
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("解散");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showAlertDialog();
            }
        });
        addMenuView(menuTextView);
    }


    public void showAlertDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("您确定要解散该组织");
        builder.setTitle("提示");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showLoading();
                new FinishRefreshDismiss(getMy()).execute();
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

    private class FinishRefreshDismiss extends AsyncTask<String, Void, String> {
        private Context context;
        List<Orgunit> orgunits;

        /**
         * @param context 上下文
         */
        public FinishRefreshDismiss(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "1";


            boolean flagUserDept = false;
            boolean flagUserOrg = false;
            boolean flagDept = false;
            boolean flagOrgUnit = false;
            JCloudDB jCloudDB = new JCloudDB();
            try {
                if (null != users) {
                    orgunits = jCloudDB.findAllByWhere(Orgunit.class, " org_code = " + StrUtils.QuotedStr(orgCode) +
                            "and founder_code =" + StrUtils.QuotedStr(users.getUser_code()));
                    if (ListUtils.IsNotNull(orgunits)) {

                        flagUserDept = jCloudDB.deleteByWhere(Userdept.class, " org_code = " + "\'" + orgCode + "\'");
                        flagUserOrg = jCloudDB.deleteByWhere(Userorg.class, " org_code = " + "\'" + orgCode + "\'");
                        flagDept = jCloudDB.deleteByWhere(Dept.class, " org_code = " + "\'" + orgCode + "\'");
                        flagOrgUnit = jCloudDB.deleteByWhere(Orgunit.class, " org_code = " + "\'" + orgCode + "\'");
                        if (flagUserDept && flagUserOrg && flagDept && flagOrgUnit) {
                            result = "1";
                        } else {
                            result = "0";
                        }
                    } else {
                        result = "2";
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
                ToastShow("解散成功");
                OttUtils.push("deptAdd_refresh", "");
            } else if (result.equals("2")) {
                ToastShow("您没有权限解散组织");
            } else {
                ToastShow("解散失败");
            }
            hideLoading();
        }
    }

    private class GetData extends AsyncTask<String, Void, String> {
        private Context context;
        List<Orgunit> orgunits;
        JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public GetData(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "1";
            try {
                orgunits = jCloudDB.findAllByWhere(Orgunit.class, " org_code = " + StrUtils.QuotedStr(orgCode));
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                if (ListUtils.IsNotNull(orgunits)) {
                    orgunit = orgunits.get(0);
                    tvZzm.setText(StrUtils.IsNull(orgunit.getOrg_name()));
                    tvCjr.setText(StrUtils.IsNull(orgunit.getNickname()));
                    tvCjsj.setText(StrUtils.IsNull(orgunit.getCreate_time()));
                }
            }
            hideLoading();
        }
    }


    private class FinishRefresIsFounder extends AsyncTask<String, Void, String> {
        private Context context;
        List<Orgunit> orgunits;

        /**
         * @param context 上下文
         */
        public FinishRefresIsFounder(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "1";


            JCloudDB jCloudDB = new JCloudDB();
            try {
                if (null != users) {
                    orgunits = jCloudDB.findAllByWhere(Orgunit.class, " org_code = " + StrUtils.QuotedStr(orgCode) +
                            "and founder_code =" + StrUtils.QuotedStr(users.getUser_code()));
                    if (ListUtils.IsNotNull(orgunits)) {
                        result = "1";
                    } else {
                        result = "0";
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
                Intent intent = new Intent();
                intent.putExtra("title", "修改组织名");
                intent.putExtra("code", orgunit.getOrg_code());
                intent.setClass(OragDetailActivity.this, MineEditnameActivity.class);
                JwStartActivity(intent);
            } else {
                ToastShow("您没有权限修改");
            }
            hideLoading();
        }
    }

    @OnClick(R.id.ll_oragname)
    void ll_oragnameClick() {
        new FinishRefresIsFounder(getMy()).execute();
    }

    @OnClick(R.id.rea_members)
    void rea_membersClick() {
        JwStartActivity(OrgMembersActivity.class, orgCode);
    }


    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (StrUtils.IsNotEmpty(msg) && msg.equals("org_name_refresh")) {
            getData();
        }
    }
}
