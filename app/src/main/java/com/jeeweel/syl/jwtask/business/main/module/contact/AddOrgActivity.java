package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import api.util.Contants;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOrgActivity extends JwActivity {

    @Bind(R.id.et_org_name)
    EditText etOrgName;
    @Bind(R.id.bt_ok)
    Button btOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_org);
        ButterKnife.bind(this);
        setTitle("创建组织");
    }

    @OnClick(R.id.bt_ok)
    void nextClick() {
        String orgname = etOrgName.getText().toString();
        if (StrUtils.IsNotEmpty(orgname)) {
            JwStartActivity(AddDeptActivity.class, orgname);
        } else {
            ToastShow("请输入组织名称");
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
