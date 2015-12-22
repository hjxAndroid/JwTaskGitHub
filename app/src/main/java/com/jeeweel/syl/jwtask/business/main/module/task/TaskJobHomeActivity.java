package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Alreadyread;
import com.jeeweel.syl.jwtask.business.config.jsonclass.JobItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.News;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import java.util.ArrayList;
import java.util.List;

import api.adapter.GridViewAdapter;
import api.adapter.JobGridAdapter;
import api.util.OttUtils;
import api.view.LineGridView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class TaskJobHomeActivity extends JwActivity {

    @Bind(R.id.line_gv)
    LineGridView lineGv;
    private String[] data;

    List<Userorg> mListItems;
    List<JobItem> jobItems = new ArrayList<>();
    News news;

    Users users;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_job_home);
        setTitle("工作");
        ButterKnife.bind(this);
        getData();
        initView();
        initRight();

    }

    private void getData() {
        users = JwAppAplication.getInstance().getUsers();
        news = (News) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("发布");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
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

        if(news!=null){
            String readState = news.getReadstate();
            //已读
            if(readState.equals("1")){
                news.setAuditor_state("0");
                news.setPrincipal_state("0");
                news.setParticipant_state("0");
                news.setObserver_state("0");
            }
        }

        //从首页过来
        if(null==news){
            news = new News();
            news.setAuditor_state("0");
            news.setPrincipal_state("0");
            news.setParticipant_state("0");
            news.setObserver_state("0");
        }

        JobItem jobItem = new JobItem();
        jobItem.setData(data[0]);
        jobItem.setNews_state("0");
        jobItems.add(jobItem);

        JobItem jobItem1 = new JobItem();
        jobItem1.setData(data[1]);
        jobItem1.setNews_state(news.getAuditor_state());
        jobItems.add(jobItem1);

        JobItem jobItem2 = new JobItem();
        jobItem2.setData(data[2]);
        jobItem2.setNews_state(news.getPrincipal_state());
        jobItems.add(jobItem2);

        JobItem jobItem3 = new JobItem();
        jobItem3.setData(data[3]);
        jobItem3.setNews_state(news.getParticipant_state());
        jobItems.add(jobItem3);

        JobItem jobItem4 = new JobItem();
        jobItem4.setData(data[4]);
        jobItem4.setNews_state(news.getObserver_state());
        jobItems.add(jobItem4);



        TypedArray imagesArrays = getResources().obtainTypedArray(
                R.array.job_home_image_array);
        JobGridAdapter gridViewAdapter = new JobGridAdapter(getMy(), jobItems,
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
                        //我负责的
                        JwStartActivity(JobFzListActivity.class);
                        break;
                    case 3:
                        //我参与的
                        JwStartActivity(JobCyListActivity.class);
                        break;
                    case 4:
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
