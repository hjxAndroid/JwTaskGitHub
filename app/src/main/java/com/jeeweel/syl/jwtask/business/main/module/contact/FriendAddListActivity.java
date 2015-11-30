package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.base.JeeweelApplication;
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

import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FriendAddListActivity extends JwListActivity {
    List<Friend> mListItems = new ArrayList<Friend>();

    @Bind(R.id.listview)
    PullToRefreshListView listview;
    private CommonAdapter commonAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<Friend> list;

    MenuTextView menuTextView;
    private Users users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        setTitle("好友列表");
        users = JwAppAplication.getInstance().users;
        ButterKnife.bind(this);
        initView();
        initListViewController();
    }

    private void initView(){
        menuTextView = new MenuTextView(getMy());
        menuTextView.setText("添加好友");
        menuTextView.setTextColor(getResources().getColor(R.color.white));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                JwStartActivity(FriendAddActivity.class);
            }
        });
        addMenuView(menuTextView);
    }

    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<Friend>(getMy(), mListItems, R.layout.item_friend_add) {
            @Override
            public void convert(ViewHolder helper, Friend item) {
                helper.setText(R.id.tv_name, item.getFriend_name());
                helper.setText(R.id.tv_msg, "我是"+item.getFriend_name());
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }


    @Override
    public void onListItemClick(int position){
        Friend outBoundItem = (Friend)commonAdapter.getItem(position);
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
                if(mode == 0 ){
                    list = jCloudDB.findAllByWhere(Friend.class,
                            "user_name = " + StrUtils.QuotedStr(users.getUsername()) +" limit "+0+","+10);
                }else{
                    setPage();
                    list = jCloudDB.findAllByWhere(Friend.class,
                            "user_name = " + StrUtils.QuotedStr(users.getUsername()) +" limit "+pageStart+","+pageEnd);
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

    private void setPage(){
        pageStart += addNum;
        pageEnd += addNum;
    }
}
