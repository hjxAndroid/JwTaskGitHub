package com.jeeweel.syl.jwtask.business.main.module.more;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineSexActivity extends JwActivity {
    Users users;
    String phone;
    String str1;
    ImageView iv_selm;
    ImageView iv_selw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_sex);
        setTitle(getString(R.string.minesex));
        ButterKnife.bind(this);

        users  = JwAppAplication.getInstance().users;
        phone = users.getUsername();
        iv_selm = (ImageView) findViewById(R.id.iv_selm);
        iv_selw = (ImageView) findViewById(R.id.iv_selw);
        String str= users.getSex();
        if(str.equals("男")){
            iv_selw.setVisibility(View.INVISIBLE);
        }else if(str.equals("女")){
            iv_selm.setVisibility(View.INVISIBLE);
        }
    }

    private class saveSex extends AsyncTask<String, Void, String> {
        private Context context;
        public saveSex(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "1";
            String sql="UPDATE users SET sex='"+str1+"'WHERE username ='"+phone+"'";
            try{
                CloudDB.execSQL(sql);
            }catch (CloudServiceException e){
                result = "0";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if(result.equals("1")){
                users.setSex(str1);
                JwAppAplication.getFinalDb().update(users);
            }else{
                ToastShow("数据保存失败");
            }
            MineSexActivity.this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mine_sex, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.v_man)
    void manClick() {
        str1 = "男";
        new saveSex(getMy()).execute();
    }

    @OnClick(R.id.v_woman)
    void womanClick() {
        str1 = "女";
        new saveSex(getMy()).execute();
    }
}
