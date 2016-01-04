package com.jeeweel.syl.jwtask.business.main.module.task;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Created by Ragn on 2016/1/4.
 */
public class MyDatePickDialog extends DatePickerDialog {

    public MyDatePickDialog(Context context, DatePickerDialog.OnDateSetListener callBack,
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
