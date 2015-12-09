package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import java.util.List;

import api.adapter.GridViewAdapter;
import api.view.LineGridView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class TaskJobHomeActivity extends JwActivity {

    @Bind(R.id.line_gv)
    LineGridView lineGv;
    private String[] data;

    List<Userorg> mListItems;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_job_home);
        setTitle(getString(R.string.任务));
        ButterKnife.bind(this);
        initView();
        initRight();

    }

    private void initRight(){
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("发布");
        menuTextView.setTextColor(getResources().getColor(R.color.white));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                  JwStartActivity(JobAddActivity.class);
            }
        });
        addMenuView(menuTextView);
    }


    private void initView() {
        self = this;


        data = getResources().getStringArray(R.array.job_home_array);
        TypedArray imagesArrays = getResources().obtainTypedArray(
                R.array.job_home_image_array);
        GridViewAdapter gridViewAdapter = new GridViewAdapter(getMy(), data,
                imagesArrays, R.layout.item_home);
        lineGv.setAdapter(gridViewAdapter);

        lineGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        //我发布的
                        JwStartActivity(JobListActivity.class);
                        break;
                    case 1:
                        //我审核的
                        JwStartActivity(JobShListActivity.class);
                        break;
                    case 2:
                        //我参与的
                        JwStartActivity(JobSdListActivity.class);
                        break;
                    case 3:
                        //我观察的
                        JwStartActivity(JobGcListActivity.class);
                        break;
                    default:
                        break;
                }

            }
        });
    }


}
