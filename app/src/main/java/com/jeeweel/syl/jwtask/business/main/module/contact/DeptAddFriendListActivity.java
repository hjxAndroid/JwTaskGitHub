package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.integer.IntUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.jeeweel.syl.lib.api.core.toast.JwToast;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import api.util.Contants;
import api.util.OttUtils;
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DeptAddFriendListActivity extends JwListActivity {
    List<Friend> mListItems = new ArrayList<Friend>();

    @Bind(R.id.listview)
    PullToRefreshListView listview;
    private CommonAdapter commonAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<Friend> list;

    private Users users;

    /**
     * 用于判断是从哪请求过来的
     */
    private String tag = "";

    List<Integer> integers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        setTitle("添加成员");
        users = JwAppAplication.getInstance().users;
        tag = getIntent().getStringExtra(StaticStrUtils.baseItem);
        ButterKnife.bind(this);
        initView();
        initListViewController();
    }

    private void initView(){
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成");
        menuTextView.setTextColor(getResources().getColor(R.color.white));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                List<Friend> friendcds = new ArrayList<>();
                for(Friend friend : mListItems){
                    if(friend.isChoose()){
                        friendcds.add(friend);
                    }
                }


                String json = new Gson().toJson(friendcds);

                //部门添加好友请求
                if(StrUtils.IsNotEmpty(tag)&&tag.equals(Contants.group)){
                    OttUtils.push(Contants.group,json);
                    finish();
                }

                //签到添加好友请求
                if(StrUtils.IsNotEmpty(tag)&&tag.equals(Contants.sign)){
                    OttUtils.push(Contants.sign,json);
                    finish();
                }
            }
        });
        addMenuView(menuTextView);
    }

    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<Friend>(getMy(), mListItems, R.layout.item_group_add_friend) {
            @Override
            public void convert(final ViewHolder helper, final Friend item) {
                helper.setText(R.id.tv_name, item.getFriend_nickname());

                final CheckBox choose = helper.getView(R.id.ck_choose);
                choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                        int position = helper.getPosition();
                       if(choose.isChecked()){
                           Friend friend = mListItems.get(position);
                           friend.setChoose(true);
                       }else{
                           Friend friend = mListItems.get(position);
                           friend.setChoose(false);
                       }
                    }
                });
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }


    @Override
    public void onListItemClick(int position){

    }

    @Override
    public void onListViewHeadRefresh(){
        showLoading();
        new FinishRefresh(getMy(), 0).execute();
    }

    @Override
    public void onListViewFooterRefresh(){
        showLoading();
        new FinishRefresh(getMy(), 1).execute();
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private int mode = 0;
        private JCloudDB jCloudDB;
        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context,int mode)
        {
            this.context = context;
            this.mode = mode;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "0";

            if(null!=users){
                try {
                    if(mode == 0 ){
                        setPage(true);
                        list = jCloudDB.findAllByWhere(Friend.class,
                                "user_name = " + StrUtils.QuotedStr(users.getUsername()) + "and state=2" +" limit "+pageStart+","+pageEnd);
                    }else{
                        setPage(false);
                        list = jCloudDB.findAllByWhere(Friend.class,
                                "user_name = " + StrUtils.QuotedStr(users.getUsername()) + "and state=2" +" limit "+pageStart+","+pageEnd);
                    }
                } catch (CloudServiceException e) {
                    e.printStackTrace();
                }

                if(ListUtils.IsNotNull(list)){
                    result = "1";
                }else{
                    result = "0";
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("1")){
                mListItems.addAll(list);
                commonAdapter.notifyDataSetChanged();
            }else{
                //没有加载到数据
            }
            listview.onRefreshComplete();
            hideLoading();
        }
    }

    /**
     *分页增数
     */

    private void setPage(boolean tag){
        if(tag){
            pageStart = 0;
            pageEnd = 10;
        }else{
            pageStart += addNum;
            pageEnd += addNum;
        }
    }
}
