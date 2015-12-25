package com.jeeweel.syl.jwtask.business.main.module.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Alreadyread;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Submit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.imagedemo.image.ImagePagerActivity;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import api.util.Contants;
import api.util.OttUtils;
import api.util.Utils;
import api.view.GridNoScrollView;
import api.view.ListNoScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FinishShActivity extends JwActivity {

    @Bind(R.id.et_task_name)
    TextView etTaskName;
    @Bind(R.id.et_confirm_time)
    TextView etConfirmTime;
    @Bind(R.id.tv_zwpj)
    TextView tvZwpj;
    @Bind(R.id.tv_wcqk)
    TextView tvWcqk;
    @Bind(R.id.tv_yjfk)
    TextView tvYjfk;
    @Bind(R.id.noScrollgridview)
    GridNoScrollView noScrollgridview;
    @Bind(R.id.et_shpj)
    TextView etShpj;
    @Bind(R.id.li_shpj)
    LinearLayout liShpj;
    @Bind(R.id.li_wcqk)
    EditText liWcqk;

    Task task;
    Submit submit;
    @Bind(R.id.listview)
    ListNoScrollView listview;
    @Bind(R.id.tv_rwnd)
    TextView tvRwnd;

    private AlertDialog dialog;
    Activity context;

    Users users;

    String wcqk;
    String shpj;
    CommonAdapter commonAdapter;
    int score;
    String orgCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_sh);
        ButterKnife.bind(this);
        setTitle("任务完成审核");
        context = this;
        orgCode = (String) SharedPreferencesUtils.get(getMy(), Contants.org_code, "");
        users = JwAppAplication.getInstance().getUsers();
        initRight();
        getData();
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                wcqk = liWcqk.getText().toString();
                shpj = etShpj.getText().toString();

                if (StrUtils.IsNotEmpty(wcqk) && StrUtils.IsNotEmpty(shpj)) {
                    showLoading();
                    double sco = 0.01;
                    if (shpj.equals("优秀")) {
                        sco = task.getDegree_score() * 1;
                    } else if (shpj.equals("良好")) {
                        sco = task.getDegree_score() * (0.5);
                    } else if (shpj.equals("未完成")) {
                        sco = task.getDegree_score() * (-0.5);
                    }
                    score = (int) sco;

                    new saveRefresh(getMy()).execute();
                } else {
                    ToastShow("请完成审核内容");
                }
            }
        });
        addMenuView(menuTextView);
    }

    private void getData() {
        showLoading();
        task = (Task) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        new FinishRefresh(getMy()).execute();
    }

    @OnClick(R.id.li_shpj)
    void shpjClick() {
        List<String> mListItems = new ArrayList<>();
        String[] data = getResources().getStringArray(R.array.shpj_array);

        for (int i = 0; i < data.length; i++) {
            mListItems.add(data[i]);
        }
        showDialog(mListItems);
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        List<Submit> list;
        List<Picture> pictureList;
        List<Taskflow> taskflows;

        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            JCloudDB jCloudDB = new JCloudDB();
            try {
                if (null != task) {
                    list = jCloudDB.findAllByWhere(Submit.class,
                            "task_code=" + StrUtils.QuotedStr(task.getTask_code()));

                    pictureList = jCloudDB.findAllByWhere(Picture.class,
                            "pic_code=" + StrUtils.QuotedStr(task.getTask_code()));

                    taskflows = jCloudDB.findAllByWhere(Taskflow.class,
                            "task_code=" + StrUtils.QuotedStr(task.getTask_code()));

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
                OttUtils.push("news_refresh", "");
                if (ListUtils.IsNotNull(list)) {
                    submit = list.get(0);
                    etTaskName.setText(StrUtils.IsNull(submit.getTask_name()));
                    etConfirmTime.setText(StrUtils.IsNull(task.getConfirm_time()));
                    tvRwnd.setText(StrUtils.IsNull(task.getDegree()));
                    tvWcqk.setText(StrUtils.IsNull(submit.getPerformance()));
                    tvYjfk.setText(StrUtils.IsNull(submit.getFeedback()));
                    tvZwpj.setText(StrUtils.IsNull(submit.getEvaluate()));
                }

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

                if (ListUtils.IsNotNull(taskflows)) {
                    commonAdapter = new CommonAdapter<Taskflow>(getMy(), taskflows, R.layout.item_task_detail) {
                        @Override
                        public void convert(ViewHolder helper, Taskflow item) {
                            helper.setText(R.id.tv_nickname, item.getNickname());
                            helper.setText(R.id.tv_action, item.getUser_action());
                            helper.setText(R.id.tv_time, item.getCreate_time());
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
                                    JwStartActivity(MyJobDetailActivity.class, taskflow.getTask_code());
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
                                default:
                                    break;
                            }
                        }
                    });
                }
            } else {

            }
            hideLoading();
        }
    }

    private void showDialog(List<String> mListItems) {

        dialog = new AlertDialog.Builder(getMy()).create();// 创建一个AlertDialog对象
        View view = context.getLayoutInflater().inflate(R.layout.item_task_dialog,
                null);// 自定义布局
        dialog.setView(view, 0, 0, 0, 0);// 把自定义的布局设置到dialog中，注意，布局设置一定要在show之前。从第二个参数分别填充内容与边框之间左、上、右、下、的像素
        dialog.show();// 一定要先show出来再设置dialog的参数，不然就不会改变dialog的大小了
        int width = context.getWindowManager().getDefaultDisplay().getWidth();// 得到当前显示设备的宽度，单位是像素
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();// 得到这个dialog界面的参数对象
        params.width = width - (width / 6);// 设置dialog的界面宽度
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;// 设置dialog高度为包裹内容
        params.gravity = Gravity.CENTER;// 设置dialog的重心
        dialog.getWindow().setAttributes(params);// 最后把这个参数对象设置进去，即与dialog绑定

        ListView listView = (ListView) view.findViewById(R.id.listview);

        final CommonAdapter commonAdapter = new CommonAdapter<String>(getMy(), mListItems, R.layout.item_friend_detail) {
            @Override
            public void convert(ViewHolder helper, String item) {
                helper.setText(R.id.tv_org_name, item);
            }
        };
        listView.setAdapter(commonAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String item = (String) commonAdapter.getItem(position);
                etShpj.setText(item);
                dialog.cancel();
            }
        });
    }


    /**
     * 保存到数据库
     */
    private class saveRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public saveRefresh(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            if (null != submit) {
                try {
                    String sqlsubmit = "update submit set audit_content = '" + wcqk + "' , audit_evaluate = '" + shpj + "',score = " + score + " where task_code = " + StrUtils.QuotedStr(task.getTask_code());
                    CloudDB.execSQL(sqlsubmit);

                    if (null != users) {
                        String sql = "update task set now_state = 3 , now_state_name = '已审核' where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and principal_code like " + StrUtils.QuotedStrLike(users.getUser_code());
                        CloudDB.execSQL(sql);
                    }

                    //保存到流程表里
                    Taskflow taskflow = new Taskflow();
                    taskflow.setUser_code(users.getUser_code());
                    taskflow.setNickname(users.getNickname());
                    taskflow.setTask_code(task.getTask_code());
                    taskflow.setNow_state(3);
                    taskflow.setNow_state_name(Contants.ysh);
                    taskflow.setUser_action(Contants.action_sh);
                    jCloudDB.save(taskflow);

                } catch (CloudServiceException e) {
                    result = "0";
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                ToastShow("发布成功");
                OttUtils.push("sh_refresh", "");
                finish();
            } else {
                ToastShow("保存失败");
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
