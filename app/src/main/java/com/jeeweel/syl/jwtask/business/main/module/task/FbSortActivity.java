package com.jeeweel.syl.jwtask.business.main.module.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import java.util.ArrayList;
import java.util.List;

import api.adapter.GridViewAdapter;
import api.view.LineGridView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class FbSortActivity extends JwActivity {


    @Bind(R.id.line_gv)
    LineGridView lineGv;
    @Bind(R.id.chaoqi_gv)
    LineGridView chaoqiGv;
    private String[] data;

    private String[] chaoData;

    private ArrayList<Integer> localImages = new ArrayList<Integer>();
    private List<String> networkImages;
    private String[] images;

    private AlertDialog dialog;

    List<Userorg> mListItems;

    private Activity context;
    MenuTextView menuTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_sort);
        setTitle("我发布");
        context = this;
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        self = this;


        data = getResources().getStringArray(R.array.fz_array);
        TypedArray imagesArrays = getResources().obtainTypedArray(
                R.array.fz_img_array);
        GridViewAdapter gridViewAdapter = new GridViewAdapter(getMy(), data,
                imagesArrays, R.layout.item_home);
        lineGv.setAdapter(gridViewAdapter);
        final Intent intent = new Intent(context, JobListActivity.class);
        lineGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        //未确认
                        intent.putExtra(StaticStrUtils.baseItem, "0");
                        intent.putExtra("title", "未确认");
                        startActivity(intent);
                        break;
                    case 1:
                        //签到
                        intent.putExtra(StaticStrUtils.baseItem, "1");
                        intent.putExtra("title", "已确认");
                        startActivity(intent);
                        break;
                    case 2:
                        //未审核
                        intent.putExtra(StaticStrUtils.baseItem, "2");
                        intent.putExtra("title", "未审核");
                        startActivity(intent);
                        break;
                    case 3:
                        //已审核
                        intent.putExtra(StaticStrUtils.baseItem, "3");
                        intent.putExtra("title", "已审核");
                        startActivity(intent);
                        break;
                    case 4:
                        //审核驳回
                        intent.putExtra(StaticStrUtils.baseItem, "10");
                        intent.putExtra("title", "审核驳回");
                        startActivity(intent);
                        break;
                    case 5:
                        //延期申请中
                        intent.putExtra(StaticStrUtils.baseItem, "4");
                        intent.putExtra("title", "延期申请中");
                        startActivity(intent);
                        break;
                    case 6:
                        //已延期
                        intent.putExtra(StaticStrUtils.baseItem, "5");
                        intent.putExtra("title", "已延期");
                        startActivity(intent);
                        break;
                    case 7:
                        //延期驳回
                        intent.putExtra(StaticStrUtils.baseItem, "6");
                        intent.putExtra("title", "延期驳回");
                        startActivity(intent);
                        break;
                    case 8:
                        //放弃申请中
                        intent.putExtra(StaticStrUtils.baseItem, "7");
                        intent.putExtra("title", "放弃申请中");
                        startActivity(intent);
                        break;
                    case 9:
                        //已放弃
                        intent.putExtra(StaticStrUtils.baseItem, "8");
                        intent.putExtra("title", "已放弃");
                        startActivity(intent);
                        break;
                    case 10:
                        //放弃驳回
                        intent.putExtra(StaticStrUtils.baseItem, "9");
                        intent.putExtra("title", "放弃驳回");
                        startActivity(intent);
                        break;
                    default:
                        break;
                }

            }
        });


        chaoData = getResources().getStringArray(R.array.chao_array);
        TypedArray chaoImages = getResources().obtainTypedArray(
                R.array.caho_img_array);
        GridViewAdapter chaoAdapter = new GridViewAdapter(getMy(), chaoData,
                chaoImages, R.layout.item_home);
        chaoqiGv.setAdapter(chaoAdapter);

        chaoqiGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        //即将超期
                        JwStartActivity(OverTaskListActivity.class);
                        break;
                    case 1:
                        //已超期
                        JwStartActivity(OveredTaskListActivity.class);
                        break;
                    default:
                        break;
                }

            }
        });

    }


}
