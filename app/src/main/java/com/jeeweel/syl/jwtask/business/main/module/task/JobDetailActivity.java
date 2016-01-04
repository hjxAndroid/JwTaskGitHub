package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Alreadyread;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.imagedemo.image.ImagePagerActivity;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.basic.GetUserPicture;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.DateHelper;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import api.util.Contants;
import api.util.OttUtils;
import api.util.Utils;
import api.view.CustomDialog;
import api.view.GridNoScrollView;
import api.view.ListNoScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class JobDetailActivity extends JwActivity {

    @Bind(R.id.iv_xz)
    CircleImageView ivXz;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.tv_now_state)
    TextView tvNowState;
    @Bind(R.id.tv_task_name)
    TextView tvTaskName;
    @Bind(R.id.tv_start_time)
    TextView tvStartTime;
    @Bind(R.id.tv_end_time)
    TextView tvEndTime;
    @Bind(R.id.tv_fbr)
    TextView tvFbr;
    @Bind(R.id.tv_fzr)
    TextView tvFzr;
    @Bind(R.id.tv_shr)
    TextView tvShr;
    @Bind(R.id.tv_cyz)
    TextView tvCyz;
    @Bind(R.id.tv_gcz)
    TextView tvGcz;
    @Bind(R.id.tv_task_yq)
    TextView tvTaskYq;
    @Bind(R.id.tv_yxj)
    TextView tvYxj;
    @Bind(R.id.tv_khbz)
    TextView tvKhbz;
    @Bind(R.id.listview)
    ListNoScrollView listview;

    @Bind(R.id.bt_qrjs)
    Button btQrjs;
    @Bind(R.id.bt_djsh)
    Button btDjsh;
    @Bind(R.id.bt_sqyq)
    Button btSqyq;
    @Bind(R.id.bt_fqrw)
    Button btFqrw;
    @Bind(R.id.li_bt)
    LinearLayout liBt;
    @Bind(R.id.noScrollgridview)
    GridNoScrollView noScrollgridview;

    private Users users;

    /**
     * 任务主键
     */
    private Task task;
    String flag;
    CommonAdapter commonAdapter;
    List<Taskflow> taskflows;
    String orgCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        ButterKnife.bind(this);
        setTitle("任务详情");
        users = JwAppAplication.getInstance().getUsers();
        setData();
    }

    private void setData() {
        flag = getIntent().getStringExtra("flag");
        if (StrUtils.IsNotEmpty(flag)) {
            liBt.setVisibility(View.GONE);
        }

        orgCode = (String) SharedPreferencesUtils.get(getMy(), Contants.org_code, "");

        task = (Task) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        if (null != task) {

            if (users != null) {
                new GetUserPicture(getMy(), ivXz, users.getUser_code()).execute();
            }

            tvNickname.setText(StrUtils.IsNull(task.getNickname()));
            tvNowState.setText(StrUtils.IsNull(task.getNow_state_name()));
            tvTaskName.setText(StrUtils.IsNull(task.getTask_name()));
            tvStartTime.setText(StrUtils.IsNull(task.getBegin_time()));
            tvEndTime.setText(StrUtils.IsNull(task.getOver_time()));

            tvTaskYq.setText(StrUtils.IsNull(task.getTask_request()));
            tvYxj.setText(StrUtils.IsNull(task.getPriority()));
            tvKhbz.setText(StrUtils.IsNull(task.getDegree()));

            int state = task.getNow_state();

            switch (state) {
                case 0:
                    btDjsh.setClickable(false);
                    break;
                case 1:
                    btQrjs.setText("已确认");
                    btQrjs.setClickable(false);
                    btSqyq.setClickable(true);
                    break;
                case 2:
                    btQrjs.setText("已确认");
                    btQrjs.setClickable(false);
                    btSqyq.setClickable(false);

                    btDjsh.setText("已递交");
                    btDjsh.setClickable(false);
                    btSqyq.setClickable(false);
                    break;
                case 3:
                    //已审核,任务已完结
                    liBt.setVisibility(View.GONE);
                    break;
                case 4:
                    btQrjs.setText("已确认");
                    btQrjs.setClickable(false);
                    btSqyq.setText("延期申请中");
                    break;
                case 5:
                    btQrjs.setText("已确认");
                    btQrjs.setClickable(false);

                    btSqyq.setClickable(false);
                    btSqyq.setText("延期通过");
                    break;
                case 6:
                    btQrjs.setText("已确认");
                    btQrjs.setClickable(false);

                    btSqyq.setClickable(false);
                    btSqyq.setText("延期驳回");
                    break;
                case 7:
                    btFqrw.setClickable(false);
                    btFqrw.setText("放弃申请中");
                    //其他按钮都不可点
                    btQrjs.setClickable(false);
                    btDjsh.setClickable(false);
                    btSqyq.setClickable(false);
                    break;
                case 8:
                    //任务已放弃
                    liBt.setVisibility(View.GONE);
                    break;
                case 9:
                    btFqrw.setClickable(false);
                    btFqrw.setText("放弃驳回");
                    //其他按钮都恢复可点
                    btQrjs.setClickable(true);
                    btDjsh.setClickable(true);
                    btSqyq.setClickable(true);
                    break;
                default:
                    break;
            }

            showLoading();
            new FinishRefresh(getMy()).execute();
        }

    }


    @OnClick(R.id.bt_qrjs)
    void qrjsClick() {
        new changeTask(getMy()).execute();
    }

    @OnClick(R.id.bt_djsh)
    void djshClick() {
        JwStartActivity(SendShActivity.class, task);
    }

    @OnClick(R.id.bt_sqyq)
    void sqyqClick() {
        JwStartActivity(ApplyDelayActivity.class, task);
    }

    @OnClick(R.id.bt_fqrw)
    void fqrwClick() {
        JwStartActivity(ApplyGiveUpActivity.class, task);
    }

    @OnClick(R.id.tv_pl)
    void commitClick() {
        Intent intent = new Intent(this, CommitListActivity.class);
        intent.putExtra(StaticStrUtils.baseItem, task);
        if (StrUtils.IsNotEmpty(flag) && flag.equals("gc")) {
            intent.putExtra("flag", "gc");
        }
        startActivity(intent);
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;
        List<Task> tasks;
        List<Picture> pictureList;
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
                if (null != task) {
                    String unid = Utils.getUUid();
                    String sql = "call get_task_detail('" + unid + "','" + task.getTask_code() + "');";
                    CloudDB.execSQL(sql);

                    String newSql = "select * from tmp" + unid;
                    //查找数据
                    tasks = jCloudDB.findAllBySql(Task.class, newSql);

                    String deletSql = "DROP TABLE tmp" + unid;
                    CloudDB.execSQL(deletSql);


                    List<Alreadyread> alreadyreadList = jCloudDB.findAllByWhere(Alreadyread.class,
                            "task_code=" + StrUtils.QuotedStr(task.getTask_code()) + "and operator_code=" + StrUtils.QuotedStr(users.getUser_code()) + "and org_code=" + StrUtils.QuotedStr(orgCode));
                    if (ListUtils.IsNull(alreadyreadList)) {
                        //已读表未插入，插入到已读表
                        Alreadyread alreadyread = new Alreadyread();
                        alreadyread.setTask_code(task.getTask_code());
                        alreadyread.setOperator_code(users.getUser_code());
                        alreadyread.setOrg_code(orgCode);
                        alreadyread.setOperate_type("2");
                        jCloudDB.save(alreadyread);
                    }

                    pictureList = jCloudDB.findAllByWhere(Picture.class,
                            "pic_code=" + StrUtils.QuotedStr(task.getTask_code()+"1"));
                }

                String newSql = "select * from  v_taskflow where task_code= " + StrUtils.QuotedStr(task.getTask_code()) + "ORDER BY create_time DESC";
                //查找数据
                taskflows = jCloudDB.findAllBySql(Taskflow.class, newSql);
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                OttUtils.push("news_refresh", "");

                //展现图片
                 if (ListUtils.IsNotNull(pictureList)) {
                    final String imgs[] = new String[pictureList.size()];
                    for(int i = 0; i<pictureList.size();i++){
                        imgs[i] = Utils.getPicUrl() + pictureList.get(i).getPic_road();
                    }

                    CommonAdapter commonAdapter1 = new CommonAdapter<Picture>(getMy(), pictureList, R.layout.item_img) {
                        @Override
                        public void convert(ViewHolder helper, Picture item) {
                            ImageView imageView = helper.getImageView(R.id.img);
                            JwImageLoader.displayImage(Utils.getPicUrl() + item.getPic_road(), imageView);
                        }
                    };
                    noScrollgridview.setAdapter(commonAdapter1);
                    noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(imgs.length!=0){
                                Intent intent = new Intent(getMy(),ImagePagerActivity.class);
                                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, imgs);
                                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                                getMy().startActivity(intent);
                            }
                        }
                    });
                }


                if (ListUtils.IsNotNull(tasks)) {
                    Task task = tasks.get(0);
                    tvFbr.setText(task.getPromulgator_nickname());
                    tvFzr.setText(task.getPrincipal_nickname());
                    tvShr.setText(task.getAuditor_nickname());
                    tvCyz.setText(task.getParticipant_nickname());
                    tvGcz.setText(task.getObserver_nickname());
                }


                commonAdapter = new CommonAdapter<Taskflow>(getMy(), taskflows, R.layout.item_task_detail) {
                    @Override
                    public void convert(ViewHolder helper, Taskflow item) {
                        helper.setText(R.id.tv_nickname, item.getNickname());
                        helper.setText(R.id.tv_action, item.getUser_action());
                        helper.setText(R.id.tv_time, item.getCreate_time());

                        ImageView imageView = helper.getImageView(R.id.iv_xz);
                        JwImageLoader.getImageLoader().displayImage(Utils.getPicUrl() + item.getPic_road(), imageView);
                    }
                };
                listview.setAdapter(commonAdapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Taskflow taskflow = (Taskflow) commonAdapter.getItem(position);
                        int state = taskflow.getNow_state();
                        switch (state) {
                            case 2:
                                //已递交，未审核，查看自己提交的完成情况信息
                                JwStartActivity(MyJobDetailActivity.class,task);
                                break;
                            case 3:
                                //已审核，查看审核情况
                                JwStartActivity(YshActivity.class, taskflow.getTask_code());
                                break;
                            case 4:
                                //延期申请中，查看自己的延期信息
                                JwStartActivity(SolveDelayActivity.class, taskflow.getTask_code());
                                break;
                            case 7:
                                //放弃申请中，查看自己的放弃信息
                                Intent intent = new Intent(getMy(), SolveGiveUpActivity.class);
                                intent.putExtra("flag", "1");
                                intent.putExtra(StaticStrUtils.baseItem, task);
                                startActivity(intent);
                                break;
                            case 8:
                                JwStartActivity(SolveGiveUpDetail.class, task);
                                break;
                            case 9:
                                JwStartActivity(SolveGiveUpDetail.class, task);
                                break;
                            default:
                                break;
                        }
                    }
                });
            } else {
                ToastShow("保存失败");
            }
            hideLoading();
        }
    }


    /**
     * 改变任务表状态
     */
    private class changeTask extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public changeTask(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            try {
                if (null != users) {
                    if (null != task) {
                        String sql = "update task set now_state = 1 , now_state_name = '已确认',confirm_time =" + StrUtils.QuotedStr(DateHelper.getCurDateTime()) + "  where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and principal_code like " + StrUtils.QuotedStrLike(users.getUser_code());
                        CloudDB.execSQL(sql);

                        task.setConfirm_time(DateHelper.getCurDateTime());
                        //保存到流程表里
                        Taskflow taskflow = new Taskflow();
                        taskflow.setUser_code(users.getUser_code());
                        taskflow.setNickname(users.getNickname());
                        taskflow.setTask_code(task.getTask_code());
                        taskflow.setNow_state(1);
                        taskflow.setNow_state_name(Contants.yqr);
                        taskflow.setUser_action(Contants.action_qr);
                        jCloudDB.save(taskflow);

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
                btQrjs.setText("已确认");
                btQrjs.setClickable(false);
                btDjsh.setClickable(true);
                btSqyq.setClickable(true);

                //通知列表更新
                OttUtils.push("fz_refresh", "");
            } else {
                ToastShow("操作失败");
            }
            hideLoading();
        }
    }


    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (msg.equals("job_refresh")) {
            btDjsh.setText("已递交");
            btDjsh.setClickable(false);
            new TaskFlowRefresh(getMy()).execute();
        } else if (msg.equals("yyq_refresh")) {
            btSqyq.setText("延期申请中");
            new TaskFlowRefresh(getMy()).execute();
        } else if (msg.equals("give_refresh")) {
            btFqrw.setText("放弃申请中");
            btFqrw.setClickable(false);
            new TaskFlowRefresh(getMy()).execute();
        }
    }

    public void showAlertDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("是否放弃任务");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
                showLoading();
                new fqTask(getMy()).execute();
            }
        });

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();

    }


    /**
     * 改变任务表状态
     */
    private class fqTask extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public fqTask(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            try {
                if (null != users) {
                    if (null != task) {
                        String sql = "update task set now_state = 7 , now_state_name = '放弃申请中'  where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and principal_code like " + StrUtils.QuotedStrLike(users.getUser_code());
                        CloudDB.execSQL(sql);

                        //保存到流程表里
                        Taskflow taskflow = new Taskflow();
                        taskflow.setNickname(users.getNickname());
                        taskflow.setTask_code(task.getTask_code());
                        taskflow.setNow_state(7);
                        taskflow.setNow_state_name(Contants.fqsqz);
                        taskflow.setUser_action(Contants.action_sqfq);
                        jCloudDB.save(taskflow);

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
                btFqrw.setText("放弃申请中");
                btFqrw.setClickable(false);
            } else {
                ToastShow("操作失败");
            }
            hideLoading();
        }
    }

    /**
     * 刷新任务流程
     */
    private class TaskFlowRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;
        List<Taskflow> taskflowNews;

        /**
         * @param context 上下文
         */
        public TaskFlowRefresh(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";


            try {
                String newSql = "select * from  v_taskflow where task_code= " + StrUtils.QuotedStr(task.getTask_code());
                //查找数据
                taskflows = jCloudDB.findAllBySql(Taskflow.class, newSql);
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                if (ListUtils.IsNotNull(taskflowNews)) {
                    taskflows.clear();
                    taskflows.addAll(taskflowNews);
                    commonAdapter.notifyDataSetChanged();
                }
            } else {
                ToastShow("数据获取失败");
            }
            hideLoading();
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
