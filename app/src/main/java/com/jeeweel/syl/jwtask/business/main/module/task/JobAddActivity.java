package com.jeeweel.syl.jwtask.business.main.module.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;


import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.DegreeItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.publicjsonclass.ResMsgItem;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;

import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import api.date.DatePickerDialog;
import api.photoview.Bimp;
import api.util.Contants;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JobAddActivity extends JwActivity {


    @Bind(R.id.et_task_name)
    EditText etTaskName;
    @Bind(R.id.et_start_time)
    TextView etStartTime;
    @Bind(R.id.et_end_time)
    TextView etEndTime;
    @Bind(R.id.et_fzr)
    TextView etFzr;
    @Bind(R.id.li_fzr)
    LinearLayout liFzr;
    @Bind(R.id.et_shr)
    TextView etShr;
    @Bind(R.id.tv_gcz)
    TextView tvGcz;
    @Bind(R.id.et_gcz)
    TextView etGcz;
    @Bind(R.id.tv_cyz)
    TextView tvCyz;
    @Bind(R.id.et_rwyq)
    EditText etRwyq;
    @Bind(R.id.et_yxj)
    TextView etYxj;
    @Bind(R.id.et_khbz)
    TextView etKhbz;
    private AlertDialog dialog;

    private Activity context;

    String fzrCode = "";

    String shrCode = "";

    String gczCode = "";

    String cyzCode = "";

    Task task;

    Users users;

    String orgcode;

    String orgname;

    int timeFlag = 0;

    DegreeItem item;

    String timeData = "";

    String dateStr;
    String[] data;
    String[] dateTime;
    Dialog timeDialog = null;
    StringBuilder str;
    int year;
    int month;
    int day;
    int hours;
    int minutes;
    private String dateAndTime;
//    MyDate myDate = new MyDate(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_add);
        ButterKnife.bind(this);
        setTitle("发起任务");
        context = this;
        users = JwAppAplication.getInstance().getUsers();
        orgcode = (String) SharedPreferencesUtils.get(getMy(), Contants.org_code, "");
        orgname = (String) SharedPreferencesUtils.get(getMy(), Contants.org_name, "");
        initRight();
    }


    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Task task = save();
                if (null != task) {
                    showLoading();
                    new FinishRefresh(getMy()).execute();
                } else {
                    ToastShow("请完善必填信息");
                }
            }
        });
        addMenuView(menuTextView);
    }

    private Task save() {

        task = new Task();

        String task_name = etTaskName.getText().toString();
        if (StrUtils.IsNotEmpty(task_name)) {
            task.setTask_name(task_name);
        } else {
            return null;
        }

        String start_time = etStartTime.getText().toString();
        if (StrUtils.IsNotEmpty(start_time)) {
            task.setBegin_time(start_time);
        }


        String end_time = etEndTime.getText().toString();
        if (StrUtils.IsNotEmpty(end_time)) {
            task.setOver_time(end_time);
        }


        String fzr = etFzr.getText().toString();
        if (StrUtils.IsNotEmpty(fzr) && StrUtils.IsNotEmpty(fzrCode)) {
            task.setPrincipal_code(fzrCode);
        } else {
            return null;
        }


        String shr = etShr.getText().toString();
        if (StrUtils.IsNotEmpty(shr) && StrUtils.IsNotEmpty(shrCode)) {
            task.setAuditor_code(shrCode);
        } else {
            return null;
        }


        String gcz = etGcz.getText().toString();
        if (StrUtils.IsNotEmpty(gcz) && StrUtils.IsNotEmpty(gczCode)) {
            task.setObserver_code(gczCode);
        }


        String cyz = tvCyz.getText().toString();
        if (StrUtils.IsNotEmpty(cyz) && StrUtils.IsNotEmpty(cyzCode)) {
            task.setParticipant_code(cyzCode);
        }


        String rwyq = etRwyq.getText().toString();
        if (StrUtils.IsNotEmpty(rwyq)) {
            task.setTask_request(rwyq);
        } else {
            return null;
        }


        String yxj = etYxj.getText().toString();
        if (StrUtils.IsNotEmpty(yxj)) {
            task.setPriority(yxj);
        }


        String khbz = etKhbz.getText().toString();
        if (StrUtils.IsNotEmpty(khbz)) {
            task.setDegree(khbz);
            if (null != item) {
                task.setDegree_score(item.getDegree_score());
            }
        }

        return task;
    }


//    /**
//     * 创建日期及时间选择对话框
//     */
//    @Override
//    protected Dialog onCreateDialog(int id) {
//        Dialog dialog = null;
//
//        if (StrUtils.IsNotEmpty(timeData)) {
//            String[] data = timeData.split("-");
//            int year = Integer.parseInt(data[0]);
//            int mouth = Integer.parseInt(data[1]) - 1;
//            int day = Integer.parseInt(data[2]);
//            dialog = new android.app.DatePickerDialog(
//                    this,
//                    new android.app.DatePickerDialog.OnDateSetListener() {
//                        public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
//                            if (timeFlag == 0) {
//                                etStartTime.setText("" + year + "-" + (month + 1) + "-" + dayOfMonth);
//                            } else {
//                                etEndTime.setText("" + year + "-" + (month + 1) + "-" + dayOfMonth);
//                            }
//                        }
//                    },
//                    year, // 传入年份
//                    mouth, // 传入月份
//                    day // 传入天数
//            );
//        } else {
//            Calendar c = Calendar.getInstance();
//            dialog = new android.app.DatePickerDialog(
//                    this,
//                    new android.app.DatePickerDialog.OnDateSetListener() {
//                        public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
//                            if (timeFlag == 0) {
//                                etStartTime.setText("" + year + "-" + (month + 1) + "-" + dayOfMonth);
//                            } else {
//                                etEndTime.setText("" + year + "-" + (month + 1) + "-" + dayOfMonth);
//                            }
//                        }
//                    },
//                    c.get(Calendar.YEAR), // 传入年份
//                    c.get(Calendar.MONTH), // 传入月份
//                    c.get(Calendar.DAY_OF_MONTH) // 传入天数
//            );
//        }
//        return dialog;
//    }

    /**
     * 创建日期及时间选择对话框
     */
    @Override
    protected Dialog onCreateDialog(int id) {
//    private void clickChangeDateTime() {
        Dialog dateDialog = null;
        if (StrUtils.IsNotEmpty(timeData)) {
            data = timeData.split("-");
            year = Integer.parseInt(data[0]);
            month = Integer.parseInt(data[1]) - 1;
            day = Integer.parseInt(data[2]);
            dateAndTime = data[3];
            dateTime = dateAndTime.split(":");
            hours = Integer.parseInt(dateTime[0]);
            minutes = Integer.parseInt(data[1]);
            dateDialog = new MyDatePickDialog(JobAddActivity.this, new android.app.DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    str = new StringBuilder("");
                    str.append(year + "-" + (month + 1) + "-"
                            + day + " ");
                    timeDialog = new MyTimePickerDialog(JobAddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                            str.append(hours + ":"
                                    + minutes);
                            dateStr = str.toString();
                            if (timeFlag == 0) {
                                etStartTime.setText(dateStr);
                            } else {
                                etEndTime.setText(dateStr);
                            }
                        }
                    }, hours, minutes
// true表示采用24小时制
                            , true);
                    timeDialog.setTitle("请选择时间");
                    timeDialog.show();
                }
            }, year, month, day);
            dateDialog.setTitle("请选择日期");
            dateDialog.show();
        } else {
            Calendar c = Calendar.getInstance();
            dateDialog = new MyDatePickDialog(JobAddActivity.this, new android.app.DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    str = new StringBuilder("");
                    str.append(year + "-" + (month + 1) + "-"
                            + dayOfMonth + " ");
                    Calendar time = Calendar.getInstance();
                    timeDialog = new MyTimePickerDialog(JobAddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            str.append(hourOfDay + ":"
                                    + minute);
                            dateStr = str.toString();
                            if (timeFlag == 0) {
                                etStartTime.setText(dateStr);
                            } else {
                                etEndTime.setText(dateStr);
                            }
                        }
                    }, time.get(Calendar.HOUR_OF_DAY), time
                            .get(Calendar.MINUTE)
// true表示采用24小时制
                            , true);
                    timeDialog.setTitle("请选择时间");
                    timeDialog.show();
                }
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                    .get(Calendar.DAY_OF_MONTH));
            dateDialog.setTitle("请选择日期");
            dateDialog.show();
        }
        return dateDialog;
    }


    //开始时间
    @OnClick(R.id.li_start_time)
    void starttimeClick() {
        timeFlag = 0;
        timeData = etStartTime.getText().toString();
        showDialog(0);
//        dateStr = myDate.dateSelect(timeData);
//        etStartTime.setText(dateStr);

    }


    //接受时间
    @OnClick(R.id.li_end_time)
    void endtimeClick() {
        timeFlag = 1;
        timeData = etEndTime.getText().toString();
//        dateStr = myDate.dateSelect(timeData);
//        etEndTime.setText(timeData);
        showDialog(0);
    }


    //选择负责人
    @OnClick(R.id.li_fzr)
    void fzrClick() {
        JwStartActivity(PublicyContactHomeActivity.class, Contants.fzr);
    }

    //选择审核人
    @OnClick(R.id.li_shr)
    void shrClick() {
        JwStartActivity(PublicyContactHomeActivity.class, Contants.shr);
    }


    //选择观察者
    @OnClick(R.id.li_gcz)
    void gczClick() {
        JwStartActivity(PublicyContactHomeActivity.class, Contants.gcz);
    }


    //选择参与者
    @OnClick(R.id.li_cyz)
    void cyzClick() {
        JwStartActivity(PublicyContactHomeActivity.class, Contants.cyz);
    }


    @OnClick(R.id.li_yxj)
    void yxjClick() {
        List<String> mListItems = new ArrayList<String>();
        mListItems.add("急");
        mListItems.add("优先");
        mListItems.add("一般");
        showDialog(0, mListItems);
    }

    @OnClick(R.id.li_khbz)
    void khbzClick() {
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
        showDegreeDialog(1, mListItems);
    }


    private void showDialog(final int postion, List<String> mListItems) {

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
                //紧急程度
                if (postion == 0) {
                    etYxj.setText(item);
                } else if (postion == 1) {
                    etKhbz.setText(item);
                }
                dialog.cancel();
            }
        });
    }

    private void showDegreeDialog(final int postion, List<DegreeItem> mListItems) {

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
                item = (DegreeItem) commonAdapter.getItem(position);
                //紧急程度
                etKhbz.setText(item.getDegree());
                dialog.cancel();
            }
        });
    }

    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        List<Userdept> userdepts;
        String msg = activityMsgEvent.getMsg();
        String json = activityMsgEvent.getParam1();
        if (StrUtils.IsNotEmpty(json)) {
            userdepts = JwJSONUtils.getParseArray(json, Userdept.class);
        } else {
            json = activityMsgEvent.getParam();
            List<Friend> friends = JwJSONUtils.getParseArray(json, Friend.class);
            userdepts = new ArrayList<>();
            for (Friend friend : friends) {
                Userdept userdept = new Userdept();
                userdept.setUser_code(friend.getFriend_code());
                userdept.setNickname(friend.getFriend_nickname());
                userdepts.add(userdept);
            }
        }


        if (StrUtils.IsNotEmpty(msg) && msg.equals(Contants.fzr)) {
            if (StrUtils.IsNotEmpty(json)) {
                if (ListUtils.IsNotNull(userdepts)) {
                    fzrCode = "";
                    String fzr = "";
                    for (Userdept userdept : userdepts) {
                        fzr += userdept.getNickname() + ",";
                        fzrCode += userdept.getUser_code() + ",";
                    }
                    if (StrUtils.IsNotEmpty(fzr)) {
                        fzr = fzr.substring(0, fzr.length() - 1);
                    }
                    if (StrUtils.IsNotEmpty(fzrCode)) {
                        fzrCode = fzrCode.substring(0, fzrCode.length() - 1);
                    }
                    etFzr.setText(fzr);
                }
            }

        } else if (StrUtils.IsNotEmpty(msg) && msg.equals(Contants.shr)) {
            if (StrUtils.IsNotEmpty(json)) {
                if (ListUtils.IsNotNull(userdepts)) {
                    String fzr = "";
                    shrCode = "";
                    for (Userdept userdept : userdepts) {
                        fzr += userdept.getNickname() + ",";
                        shrCode += userdept.getUser_code() + ",";
                    }
                    if (StrUtils.IsNotEmpty(fzr)) {
                        fzr = fzr.substring(0, fzr.length() - 1);
                    }
                    if (StrUtils.IsNotEmpty(shrCode)) {
                        shrCode = shrCode.substring(0, shrCode.length() - 1);
                    }
                    etShr.setText(fzr);
                }
            }
        } else if (StrUtils.IsNotEmpty(msg) && msg.equals(Contants.gcz)) {
            if (StrUtils.IsNotEmpty(json)) {
                if (ListUtils.IsNotNull(userdepts)) {
                    String fzr = "";
                    gczCode = "";
                    for (Userdept userdept : userdepts) {
                        fzr += userdept.getNickname() + ",";
                        gczCode += userdept.getUser_code() + ",";
                    }
                    if (StrUtils.IsNotEmpty(fzr)) {
                        fzr = fzr.substring(0, fzr.length() - 1);
                    }
                    if (StrUtils.IsNotEmpty(gczCode)) {
                        gczCode = gczCode.substring(0, gczCode.length() - 1);
                    }
                    etGcz.setText(fzr);
                }
            }
        } else if (StrUtils.IsNotEmpty(msg) && msg.equals(Contants.cyz)) {
            if (StrUtils.IsNotEmpty(json)) {
                if (ListUtils.IsNotNull(userdepts)) {
                    String fzr = "";
                    cyzCode = "";
                    for (Userdept userdept : userdepts) {
                        fzr += userdept.getNickname() + ",";
                        cyzCode += userdept.getUser_code() + ",";
                    }
                    if (StrUtils.IsNotEmpty(fzr)) {
                        fzr = fzr.substring(0, fzr.length() - 1);
                    }
                    if (StrUtils.IsNotEmpty(cyzCode)) {
                        cyzCode = cyzCode.substring(0, cyzCode.length() - 1);
                    }
                    tvCyz.setText(fzr);
                }
            }
        }
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

            if (null != task) {
                try {
                    String unid = Utils.getUUid();
                    if (null != users) {
                        task.setTask_code(unid);
                        task.setPromulgator_code(users.getUser_code());
                        task.setPromulgator_name(users.getUsername());
                        task.setNickname(users.getNickname());
                        //设置当前状态(已发布未确认)
                        task.setNow_state(0);
                        task.setNow_state_name(Contants.wqr);
                    }

                    if (StrUtils.IsNotEmpty(orgcode)) {
                        task.setOrg_code(orgcode);
                        task.setOrg_name(orgname);
                    }

                    jCloudDB.save(task);


                    //保存到流程表里
                    Taskflow taskflow = new Taskflow();
                    taskflow.setUser_code(users.getUser_code());
                    taskflow.setNickname(users.getNickname());
                    taskflow.setTask_code(unid);
                    taskflow.setNow_state(0);
                    taskflow.setNow_state_name(Contants.wqr);
                    taskflow.setUser_action(Contants.action_fb);
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
                pushData();
                ToastShow("任务发布成功");
            } else {
                ToastShow("保存失败");
            }
            hideLoading();

        }

    }

    @Subscribe
    public void dateTimeSelect(ActivityMsgEvent activityMsgEvent) {
        if (activityMsgEvent.getMsg().equals("dateTimePick")) {
            String birthday = activityMsgEvent.getJson();
            if (StrUtils.IsNotEmpty(birthday)) {
                if (timeFlag == 0) {
                    etStartTime.setText(birthday);
                } else {
                    etEndTime.setText(birthday);
                }
            }
        }
    }


    public void pushData() {
        if (task != null) {
            String title = task.getTask_name();
            String content = task.getTask_request();

            String all_code = "";
            String fzr = task.getPrincipal_code();
            String shr = task.getAuditor_code();
            String cyz = task.getParticipant_code();
            String gcz = task.getObserver_code();

            if (StrUtils.IsNotEmpty(fzr)) {
                all_code += fzr;
            }

            if (StrUtils.IsNotEmpty(shr)) {
                all_code += "," + shr;
            }

            if (StrUtils.IsNotEmpty(cyz)) {
                all_code += "," + cyz;
            }

            if (StrUtils.IsNotEmpty(gcz)) {
                all_code += "," + gcz;
            }

            if (StrUtils.IsNotEmpty(title) && StrUtils.IsNotEmpty(content)) {
                try {
                    title = URLEncoder.encode(URLEncoder.encode(title, "utf-8"), "utf-8");
                    content = URLEncoder.encode(URLEncoder.encode(content, "utf-8"), "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String param = "?all_code=" + all_code + "&title=" + title + "&content=" + content;
            String apiStr = Utils.getPublicUrl() + param;
            JwHttpGet(apiStr, true);
        }
    }

    @Override
    public void HttpSuccess(ResMsgItem resMsgItem) {
        finish();
    }

    @Override
    public void HttpFail(String strMsg) {
        super.HttpFail(strMsg);
        finish();
    }

    @Override
    public void HttpFinish() {
        super.HttpFinish();
        finish();
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
