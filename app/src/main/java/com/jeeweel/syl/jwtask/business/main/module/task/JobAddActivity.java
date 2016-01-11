package com.jeeweel.syl.jwtask.business.main.module.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.gson.Gson;
import com.jeeweel.syl.jcloudlib.db.api.CloudFile;
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
import com.jeeweel.syl.jwtask.business.main.module.photo.GetPicActivity;
import com.jeeweel.syl.jwtask.business.main.module.photo.PhotoActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.config.publicjsonclass.ResMsgItem;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.o.OUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;

import com.jeeweel.syl.lib.api.core.toast.JwToast;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import api.date.DatePickerDialog;
import api.photoview.Bimp;
import api.photoview.FileUtils;
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
    String kssj = "";
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


    private ScrollView li_fb;
    GridView noScrollgridview;
    GridAdapter adapter;

    String fzr;

    String chooseFlag = "";

    List<Userdept> userdepts = new ArrayList<>();;
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
        initView();
    }


    private void initView(){
        li_fb = (ScrollView) findViewById(R.id.li_fb);

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        adapter = new GridAdapter(JobAddActivity.this);
        adapter.update1();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.bmp.size()) {
                    new PopupWindows(context, li_fb);
                } else {
                    Intent intent = new Intent(context,
                            PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
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


        if (StrUtils.IsNotEmpty(fzr) && StrUtils.IsNotEmpty(fzrCode)) {
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
                                        //后面时间比较大
                                        if(Utils.compare_date(dateStr,kssj)){
                                            etEndTime.setText(dateStr);
                                        }else {
                                            ToastShow("结束时间不能比开始时间早，请您重新选择时间");
                                        }
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
                                //后面时间比较大
                                if(Utils.compare_date(dateStr,kssj)){
                                    etEndTime.setText(dateStr);
                                }else {
                                    ToastShow("结束时间不能比开始时间早，请您重新选择时间");
                                }
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
        kssj = etStartTime.getText().toString();
        if(StrUtils.IsNotEmpty(kssj)){
            timeData = etEndTime.getText().toString();
            showDialog(0);
        }else{
            ToastShow("请先选择开始时间");
        }
    }


    //选择负责人
    @OnClick(R.id.li_fzr)
    void fzrClick() {
        if(ListUtils.IsNotNull(userdepts)){
            String json = new Gson().toJson(userdepts);
            Intent intent = new Intent(getMy(),SelectedActivity.class);
            intent.putExtra(StaticStrUtils.baseItem,Contants.fzr);
            intent.putExtra("data",json);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getMy(),PublicyContactHomeActivity.class);
            intent.putExtra(StaticStrUtils.baseItem,Contants.fzr);
            startActivity(intent);
        }
    }

    //选择审核人
    @OnClick(R.id.li_shr)
    void shrClick() {
        if(ListUtils.IsNotNull(userdepts)){
            String json = new Gson().toJson(userdepts);
            Intent intent = new Intent(getMy(),SelectedActivity.class);
            intent.putExtra(StaticStrUtils.baseItem,Contants.shr);
            intent.putExtra("data",json);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getMy(),PublicyContactHomeActivity.class);
            intent.putExtra(StaticStrUtils.baseItem,Contants.shr);
            startActivity(intent);
        }
    }


    //选择观察者
    @OnClick(R.id.li_gcz)
    void gczClick() {
        if(ListUtils.IsNotNull(userdepts)){
            String json = new Gson().toJson(userdepts);
            Intent intent = new Intent(getMy(),SelectedActivity.class);
            intent.putExtra(StaticStrUtils.baseItem,Contants.gcz);
            intent.putExtra("data",json);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getMy(),PublicyContactHomeActivity.class);
            intent.putExtra(StaticStrUtils.baseItem,Contants.gcz);
            startActivity(intent);
        }
    }


    //选择参与者
    @OnClick(R.id.li_cyz)
    void cyzClick() {
        if(ListUtils.IsNotNull(userdepts)){
            String json = new Gson().toJson(userdepts);
            Intent intent = new Intent(getMy(),SelectedActivity.class);
            intent.putExtra(StaticStrUtils.baseItem,Contants.cyz);
            intent.putExtra("data",json);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getMy(),PublicyContactHomeActivity.class);
            intent.putExtra(StaticStrUtils.baseItem,Contants.cyz);
            startActivity(intent);
        }
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
        String msg = activityMsgEvent.getMsg();
        String json = activityMsgEvent.getParam1();
        if (StrUtils.IsNotEmpty(json)) {
            userdepts = JwJSONUtils.getParseArray(json, Userdept.class);
        } else {
            json = activityMsgEvent.getParam();
            List<Friend> friends = JwJSONUtils.getParseArray(json, Friend.class);
            for (Friend friend : friends) {
                Userdept userdept = new Userdept();
                userdept.setUser_code(friend.getFriend_code());
                userdept.setNickname(friend.getFriend_nickname());
                userdepts.add(userdept);
            }
        }


        if (StrUtils.IsNotEmpty(msg) && msg.equals(Contants.fzr)) {
            if (StrUtils.IsNotEmpty(json)&&!json.equals("[]")) {
                if (ListUtils.IsNotNull(userdepts)) {
                    fzrCode = "";
                    fzr = "";
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
            }else{
                etFzr.setText("");
                fzrCode = "";
                fzr = "";
                userdepts.clear();
            }

        } else if (StrUtils.IsNotEmpty(msg) && msg.equals(Contants.shr)) {
            if (StrUtils.IsNotEmpty(json)&&!json.equals("[]")) {
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
            }else{
                etShr.setText("");
                shrCode = "";
                userdepts.clear();
            }
        } else if (StrUtils.IsNotEmpty(msg) && msg.equals(Contants.gcz)) {
            if (StrUtils.IsNotEmpty(json)&&!json.equals("[]")) {
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
            }else{
                etGcz.setText("");
                gczCode = "";
                userdepts.clear();
            }
        } else if (StrUtils.IsNotEmpty(msg) && msg.equals(Contants.cyz)) {
            if (StrUtils.IsNotEmpty(json)&&!json.equals("[]")) {
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
            }else{
                tvCyz.setText("");
                cyzCode = "";
                userdepts.clear();
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
            try {
               if (StrUtils.IsNotEmpty(fzr) && StrUtils.IsNotEmpty(fzrCode)) {
                   String[] fzrNames = fzr.split(",");
                   String[] fzrs = fzrCode.split(",");
                   String task_name = task.getTask_name();
                   //批量发布任务
                   for(int i = 0; i<fzrs.length; i++){
                       task.setPrincipal_code(fzrs[i]);
                       if (null != task) {

                               String unid = Utils.getUUid();
                               if (null != users) {
                                 /*  //设置任务名为任务名-负责人-发布时间
                                   String taskName = task_name+"_"+fzrNames[i]+"_"+task.getOver_time();
                                   task.setTask_name(taskName);*/
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

                               //保存图片表
                               for (String sFile : Bimp.drr) {
                                   // File file = new File(sFile);
                                   CloudFile.upload(sFile, unid + "1");
                               }


                       }
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
            pushData();
            ToastShow("任务发布成功");
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

    //相册popwin
    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            super(mContext);

            View view = View
                    .inflate(mContext, R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(GridLayout.LayoutParams.FILL_PARENT);
            setHeight(GridLayout.LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    photo();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context,
                            GetPicActivity.class);
                    startActivity(intent);
                    dismiss();
                }
            });
            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void photo() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStorageDirectory() + "/myimage/");
            if (!dir.exists()) dir.mkdirs();

            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(dir, String.valueOf(System.currentTimeMillis())
                    + ".jpg");
            path = file.getPath();
            Uri imageUri = Uri.fromFile(file);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        } else {
            Toast.makeText(context, "没有储存卡", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.drr.size() < 9 && resultCode == -1) {
                    Bimp.drr.add(path);
                }
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater; // 视图容器
        private int selectedPosition = -1;// 选中的位置
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update1() {
            loading1();
        }

        public int getCount() {
            return (Bimp.bmp.size() + 1);
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        /**
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            //final int coord = position;
            ViewHolder holder = null;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.image.setVisibility(View.VISIBLE);

            if (position == Bimp.bmp.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));

            } else {
                holder.image.setImageBitmap(Bimp.bmp.get(position));
            }

            if (position == 9) {
                holder.image.setVisibility(View.GONE);
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading1() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.drr.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            try {
                                String path = Bimp.drr.get(Bimp.max);
                                System.out.println(path);
                                Bitmap bm = Bimp.revitionImageSize(path);
                                Bimp.bmp.add(bm);
                                String newStr = path.substring(
                                        path.lastIndexOf("/") + 1,
                                        path.lastIndexOf("."));
                                FileUtils.saveBitmap(bm, "" + newStr);
                                Bimp.max += 1;
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } catch (IOException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onRestart() {
        adapter.update1();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Bimp.bmp.clear();
        Bimp.drr.clear();
        Bimp.max = 0;
        super.onDestroy();
    }
}
