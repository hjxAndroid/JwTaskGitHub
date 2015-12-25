package com.jeeweel.syl.jwtask.business.main.module.task;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.DegreeItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import api.util.Contants;
import api.util.OttUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SolveGiveUpActivity extends JwActivity {

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
    @Bind(R.id.btn_ty)
    Button btnTy;
    @Bind(R.id.btn_jj)
    Button btnJj;

    Task task;
    int flag = 0;
    @Bind(R.id.li_bt)
    LinearLayout liBt;
    Users users;
    @Bind(R.id.et_bhly)
    EditText etBhly;
    @Bind(R.id.degree_score)
    TextView degreeScore;

    private String bhly;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_give_up);
        ButterKnife.bind(this);
        setTitle("放弃审核");
        users = JwAppAplication.getInstance().getUsers();
        getData();
    }

    //绩效分值
    @OnClick(R.id.li_degree_score)
    void fzClick() {
        List<DegreeItem> mListItems = new ArrayList<DegreeItem>();
        DegreeItem degreeItem = new DegreeItem();
        degreeItem.setDegree("简单");
        degreeItem.setDegree_score(2);
        mListItems.add(degreeItem);

        DegreeItem degreeItem1 = new DegreeItem();
        degreeItem1.setDegree("一般");
        degreeItem1.setDegree_score(4);
        mListItems.add(degreeItem1);

        DegreeItem degreeItem2 = new DegreeItem();
        degreeItem2.setDegree("困难");
        degreeItem2.setDegree_score(6);
        mListItems.add(degreeItem2);

        DegreeItem degreeItem3 = new DegreeItem();
        degreeItem3.setDegree("极难");
        degreeItem3.setDegree_score(8);
        mListItems.add(degreeItem3);
    }

    //同意
    @OnClick(R.id.btn_ty)
    void tyClick() {
        flag = 0;
        bhly = etBhly.getText().toString();
        if (StrUtils.IsNotEmpty(bhly)) {
            new changeTask(getMy()).execute();
        } else {
            ToastShow("请填写理由");
        }
    }

    //拒绝
    @OnClick(R.id.btn_jj)
    void jjClick() {
        flag = 1;
        bhly = etBhly.getText().toString();
        if (StrUtils.IsNotEmpty(bhly)) {
            new changeTask(getMy()).execute();
        } else {
            ToastShow("请填写理由");
        }
    }

    private void getData() {
        task = (Task) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        String flag = getIntent().getStringExtra("flag");
        if (StrUtils.IsNotEmpty(flag)) {
            liBt.setVisibility(View.GONE);
        }
        if (null != task) {
            tvRmw.setText(StrUtils.IsNull(task.getTask_name()));
            tvKssj.setText(StrUtils.IsNull(task.getBegin_time()));
            tvJiesusj.setText(StrUtils.IsNull(task.getOver_time()));
            tvRwyq.setText(StrUtils.IsNull(task.getTask_request()));
            tvFqly.setText(StrUtils.IsNull(task.getGive_up_content()));
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
                if (null != task) {
                    String sql = "";
                    if (flag == 0) {
                        sql = "update task set now_state = 8 , now_state_name = '已放弃',reject_content = " + StrUtils.QuotedStr(bhly) + "  where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and auditor_code like " + StrUtils.QuotedStrLike(task.getAuditor_code());
                        CloudDB.execSQL(sql);
                        //保存到流程表里
                        Taskflow taskflow = new Taskflow();
                        taskflow.setTask_code(task.getTask_code());
                        taskflow.setNickname(users.getNickname());
                        taskflow.setNow_state(8);
                        taskflow.setNow_state_name(Contants.yfq);
                        taskflow.setUser_action(Contants.action_tyfq);
                        jCloudDB.save(taskflow);
                    } else if (flag == 1) {
                        sql = "update task set now_state = 9 , now_state_name = '放弃驳回',reject_content = " + StrUtils.QuotedStr(bhly) + "  where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and auditor_code like " + StrUtils.QuotedStrLike(task.getAuditor_code());
                        CloudDB.execSQL(sql);
                        //保存到流程表里
                        Taskflow taskflow = new Taskflow();
                        taskflow.setUser_code(users.getUser_code());
                        taskflow.setNickname(users.getNickname());
                        taskflow.setTask_code(task.getTask_code());
                        taskflow.setNow_state(9);
                        taskflow.setNow_state_name(Contants.fqbh);
                        taskflow.setUser_action(Contants.action_fqbh);
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
                ToastShow("操作成功");
                OttUtils.push("give_refresh", "");
                finish();
            } else {
                ToastShow("操作失败");
                finish();
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


    private void showDegreeDialog(final int postion, List<DegreeItem> mListItems) {

        dialog = new AlertDialog.Builder(getMy()).create();// 创建一个AlertDialog对象
        View view = SolveGiveUpActivity.this.getLayoutInflater().inflate(R.layout.item_task_dialog,
                null);// 自定义布局
        dialog.setView(view, 0, 0, 0, 0);// 把自定义的布局设置到dialog中，注意，布局设置一定要在show之前。从第二个参数分别填充内容与边框之间左、上、右、下、的像素
        dialog.show();// 一定要先show出来再设置dialog的参数，不然就不会改变dialog的大小了
        int width = SolveGiveUpActivity.this.getWindowManager().getDefaultDisplay().getWidth();// 得到当前显示设备的宽度，单位是像素
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();// 得到这个dialog界面的参数对象
        params.width = width - (width / 6);// 设置dialog的界面宽度
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;// 设置dialog高度为包裹内容
        params.gravity = Gravity.CENTER;// 设置dialog的重心
        dialog.getWindow().setAttributes(params);// 最后把这个参数对象设置进去，即与dialog绑定

        ListView listView = (ListView) view.findViewById(R.id.listview);

        final CommonAdapter commonAdapter = new CommonAdapter<DegreeItem>(getMy(), mListItems, R.layout.item_friend_detail) {
            @Override
            public void convert(ViewHolder helper, DegreeItem item) {
                helper.setText(R.id.tv_org_name, item.getDegree());
            }
        };
        listView.setAdapter(commonAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                DegreeItem item = (DegreeItem) commonAdapter.getItem(position);
                //紧急程度
                degreeScore.setText(item.getDegree());
                dialog.cancel();
            }
        });
    }

}
