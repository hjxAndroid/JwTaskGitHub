package com.jeeweel.syl.jwtask.business.main.module.task;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import java.util.HashMap;

import api.adapter.GridViewAdapter;
import api.util.Contants;
import api.util.OttUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FzSortActivity extends JwActivity {


    @Bind(R.id.tv_wqr)
    TextView tvWqr;
    @Bind(R.id.tv_yqr)
    TextView tvYqr;
    @Bind(R.id.tv_wsh)
    TextView tvWsh;
    @Bind(R.id.tv_ysh)
    TextView tvYsh;
    @Bind(R.id.tv_shbh)
    TextView tvShbh;
    @Bind(R.id.tv_yqsqz)
    TextView tvYqsqz;
    @Bind(R.id.tv_yyq)
    TextView tvYyq;
    @Bind(R.id.tv_yqbh)
    TextView tvYqbh;
    @Bind(R.id.tv_fqsqz)
    TextView tvFqsqz;
    @Bind(R.id.tv_yfq)
    TextView tvYfq;
    @Bind(R.id.tv_fqbh)
    TextView tvFqbh;
    private Activity context;
    MenuTextView menuTextView;
    Intent intent = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fz_sort);
        setTitle("我负责");
        context = this;
        ButterKnife.bind(this);
        intent = new Intent(context, JobFzListActivity.class);
        initRight();
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("全部");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent1 = new Intent(context, FzAllListActivity.class);
                intent1.putExtra(StaticStrUtils.baseItem, "0");
                intent1.putExtra("title", "全部");
                startActivity(intent1);
            }
        });
        addMenuView(menuTextView);
    }


    @OnClick(R.id.tv_wqr)
    void wqrClick() {
        //未确认
        intent.putExtra(StaticStrUtils.baseItem, "0");
        intent.putExtra("title", "未确认");
        startActivity(intent);
    }
    @OnClick(R.id.tv_yqr)
    void yqrClick() {
        intent.putExtra(StaticStrUtils.baseItem, "1");
        intent.putExtra("title", "已确认");
        startActivity(intent);
    }
    @OnClick(R.id.tv_wsh)
    void wshClick() {
        //未审核
        intent.putExtra(StaticStrUtils.baseItem, "2");
        intent.putExtra("title", "未审核");
        startActivity(intent);
    }
    @OnClick(R.id.tv_ysh)
    void yshClick() {
        //已审核
        intent.putExtra(StaticStrUtils.baseItem, "3");
        intent.putExtra("title", "已审核");
        startActivity(intent);
    }
    @OnClick(R.id.tv_shbh)
    void shbhClick() {
        //审核驳回
        intent.putExtra(StaticStrUtils.baseItem, "10");
        intent.putExtra("title", "审核驳回");
        startActivity(intent);
    }
    @OnClick(R.id.tv_yqsqz)
    void yqsqzClick() {
        //延期申请中
        intent.putExtra(StaticStrUtils.baseItem, "4");
        intent.putExtra("title", "延期申请中");
        startActivity(intent);
    }
    @OnClick(R.id.tv_yyq)
    void yyqClick() {
        //已延期
        intent.putExtra(StaticStrUtils.baseItem, "5");
        intent.putExtra("title", "已延期");
        startActivity(intent);
    }
    @OnClick(R.id.tv_yqbh)
    void yqbhClick() {
        //延期驳回
        intent.putExtra(StaticStrUtils.baseItem, "6");
        intent.putExtra("title", "延期驳回");
        startActivity(intent);
    }
    @OnClick(R.id.tv_fqsqz)
    void fqsqzClick() {
        //放弃申请中
        intent.putExtra(StaticStrUtils.baseItem, "7");
        intent.putExtra("title", "放弃申请中");
        startActivity(intent);
    }
    @OnClick(R.id.tv_yfq)
    void yfqClick() {
        //已放弃
        intent.putExtra(StaticStrUtils.baseItem, "8");
        intent.putExtra("title", "已放弃");
        startActivity(intent);
    }
    @OnClick(R.id.tv_fqbh)
    void fqbhClick() {
        //放弃驳回
        intent.putExtra(StaticStrUtils.baseItem, "9");
        intent.putExtra("title", "放弃驳回");
        startActivity(intent);
    }

    @OnClick(R.id.tv_stn)
    void stnClick() {
        JwStartActivity(OverTaskListActivity.class);
    }
    @OnClick(R.id.tv_ydq)
    void ydqClick() {
//已超期
        JwStartActivity(OveredTaskListActivity.class);

    }

}
