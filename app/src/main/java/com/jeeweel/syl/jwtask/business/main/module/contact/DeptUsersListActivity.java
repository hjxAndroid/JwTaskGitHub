package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.ActionItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Dept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.UserdeptItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.more.MineEditnameActivity;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.config.publicjsonclass.BaseItem;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
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
import api.view.TitlePopup;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DeptUsersListActivity extends JwListActivity {
    List<UserdeptItem> mListItems = new ArrayList<UserdeptItem>();

    @Bind(R.id.listview)
    PullToRefreshListView listview;
    private CommonAdapter commonAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数
    private TitlePopup titlePopup;
    private List<Userorg> userorgsList;


    List<UserdeptItem> list;
    List<Orgunit> orgunits;
    Orgunit orgunit;

    /**
     * 用于判断是从哪请求过来的
     */
    private String tag = "";


    private Userdept userdept;
    private String orgCode;
    private Users users;
    private List<Userdept> listUserDept;
    private String dept_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        userdept = (Userdept) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        users = JwAppAplication.getUsers();
        if (null != userdept) {
            setTitle(userdept.getDept_name());
            orgCode = userdept.getOrg_code();
            dept_code = userdept.getDept_code();
        }
        ButterKnife.bind(this);
        initListViewController();
        initView();
    }

    private void initView() {
        titlePopup = new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ActionItem action = new ActionItem(getResources().getDrawable(R.drawable.a0), "添加");
        ActionItem action1 = new ActionItem(getResources().getDrawable(R.drawable.a5), "修改部门名");
        titlePopup.addAction(action);
        titlePopup.addAction(action1);
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                if (position == 0) {
                    Intent intent = new Intent(getMy(), DeptSelectFriendListActivity.class);
                    intent.putExtra("userdept", userdept);
                    intent.putExtra(StaticStrUtils.baseItem, Contants.dept_add_friend);
                    startActivity(intent);
                    //     finish();
                } else {

                    new FinishRefresIsFounder(getMy()).execute();

//                    Intent intent = new Intent();
//                    intent.putExtra("title", "修改部门名");
//                    intent.putExtra("code", userdept.getDept_code());
//                    intent.setClass(DeptUsersListActivity.this, MineEditnameActivity.class);
//                    JwStartActivity(intent);
                }
            }
        });
        MenuImageView menuImageView = new MenuImageView(getMy());
        menuImageView.setBackgroundResource(R.drawable.more);
        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titlePopup.show(v);
            }
        });
        addMenuView(menuImageView);
    }
//    private void initView() {
//        MenuTextView menuTextView = new MenuTextView(getMy());
//        menuTextView.setText("添加");
//        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
//        menuTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                Intent intent = new Intent(getMy(), DeptSelectFriendListActivity.class);
//                intent.putExtra("userdept", userdept);
//                intent.putExtra(StaticStrUtils.baseItem, Contants.dept_add_friend);
//                startActivity(intent);
//                finish();
//            }
//        });
//        addMenuView(menuTextView);
//        titlePopup = new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        ActionItem action = new ActionItem(getResources().getDrawable(R.drawable.a0), "添加");
//        ActionItem action1 = new ActionItem(getResources().getDrawable(R.drawable.a0), "解散");
//        titlePopup.addAction(action);
//        titlePopup.addAction(action1);
//        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
//            @Override
//            public void onItemClick(ActionItem item, int position) {
//                if (position == 0) {
//                    Intent intent = new Intent(getMy(), DeptSelectFriendListActivity.class);
//                    intent.putExtra("userdept", userdept);
//                    intent.putExtra(StaticStrUtils.baseItem, Contants.dept_add_friend);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    new FinishRefreshDismiss(getMy()).execute();
//                    finish();
//                }
//            }
//        });
//        MenuImageView menuImageView = new MenuImageView(getMy());
//        menuImageView.setBackgroundResource(R.drawable.more);
//        menuImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                titlePopup.show(v);
//            }
//        });
//        addMenuView(menuImageView);
//
//    }

    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<UserdeptItem>(getMy(), mListItems, R.layout.item_friend) {
            @Override
            public void convert(ViewHolder helper, UserdeptItem item) {
                String nickname = item.getNickname();
                helper.setText(R.id.tv_name, item.getUsername());
                helper.setText(R.id.tv_nick_name, nickname);

                if (item.getPic_road() != null) {
                    ImageView iv_photo = helper.getImageView(R.id.iv_xz);
                    //     Logv("qwqwqw--"+Utils.getPicUrl()+"!!!"+item.getPhoto_code());
                    JwImageLoader.displayImage(Utils.getPicUrl() + item.getPic_road(), iv_photo);
                } else {
                    if (nickname.length() > 2) {
                        nickname = nickname.substring(nickname.length() - 2, nickname.length());
                        helper.setText(R.id.tv_user_head1, nickname);
                    } else {
                        helper.setText(R.id.tv_user_head1, nickname);
                    }
                }

                if (item.getAdmin_state() == 1) {
                    ImageView tv_name = helper.getImageView(R.id.iv_admin);
                    tv_name.setVisibility(View.VISIBLE);
                }
            }
        };
        setCommonAdapter(commonAdapter);
        super.initListViewController();
    }


    @Override
    public void onListItemClick(int position) {
        String flag = "DeptUsers";
        UserdeptItem userdept = (UserdeptItem) commonAdapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra("flag", true);
        intent.putExtra("mark", flag);
        intent.putExtra(StaticStrUtils.baseItem, userdept.getUsername());
        intent.putExtra("friend_code", userdept.getUser_code());
        intent.putExtra("org_code", userdept.getOrg_code());
        intent.putExtra("dept_code", dept_code);
        intent.setClass(DeptUsersListActivity.this, FriendDetailActivity.class);
        JwStartActivity(intent);
        //     JwStartActivity(FriendDetailActivity.class, userdept.getUsername());
    }

    @Override
    public void onListViewHeadRefresh() {
        showLoading();
        new FinishRefresh(getMy(), 0).execute();
    }

    @Override
    public void onListViewFooterRefresh() {
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
        public FinishRefresh(Context context, int mode) {
            this.context = context;
            this.mode = mode;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            if (null != userdept) {
                try {
                    if (mode == 0) {
                        setPage(true);

                        String readSql  = "select * from userdept left join picture on userdept.user_code = picture.pic_code WHERE dept_code = "+ StrUtils.QuotedStr(userdept.getDept_code()) + " limit " + pageStart + "," + pageEnd;
                        //查找数据
                        list = jCloudDB.findAllBySql(UserdeptItem.class, readSql);

                        mListItems.clear();
                    } else {
                        setPage(false);
                        String readSql  = "select * from userdept left join picture on userdept.user_code = picture.pic_code WHERE dept_code = "+ StrUtils.QuotedStr(userdept.getDept_code()) + " limit " + pageStart + "," + pageEnd;
                        //查找数据
                        list = jCloudDB.findAllBySql(UserdeptItem.class, readSql);
                    }



//                    if (ListUtils.IsNotNull(list)) {
//                        result = "1";
//                        for (Userdept userdept : list) {
//                            //取头像
//                            String user_code = userdept.getUser_code();
//                            String sSql = "pic_code=?";
//                            SqlInfo sqlInfo = new SqlInfo();
//                            sqlInfo.setSql(sSql);
//                            sqlInfo.addValue(user_code);
//                            sSql = sqlInfo.getBuildSql();
//                            List<Picture> pictureList = jCloudDB.findAllByWhere(Picture.class, sSql);
//                            if (ListUtils.IsNotNull(pictureList)) {
//                                Picture picture = pictureList.get(0);
//                                String path = picture.getPic_road();
//                                if (StrUtils.IsNotEmpty(path)) {
//                                    //存头像
//                                    userdept.setPhoto_code(path);
//                                }
//                            }
//                        }
//                    } else {
//                        result = "0";
//                    }
                } catch (CloudServiceException e) {
                    result = "0";
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                mListItems.addAll(list);
                commonAdapter.notifyDataSetChanged();
            } else {
                //没有加载到数据
            }
            listview.onRefreshComplete();
            hideLoading();
        }
    }


    private class FinishRefreshDismiss extends AsyncTask<String, Void, String> {
        private Context context;

        /**
         * @param context 上下文
         */
        public FinishRefreshDismiss(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "1";


            boolean flagUserDept = false;
            boolean flagUserOrg = false;
            boolean flagDept = false;
            boolean flagOrgUnit = false;
            JCloudDB jCloudDB = new JCloudDB();
            try {
                orgunits = jCloudDB.findAllByWhere(Orgunit.class, " org_code = " + "\'" + orgCode + "\'");
                if (ListUtils.IsNotNull(orgunits)) {
                    orgunit = orgunits.get(0);

                    flagUserDept = jCloudDB.deleteByWhere(Userdept.class, " org_code = " + "\'" + orgCode + "\'");
                    flagUserOrg = jCloudDB.deleteByWhere(Userorg.class, " org_code = " + "\'" + orgCode + "\'");
                    flagDept = jCloudDB.deleteByWhere(Dept.class, " org_code = " + "\'" + orgCode + "\'");
                    flagOrgUnit = jCloudDB.deleteByWhere(Orgunit.class, " org_code = " + "\'" + orgCode + "\'");
                    if (flagUserDept && flagUserOrg && flagDept && flagOrgUnit) {
                        result = "1";
                    } else {
                        result = "0";
                    }
                } else {
                    result = "2";
                }

            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                ToastShow("解散成功");
                OttUtils.push("deptAdd_refresh", "");
            } else if (result.equals("2")) {
                ToastShow("您没有权限解散组织");
            } else {
                ToastShow("解散失败");
            }
        }
    }


    /**
     * 分页增数
     */

    private void setPage(boolean tag) {
        if (tag) {
            pageStart = 0;
            pageEnd = 10;
        } else {
            pageStart += addNum;
            pageEnd += addNum;
        }
    }

    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (StrUtils.IsNotEmpty(msg) && msg.equals("deptUsers_refresh")) {
            list.clear();
            onListViewHeadRefresh();
        } else if (StrUtils.IsNotEmpty(msg) && msg.equals("depart_name_refresh")) {
            setTitle(activityMsgEvent.getParam());
        } else if (StrUtils.IsNotEmpty(msg) && msg.equals("deptAdd_refresh")) {
            list.clear();
            onListViewHeadRefresh();
        }
    }

    private class FinishRefresIsFounder extends AsyncTask<String, Void, String> {
        private Context context;
        List<Orgunit> orgunits;

        /**
         * @param context 上下文
         */
        public FinishRefresIsFounder(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "1";


            JCloudDB jCloudDB = new JCloudDB();
            try {
                if (null != users) {
                    listUserDept = jCloudDB.findAllByWhere(Userdept.class, " org_code = " + StrUtils.QuotedStr(orgCode) + " and user_code = " + StrUtils.QuotedStr(users.getUser_code()) + " and admin_state = 1 ");
                    orgunits = jCloudDB.findAllByWhere(Orgunit.class, " org_code = " + StrUtils.QuotedStr(orgCode) +
                            "and founder_code =" + StrUtils.QuotedStr(users.getUser_code()));

                    if (ListUtils.IsNotNull(orgunits) || ListUtils.IsNotNull(listUserDept)) {
                        result = "1";
                    } else {
                        result = "0";
                    }
                }
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                Intent intent = new Intent();
                intent.putExtra("title", "修改部门名");
                intent.putExtra("code", userdept.getDept_code());
                intent.setClass(DeptUsersListActivity.this, MineEditnameActivity.class);
                JwStartActivity(intent);
            } else {
                ToastShow("您没有权限修改");
            }
            hideLoading();
        }
    }
}
