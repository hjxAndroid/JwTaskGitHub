package com.jeeweel.syl.jwtask.business.main.module.task;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.integer.IntUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SolveGiveUpDetail extends JwActivity {

    Task task;
    int flag = 0;
    Users users;
    @Bind(R.id.tv_rmw)
    TextView tvRmw;
    @Bind(R.id.tv_kssj)
    TextView tvKssj;
    @Bind(R.id.tv_jiesusj)
    TextView tvJiesusj;
    @Bind(R.id.tv_rwyq)
    TextView tvRwyq;
    @Bind(R.id.tv_fqly)
    TextView tvFqly;
    @Bind(R.id.tv_bhly)
    TextView tvBhly;
    @Bind(R.id.degree_score)
    TextView degreeScore;

    private String bhly;
    private String score;

    private AlertDialog dialog;

    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_give_up_detail);
        ButterKnife.bind(this);
        setTitle("放弃审核详情");
        users = JwAppAplication.getInstance().getUsers();
        getData();
    }

    private void getData() {
        task = (Task) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        if (null != task) {
            tvRmw.setText(StrUtils.IsNull(task.getTask_name()));
            tvKssj.setText(StrUtils.IsNull(task.getBegin_time()));
            tvJiesusj.setText(StrUtils.IsNull(task.getOver_time()));
            tvRwyq.setText(StrUtils.IsNull(task.getTask_request()));
            tvFqly.setText(StrUtils.IsNull(task.getGive_up_content()));
            tvBhly.setText(StrUtils.IsNull(task.getReject_content()));
            degreeScore.setText(IntUtils.toStr(task.getDegree_score()));
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
