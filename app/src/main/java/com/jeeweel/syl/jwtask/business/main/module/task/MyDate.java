package com.jeeweel.syl.jwtask.business.main.module.task;

/**
 * Created by Ragn on 2016/1/4.
 */

import java.util.Calendar;
import java.util.Date;


import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.jeeweel.syl.lib.api.core.jwpublic.date.JwDateUtils;

/**
 * input method will need to run in a limited "generate key events" mode.  *   *   * @author admin  *
 */
public class MyDate extends EditText implements OnDateSetListener, OnTimeSetListener {
    public MyDatePickDialog Dlg;
    public Dialog timeDialog;
    private Context context;
    private String timeStr;
    String dateStr = null;

    public MyDate(Context context) {
        super(context);
        this.context = context;
        setListener();
    }

    public MyDate(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setListener();
    }

    private void setListener() {
        this.setFocusable(false);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                initCalendar(dateStr);
                Dlg.show();
            }
        });
    }

    public String dateSelect(String dateStr) {
        // 此方法为弹出时间选择对话框，可在外部调用
        initCalendar(dateStr);
        Dlg.show();
        return timeStr;
    }

    private void initCalendar(String dateStr) {
        dateStr = this.getText().toString().trim();
        Date date = null;
        try {
            date = JwDateUtils.ConverToDate2(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (date == null) {// 如果输入框内初始为空，则将当前时间初始为日期时间
            date = new Date();
        }
        Calendar d = Calendar.getInstance();
        d.setTimeInMillis(date.getTime());        // 初始化时间选择器
        int year = d.get(Calendar.YEAR);
        int month = d.get(Calendar.MONTH);
        int day = d.get(Calendar.DAY_OF_MONTH);
        Dlg = new MyDatePickDialog(context, this, year, month, day);
        Dlg.setCanceledOnTouchOutside(false);
        timeDialog = new MyTimePickerDialog(context, this,
                d.get(Calendar.HOUR_OF_DAY),
                d.get(Calendar.MINUTE), true);
        timeDialog.setTitle("请选择时间");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        // TODO Auto-generated method stub
        timeStr = year + "-" + (monthOfYear < 9 ? "0" + (monthOfYear + 1) : monthOfYear + 1) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + " ";
        timeDialog.show();
        Log.e("aa", "onDateSet");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub
        timeStr += (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":"
                + (minute < 10 ? "0" + minute : minute) + ":00";
        MyDate.this.setText(timeStr);
        Log.e("aa", "onTimeSet");
    }

    // 重写DatePickerDialog以避免两次执行onDateSet方法
    public static class MyDatePickDialog extends DatePickerDialog {
        public MyDatePickDialog(Context context, OnDateSetListener callBack,
                                int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
            // TODO Auto-generated constructor stub
        }

        public MyDatePickDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, theme, callBack, year, monthOfYear, dayOfMonth);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onStop() {
            // TODO Auto-generated method stub
        }
    }

    public static class MyTimePickerDialog extends TimePickerDialog {
        public MyTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
            super(context, callBack, hourOfDay, minute, is24HourView);
            // TODO Auto-generated constructor stub
        }

        public MyTimePickerDialog(Context context, int theme,
                                  OnTimeSetListener callBack,
                                  int hourOfDay, int minute,
                                  boolean is24HourView) {
            super(context, theme, callBack, hourOfDay, minute, is24HourView);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onStop() {
            // TODO Auto-generated method stub
        }
    }
}
