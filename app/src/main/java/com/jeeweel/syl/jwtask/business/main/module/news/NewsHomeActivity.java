package com.jeeweel.syl.jwtask.business.main.module.news;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.News;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Sign;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.config.jsonclass.V_publicityunread;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.contact.FriendAddListActivity;
import com.jeeweel.syl.jwtask.business.main.module.service.Helper;
import com.jeeweel.syl.jwtask.business.main.module.task.PublicyListActivity;
import com.jeeweel.syl.jwtask.business.main.module.task.SignListActivity;
import com.jeeweel.syl.jwtask.business.main.module.task.TaskJobHomeActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
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

public class NewsHomeActivity extends JwActivity {


    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.rl_friend_news)
    TextView rlFriendNews;
    @Bind(R.id.tv_friend_time)
    TextView tvFriendTime;
    @Bind(R.id.iv_friend_num)
    ImageView ivFriendNum;

    private List<Friend> friendList;

    private String myphone;

    private String orgCode;

    private Users users;

    List<News> allList = new ArrayList<News>();

    CommonAdapter commonAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideBack(true);
        setContentView(R.layout.activity_news_home);
        ButterKnife.bind(this);
        setTitle(getString(R.string.news));
        initView();
        getData();
    }

    private void initView(){
        commonAdapter = new CommonAdapter<News>(getMy(), allList, R.layout.item_news) {
            @Override
            public void convert(ViewHolder helper, News item) {
                String title = item.getMsg_title();
                String readstate = item.getReadstate();

                //类型
                helper.setText(R.id.task, item.getMsg_name());

                //是否有未读
                ImageView ivnum = helper.getImageView(R.id.iv_task_num);

                if(StrUtils.IsNotEmpty(readstate)&&readstate.equals("0")){
                    ivnum.setVisibility(View.VISIBLE);
                    //消息标题
                    if(StrUtils.IsNotEmpty(title)){
                        helper.setText(R.id.tv_task_news, item.getMsg_title());
                        helper.setText(R.id.tv_task_time, item.getCreate_time());
                    }
                }else{
                    ivnum.setVisibility(View.GONE);
                    helper.setText(R.id.tv_task_news, "暂无消息");
                    helper.setText(R.id.tv_task_time, "");
                }
                ImageView ivhead = helper.getImageView(R.id.iv_head);
                ivhead.setBackgroundResource(item.getDraw_id());

            }
        };
        listview.setAdapter(commonAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){
                    case 0 :
                        JwStartActivity(PublicyListActivity.class);
                        break;
                    case 1 :
                        JwStartActivity(SignListActivity.class);
                        break;
                    case 2 :
                        News news = (News)commonAdapter.getItem(position);
                        JwStartActivity(TaskJobHomeActivity.class,news);
                        break;
                    default :
                        break;
                }

            }
        });
    }

    private void getData() {

        users = JwAppAplication.getInstance().getUsers();

        orgCode = (String) SharedPreferencesUtils.get(getMy(), Contants.org_code, "");

        if (null != users) {
            myphone = users.getUsername();
            showLoading();
            new FinishRefresh(getMy()).execute(myphone);
        }
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        JCloudDB jCloudDB;
        List<News> newsList = new ArrayList<News>();

        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            String phone = params[0].toString();

            try {
                if (StrUtils.isEmpty(orgCode)) {
                    //取默认组织
                    List<Userorg> userorgs = jCloudDB.findAllByWhere(Userorg.class,
                            "user_name=" + StrUtils.QuotedStr(phone) + "ORDER BY create_time");
                    if (ListUtils.IsNotNull(userorgs)) {
                        orgCode = userorgs.get(0).getOrg_code();
                        SharedPreferencesUtils.save(getMy(), Contants.org_code, userorgs.get(0).getOrg_code());
                        SharedPreferencesUtils.save(getMy(), Contants.org_name, userorgs.get(0).getOrg_name());
                    }
                }
                //通过存储过程获取消息列表，用完删除
                String sql = "call get_msg_list('" + users.getUser_code() + "','" + orgCode + "');";
                CloudDB.execSQL(sql);

                String newSql = "select * from tmp" + users.getUser_code();
                //查找数据
                newsList = jCloudDB.findAllBySql(News.class, newSql);

                String deletSql = "DROP TABLE tmp" + users.getUser_code();
                CloudDB.execSQL(deletSql);

                //请求好友
                friendList = jCloudDB.findAllByWhere(Friend.class,
                        "user_name=" + StrUtils.QuotedStr(phone) + "and read_state=0 " + "ORDER BY create_time DESC");
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {

                if (ListUtils.IsNotNull(newsList)) {
                    newsList.get(0).setDraw_id(R.drawable.publicy);
                    newsList.get(1).setDraw_id(R.drawable.sign);
                    newsList.get(2).setDraw_id(R.drawable.task);
                    allList.addAll(newsList);
                    commonAdapter.notifyDataSetChanged();
                }


                //刷新好友列表
                if (ListUtils.IsNotNull(friendList)) {
                    for (Friend friend : friendList) {
                        int sendState = friend.getSend_state();
                        int state = friend.getState();
                        //假如是接受者，提醒好友添加
                        if (sendState == 0) {
                            //假如是未操作
                            if (state == 0) {
                                rlFriendNews.setText(friend.getFriend_nickname() + "请求添加您为好友");
                                ivFriendNum.setVisibility(View.VISIBLE);
                                tvFriendTime.setText(friend.getCreate_time());
                                break;
                            }
                            //假如是发送者，提醒好友通过
                        } else if (sendState == 1) {
                            //假如是已经同意，提醒好友添加成功
                            if (state == 2) {
                                rlFriendNews.setText("您已经成功添加" + friend.getFriend_nickname() + "为好友");
                                ivFriendNum.setVisibility(View.VISIBLE);
                                tvFriendTime.setText(friend.getCreate_time());
                                break;
                            }
                        }
                    }
                } else {
                    rlFriendNews.setText("暂无消息");
                    ivFriendNum.setVisibility(View.GONE);
                }

            } else {
                ToastShow("请求出错");
            }
            hideLoading();
        }
    }


    @OnClick(R.id.rl_friend)
    void friendClick() {
        JwStartActivity(FriendAddListActivity.class);
    }

    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (msg.equals("news_refresh")) {
            allList.clear();
            showLoading();
            new FinishRefresh(getMy()).execute(myphone);
        }
    }
}
