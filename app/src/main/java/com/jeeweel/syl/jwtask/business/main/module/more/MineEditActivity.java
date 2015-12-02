package com.jeeweel.syl.jwtask.business.main.module.more;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineEditActivity extends JwActivity {
    String phone;
    EditText et;
    String str1;
    Users users;
    String strtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_edit);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        strtitle=intent.getStringExtra("title");
        setTitle(strtitle);
        et= (EditText) findViewById(R.id.et_word);
        users  = JwAppAplication.getInstance().users;
        if(strtitle.equals("特长、兴趣")){
            et.setText(users.getStrong_point());
        }else if(strtitle.equals("个性签名")){
            et.setText(users.getSign());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_edit, menu);
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

    private class saveData extends AsyncTask<String, Void, String> {
        private Context context;
        public saveData(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String sql="";
            if(strtitle.equals("特长、兴趣")){
                users.setStrong_point(str1);
                sql="UPDATE users SET strong_point='"+str1+"'WHERE username ='"+phone+"'";
            }else if(strtitle.equals("个性签名")){
                users.setSign(str1);
                sql="UPDATE users SET sign='"+str1+"'WHERE username ='"+phone+"'";
            }
            try{
                CloudDB.execSQL(sql);
            }catch (CloudServiceException e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    @OnClick(R.id.btnsub)
    void editClick() {
        phone= users.getUsername();
        str1 = et.getText().toString();
        new saveData(getMy()).execute();
        this.finish();
    }

}
