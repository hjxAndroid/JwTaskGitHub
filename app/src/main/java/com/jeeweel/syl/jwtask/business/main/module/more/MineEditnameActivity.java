package com.jeeweel.syl.jwtask.business.main.module.more;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import api.view.CustomDialog;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineEditnameActivity extends JwActivity {
    String phone;
    EditText et;
    String str1;
    Users users;
    String strtitle;
    boolean register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_editname);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        strtitle=intent.getStringExtra("title");
        register=intent.getBooleanExtra("register", false);
        et= (EditText) findViewById(R.id.et_name);
        users = JwAppAplication.getInstance().users;
        phone = users.getUsername();
        setTitle(strtitle);
        if(strtitle.equals("设置昵称")){
            et.setText(users.getNickname());
        }else if(strtitle.equals("设置邮箱")){
            et.setText(users.getEmail());
            et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
    }

    public void showAlertDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("是否完善个人资料");
        builder.setTitle("提示");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
                finish();
                Intent intent=new Intent();
                intent.putExtra("register", true);
                intent.setClass(MineEditnameActivity.this, MineActivity.class);
                JwStartActivity(intent);
            }
        });

        builder.setNegativeButton("否",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                        JwStartActivity(TabHostActivity.class);
                    }
                });

        builder.create().show();

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
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "1";
            String sql="";
            if(strtitle.equals("设置昵称")){
                users.setNickname(str1);
                sql="UPDATE users SET nickname='"+str1+"'WHERE username ='"+phone+"'";
            }else if(strtitle.equals("设置邮箱")){
                users.setEmail(str1);
                sql="UPDATE users SET email='"+str1+"'WHERE username ='"+phone+"'";
            }
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
                JwAppAplication.getFinalDb().update(users);
                JwAppAplication.setUsers(users);
                if(register==true){
                    showAlertDialog();
                }else {
                    MineEditnameActivity.this.finish();
                }
            }else{
                ToastShow("数据保存失败");
                MineEditnameActivity.this.finish();
            }
        }
    }

    @OnClick(R.id.btn_sub)
    void editClick() {
        str1 = et.getText().toString();
        if (StrUtils.IsNotEmpty(str1)) {
            new saveNickName(getMy()).execute();
        }else{
            ToastShow("内容不能为空（T T）");
        }
    }

    @OnClick(R.id.btn_del)
    void delclick(){
        et.setText("");
    }
}
