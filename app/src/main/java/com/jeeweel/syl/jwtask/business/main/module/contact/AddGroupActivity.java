package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddGroupActivity extends JwActivity {

    @Bind(R.id.et_group_name)
    EditText etGroupName;
    @Bind(R.id.add)
    FrameLayout add;
    @Bind(R.id.listview)
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        ButterKnife.bind(this);
    }

}
