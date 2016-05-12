package com.jeeweel.syl.jwtask.business.main.module.news;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.jeeweel.syl.jwtask.business.config.jsonclass.Publicity;
import com.jeeweel.syl.jwtask.business.config.jsonclass.SignedPictures;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.basic.JwCaptureActivity;
import com.jeeweel.syl.jwtask.business.main.module.contact.FriendAddListActivity;
import com.jeeweel.syl.jwtask.business.main.module.task.FzSortActivity;
import com.jeeweel.syl.jwtask.business.main.module.task.OverTaskListActivity;
import com.jeeweel.syl.jwtask.business.main.module.task.PublicReadOpotionActivity;
import com.jeeweel.syl.jwtask.business.main.module.task.PublicyListActivity;
import com.jeeweel.syl.jwtask.business.main.module.task.SignListActivity;
import com.jeeweel.syl.jwtask.business.main.module.task.TaskJobHomeActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.integer.IntUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import api.util.Contants;
import api.view.CustomDialog;
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
    @Bind(R.id.tv_friend_num)
    TextView tvFriendNum;

    private List<Friend> friendList;

    //即将超期任务
    private List<Task> taskList;

    private String myphone;

    private String orgCode;

    private Users users;

    List<News> allList = new ArrayList<News>();

    CommonAdapter commonAdapter;

    List<Task> wshlist;
    List<Task> wqrlist;

    List<Publicity> publicylist;
    boolean flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideBack(true);
        setContentView(R.layout.activity_news_home);
        ButterKnife.bind(this);
        setTitle(getString(R.string.news));
        initView();
        initRight();
        getData();
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("刷新");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                allList.clear();
                showLoading();
                new FinishRefresh(getMy()).execute(myphone);
            }
        });
        addMenuView(menuTextView);
    }

    private void initView() {
        commonAdapter = new CommonAdapter<News>(getMy(), allList, R.layout.item_news) {
            @Override
            public void convert(ViewHolder helper, News item) {
                String readstate = item.getReadstate();

                //类型
                helper.setText(R.id.task, item.getMsg_name());

                //是否有未读
                ImageView ivnum = helper.getImageView(R.id.iv_task_num);
                //时间
                TextView tv_time = helper.getView(R.id.tv_task_time);
                //未读数量
                TextView tv_news_num = helper.getView(R.id.tv_news_num);

                String readSum = item.getReadsum();
                String alread = item.getAlread();
                int unread = 0;
                if (StrUtils.IsNotEmpty(readSum) && StrUtils.IsNotEmpty(alread)) {
                    unread = Integer.parseInt(readSum) - Integer.parseInt(alread);
                }
                if (unread > 0) {
                    tv_news_num.setVisibility(View.VISIBLE);
                    tv_news_num.setText(IntUtils.toStr(unread));
                } else {
                    tv_news_num.setVisibility(View.GONE);
                }


                if (StrUtils.IsNotEmpty(readstate) && readstate.equals("0")) {
                    ivnum.setVisibility(View.VISIBLE);
                    tv_time.setVisibility(View.VISIBLE);
                    tv_time.setText(item.getCreate_time());

                    //消息标题
                    helper.setText(R.id.tv_task_news, StrUtils.IsNull(item.getMsg_title()));
                } else {
                    ivnum.setVisibility(View.GONE);
                    tv_time.setVisibility(View.GONE);
                    helper.setText(R.id.tv_task_news, "暂无消息");
                }
                ImageView ivhead = helper.getImageView(R.id.iv_head);
                ivhead.setBackgroundResource(item.getDraw_id());

            }
        };
        listview.setAdapter(commonAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        JwStartActivity(PublicyListActivity.class);
                        break;
                    case 1:
                        JwStartActivity(PublicReadOpotionActivity.class);
                        break;
                    case 2:
                        News news = (News) commonAdapter.getItem(position);
                        JwStartActivity(TaskJobHomeActivity.class, news);
                        break;
                    default:
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
        List<SignedPictures> unSignList;
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
                News news = new News();
                News news1 = new News();
                News news2 = new News();
                news.setMsg_name("公告");
                news1.setMsg_name("签到");
                news2.setMsg_name("任务");

                news.setDraw_id(R.drawable.publicy);
                news1.setDraw_id(R.drawable.sign);
                news2.setDraw_id(R.drawable.task);

                newsList.add(news);
                newsList.add(news1);
                newsList.add(news2);

                //公告未读
//                publicylist = jCloudDB.findAllByWhere(Publicity.class,
//                        "auditor_code like " + StrUtils.QuotedStrLike(users.getUser_code()) + "and now_state = 2 or now_state = 4 or now_state = 7");

                //签到列表
                unSignList = jCloudDB.findAllBySql(SignedPictures.class, "SELECT t.* from sign t where t.sign_code not in (SELECT b.sign_code from signed b where b.sign_code in(SELECT t.sign_code from sign t where t.receive_code LIKE " + StrUtils.QuotedStrLike(users.getUser_code()) + ") and b.sign_user_code = " + StrUtils.QuotedStr(users.getUser_code()) + ")" + " and t.receive_code LIKE  " + StrUtils.QuotedStrLike(users.getUser_code()) +
                        " GROUP BY " +
                        " sign_code ORDER BY create_time DESC ");

                //任务数量
                //未审核数量
                wshlist = jCloudDB.findAllByWhere(Task.class,
                        "auditor_code like " + StrUtils.QuotedStrLike(users.getUser_code()) + "and now_state = 2 or now_state = 4 or now_state = 7");
                //负责未读数量
                wqrlist = jCloudDB.findAllByWhere(Task.class,
                        "principal_code like " + StrUtils.QuotedStrLike(users.getUser_code()) + "and now_state = " + 0);

                //请求好友
                friendList = jCloudDB.findAllByWhere(Friend.class,
                        "user_name=" + StrUtils.QuotedStr(phone) + "and read_state=0 " + "ORDER BY create_time DESC");

                taskList = jCloudDB.findAllByWhere(Task.class,"principal_code = "+StrUtils.QuotedStr(users.getUser_code())+" AND now_state < 3 AND DATE_SUB(over_time, interval 4 day) < NOW() and over_time >= NOW()");

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

                    int signnum = 0;
                    if(ListUtils.IsNotNull(unSignList)){
                        signnum = unSignList.size();
                    }
                    if(signnum!=0){
                        newsList.get(1).setReadstate("0");
                        newsList.get(1).setReadsum(signnum + "");
                        newsList.get(1).setAlread("0");
                        newsList.get(1).setMsg_title(unSignList.get(0).getSign_title());
                    }


                    int tasknnum = 0;
                    if(ListUtils.IsNotNull(wshlist)){
                        tasknnum = wshlist.size();
                        newsList.get(2).setMsg_title(wshlist.get(0).getTask_name());
                    }
                    if(ListUtils.IsNotNull(wqrlist)){
                        tasknnum += wqrlist.size();
                        newsList.get(2).setMsg_title(wqrlist.get(0).getTask_name());
                    }
                    if(tasknnum!=0){
                        newsList.get(2).setReadstate("0");
                        newsList.get(2).setReadsum(tasknnum + "");
                        newsList.get(2).setAlread("0");

                    }

                    allList.addAll(newsList);
                    commonAdapter.notifyDataSetChanged();
                }


                //刷新好友列表
                if (ListUtils.IsNotNull(friendList)) {
                    int size = friendList.size();
                    //好友请求数量
                    tvFriendNum.setText(IntUtils.toStr(size));
                    tvFriendNum.setVisibility(View.VISIBLE);
                    tvFriendTime.setVisibility(View.VISIBLE);

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
                    tvFriendTime.setVisibility(View.GONE);
                    ivFriendNum.setVisibility(View.GONE);
                    tvFriendNum.setVisibility(View.GONE);

                }

                if(ListUtils.IsNotNull(taskList)&&taskList.size()>0){
                    showAlertDeleatDialog();
                }

            } else {
                ToastShow("请求出错");
            }
            hideLoading();
        }
    }


    private void showAlertDeleatDialog() {
        if(flag){
            CustomDialog.Builder builder = new CustomDialog.Builder(this);
            builder.setMessage("您有"+taskList.size()+"条任务即将到期");
            builder.setTitle("提示");
            builder.setPositiveButton("前往查看", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    flag = true;
                    dialog.dismiss();
                    //即将超期
                    JwStartActivity(OverTaskListActivity.class);
                }
            });

            builder.setNegativeButton("暂时没空",
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            flag = true;
                            dialog.dismiss();
                        }
                    });

            builder.create().show();
            flag = false;
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
