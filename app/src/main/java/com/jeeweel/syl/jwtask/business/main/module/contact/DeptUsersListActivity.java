package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.config.publicjsonclass.BaseItem;
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
import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DeptUsersListActivity extends JwListActivity {
    List<Userdept> mListItems = new ArrayList<Userdept>();

    @Bind(R.id.listview)
    PullToRefreshListView listview;
    private CommonAdapter commonAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<Userdept> list;

    /**
     * 用于判断是从哪请求过来的
     */
    private String tag = "";


    private Userdept userdept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        userdept = (Userdept)getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        if(null!=userdept){
            setTitle(userdept.getOrg_name()+">"+userdept.getDept_name());
        }
        ButterKnife.bind(this);
        initListViewController();
    }

    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<Userdept>(getMy(), mListItems, R.layout.item_friend) {
            @Override
            public void convert(ViewHolder helper, Userdept item) {
                helper.setText(R.id.tv_name, item.getUsername());
                helper.setText(R.id.tv_nick_name, item.getNickname());
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }


    @Override
    public void onListItemClick(int position){
        Userdept userdept = (Userdept)commonAdapter.getItem(position);
        JwStartActivity(FriendDetailActivity.class,userdept.getUsername());
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

            if(null!=userdept){
                try {
                    if(mode == 0 ){
                        setPage(true);
                        list = jCloudDB.findAllByWhere(Userdept.class,
                                "dept_code = " + StrUtils.QuotedStr(userdept.getDept_code()) + " limit "+pageStart+","+pageEnd);
                        mListItems.clear();
                    }else{
                        setPage(false);
                        list = jCloudDB.findAllByWhere(Userdept.class,
                                "user_name = " + StrUtils.QuotedStr(userdept.getDept_code()) + " limit "+pageStart+","+pageEnd);
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
