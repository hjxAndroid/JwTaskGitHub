package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Dept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import api.util.Contants;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGroupActivity extends JwActivity {


    @Bind(R.id.et_group_name)
    EditText etGroupName;
    @Bind(R.id.fr_add)
    FrameLayout frAdd;
    @Bind(R.id.listview)
    ListView listview;

    List<Friend> friendList = new ArrayList<>();
    private CommonAdapter commonAdapter;

    List<Friend> friends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        setTitle("创建团队");
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        commonAdapter = new CommonAdapter<Friend>(self, friendList, R.layout.item_friend) {
            @Override
            public void convert(ViewHolder helper, Friend item) {
                helper.setText(R.id.tv_name,item.getFriend_name());
                helper.setText(R.id.tv_nick_name,item.getFriend_nickname());
            }
        };

        listview.setAdapter(commonAdapter);
    }

    @OnClick(R.id.fr_add)
    void nextClick() {
        JwStartActivity(GroupAddFriendListActivity.class, Contants.group);
    }

    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (StrUtils.IsNotEmpty(msg)&&msg.equals(Contants.group)) {
            String json = activityMsgEvent.getParam();
            friends = JwJSONUtils.getParseArray(json, Friend.class);
            friendList.addAll(friends);
            commonAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.bt_ok)
    void okClick() {
        new FinishRefresh(getMy()).execute();
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;

        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "0";
            //将组织，团队，和成员保存到数据库
            for(Friend friend : friends){

            }
            Dept dept = new Dept();
                try {
                    if(JwAppAplication.getInstance().jCloudDB.save(dept)){
                        result = "1";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("1")){
                ToastShow("创建成功");
                finish();
            }else{
                ToastShow("创建失败");
            }
            hideLoading();
        }
    }
}
