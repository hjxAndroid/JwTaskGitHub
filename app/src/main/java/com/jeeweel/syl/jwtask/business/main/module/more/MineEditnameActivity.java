package com.jeeweel.syl.jwtask.business.main.module.more;

import android.content.Context;
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

public class MineEditnameActivity extends JwActivity {

    String phone;
    EditText et_name;
    String str1;
    Users users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_editname);
        ButterKnife.bind(this);
        setTitle(getString(R.string.nickname));
        users  = JwAppAplication.getInstance().users;
        String nickname=users.getNickname();
        et_name= (EditText) findViewById(R.id.et_name);
        et_name.setText(nickname);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mine_editname, menu);
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

    private class saveNickName extends AsyncTask<String, Void, String> {
        private Context context;
        public saveNickName(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String sql="UPDATE users SET nickname='"+str1+"'WHERE username ='"+phone+"'";
            Boolean bResult=false;
            try{
                bResult = CloudDB.execSQL(sql);
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
 //       users  = JwAppAplication.getInstance().users;
        phone= users.getUsername();
        str1 = et_name.getText().toString();
        users.setNickname(str1);
        new saveNickName(getMy()).execute();
        this.finish();
    }

    @OnClick(R.id.btndel)
    void delclick(){
        et_name.setText("");
    }
}
