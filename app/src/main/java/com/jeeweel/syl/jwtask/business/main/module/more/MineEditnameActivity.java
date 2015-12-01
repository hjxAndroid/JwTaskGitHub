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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineEditnameActivity extends JwActivity {

    String phone;
    EditText editText1;
    String str1;
    List<Users> usersList = JwAppAplication.getInstance().finalDb.findAll(Users.class);
    Users users=usersList.get(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_editname);
        ButterKnife.bind(this);
        setTitle(getString(R.string.nickname));
        String nickname=users.getNickname();
        editText1= (EditText) findViewById(R.id.edittext1);
        editText1.setText(nickname);
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

    private class saveSignInformation extends AsyncTask<String, Void, String> {
        private Context context;
        public saveSignInformation(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            Logv("adsadsa" + phone + "--" + users.getNickname());
            String sql="UPDATE users SET nickname='"+str1+"'WHERE username ='"+phone+"'";
            Logv("adsadsa" + sql);
            Boolean bResult=false;
            try{
                bResult = CloudDB.execSQL(sql);
            }catch (CloudServiceException e){

            }

            Logv("adsadsasuccess"+bResult+"--"+users.getNickname());
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    @OnClick(R.id.btnsub)
    void editClick() {
        phone= users.getUsername();
        editText1 = (EditText) findViewById(R.id.edittext1);
        str1 = editText1.getText().toString();
        users.setNickname(str1);
        new saveSignInformation(getMy()).execute();
        this.finish();
    }

    @OnClick(R.id.btndel)
    void delclick(){
        editText1 = (EditText) findViewById(R.id.edittext1);
        editText1.setText("");
    }
}
