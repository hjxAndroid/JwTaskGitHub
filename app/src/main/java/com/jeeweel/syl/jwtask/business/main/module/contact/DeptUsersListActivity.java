package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.ActionItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Dept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.DeptFileItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.FriendItem;
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
import com.jeeweel.syl.lib.api.config.publicjsonclass.ResMsgItem;
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

import net.tsz.afinal.http.AjaxParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import api.photoview.Bimp;
import api.util.Contants;
import api.util.FileUtils;
import api.util.OttUtils;
import api.util.ReduceUtil;
import api.util.Utils;
import api.view.CustomDialog;
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

    List<UserdeptItem> listCounts;
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
    private String deptCounts;

    private int FILE_SELECT_CODE = 1000;

    private String flieName = null;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        userdept = (Userdept) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        users = JwAppAplication.getInstance().getUsers();
        if (null != userdept) {
            orgCode = userdept.getOrg_code();
            dept_code = userdept.getDept_code();
        }
        ButterKnife.bind(this);
        initListViewController();
        initView();
    }

    private void initView() {
        titlePopup = new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ActionItem action = new ActionItem(getResources().getDrawable(R.drawable.a0), "添加成员");
        ActionItem action1 = new ActionItem(getResources().getDrawable(R.drawable.a1), "修改部门名");
        ActionItem action2 = new ActionItem(getResources().getDrawable(R.drawable.a2), "部门论坛");
        ActionItem action3 = new ActionItem(getResources().getDrawable(R.drawable.a3), "解散部门");
        ActionItem action4 = new ActionItem(getResources().getDrawable(R.drawable.a4), "上传文件");
        ActionItem action5 = new ActionItem(getResources().getDrawable(R.drawable.a5), "文件夹");

        titlePopup.addAction(action);
        titlePopup.addAction(action1);
        titlePopup.addAction(action2);
        titlePopup.addAction(action3);
        titlePopup.addAction(action4);
        titlePopup.addAction(action5);
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                if (position == 0) {
                    Intent intent = new Intent(getMy(), DeptSelectFriendListActivity.class);
                    intent.putExtra("userdept", userdept);
                    intent.putExtra(StaticStrUtils.baseItem, Contants.dept_add_friend);
                    startActivity(intent);
                } else if (position == 1) {

                    new FinishRefresIsFounder(getMy()).execute();

                } else if(position == 2){
                     JwStartActivity(ContantCommitActivity.class,userdept);
                }else if(position == 3){
                    showAlertDialog();
                    //上传文件
                }else if(position == 4){
                    showFileChooser();
                    //文件夹
                }else{
                    JwStartActivity(DeptFileListActivity.class,dept_code);
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

    /** 调用文件选择软件来选择文件 **/
    private void showFileChooser() {
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getMy(), "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /** 根据返回选择的文件，来进行上传操作 **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK&&requestCode==FILE_SELECT_CODE) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            String url = FileUtils.getImageAbsolutePath(DeptUsersListActivity.this,uri);
            if(StrUtils.IsNotEmpty(url)){

                flieName = url.substring(url.lastIndexOf("/")+1,url.length());
                AjaxParams params = new AjaxParams();
                try {
                    params.put(dept_code, new File(url));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String apiStr = Utils.uploadPic();
                progressDialog = ProgressDialog.show(DeptUsersListActivity.this, "上传", "正在努力上传中,请稍候！");
                progressDialog.setCancelable(false);
                JwHttpPost(apiStr, params, false);

            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void HttpSuccess(ResMsgItem resMsgItem) {
        new FielSaveFinishRefresh(getMy()).execute();
    }

    @Override
    public void HttpFail(String strMsg) {
        super.HttpFail(strMsg);

    }

    @Override
    public void HttpFinish() {
        super.HttpFinish();

    }

    public void showAlertDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("您确定要解散该部门");
        builder.setTitle("提示");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showLoading();
                new FinishRefreshDismiss(getMy()).execute();
            }
        });

        builder.setNegativeButton("否",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();

    }

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

                        String readSql = "select * from userdept left join picture on userdept.user_code = picture.pic_code WHERE dept_code = " + StrUtils.QuotedStr(userdept.getDept_code()) + " limit " + pageStart + "," + pageEnd;
                        //查找数据
                        list = jCloudDB.findAllBySql(UserdeptItem.class, readSql);

                        mListItems.clear();
                    } else {
                        setPage(false);
                        String readSql = "select * from userdept left join picture on userdept.user_code = picture.pic_code WHERE dept_code = " + StrUtils.QuotedStr(userdept.getDept_code()) + " limit " + pageStart + "," + pageEnd;
                        //查找数据
                        list = jCloudDB.findAllBySql(UserdeptItem.class, readSql);
                    }
                    String countsSql = "select * from userdept left join picture on userdept.user_code = picture.pic_code WHERE dept_code = " + StrUtils.QuotedStr(userdept.getDept_code());
                    listCounts = jCloudDB.findAllBySql(UserdeptItem.class, countsSql);
                    removeDuplicate(listCounts);
                    deptCounts = Integer.toString(listCounts.size());


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
                if (null != userdept) {
                    if (StrUtils.IsNotEmpty(deptCounts)) {
                        setTitle(userdept.getDept_name() + "(" + deptCounts + ")");
                    } else {
                        setTitle(userdept.getDept_name());
                    }
                }
                removeDuplicate(mListItems);
                mListItems.addAll(list);
                commonAdapter.notifyDataSetChanged();
            } else {
                //没有加载到数据
            }
            listview.onRefreshComplete();
            hideLoading();
        }
    }

    /**
     * 保存到数据库
     */
    private class FielSaveFinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private int mode = 0;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FielSaveFinishRefresh(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            DeptFileItem deptFileItem = new DeptFileItem();
            deptFileItem.setNickname(users.getNickname());
            deptFileItem.setDepart_code(dept_code);
            deptFileItem.setFounder_code(users.getUser_code());
            deptFileItem.setFile_name(flieName);

            try {
                jCloudDB.save(deptFileItem);
            }  catch (CloudServiceException e) {
            result = "0";
            e.printStackTrace();
            }



            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {

                ToastShow("文件已成功上传到部门文件夹");
            } else {
                ToastShow("文件上传失败");
            }

            progressDialog.dismiss();
        }
    }

    private class FinishRefreshDismiss extends AsyncTask<String, Void, String> {
        JCloudDB jCloudDB;
        List<Orgunit> listIsFounder;
        List<Userdept> listDeptFounder;

        /**
         * @param context 上下文
         */
        public FinishRefreshDismiss(Context context) {
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "1";

            boolean flagUserDept = false;
            boolean flagUserOrg = false;
            boolean flagDept = false;

            try {
                if (null != users) {

                    listDeptFounder = jCloudDB.findAllByWhere(Userdept.class, " org_code = " + StrUtils.QuotedStr(orgCode) + " and user_code = " + StrUtils.QuotedStr(users.getUser_code()) + " and admin_state = 1 ");
                    listIsFounder = jCloudDB.findAllByWhere(Orgunit.class, " org_code = " + StrUtils.QuotedStr(orgCode) + " and founder_code = " + StrUtils.QuotedStr(users.getUser_code()));

                    if (ListUtils.IsNotNull(listDeptFounder) || ListUtils.IsNotNull(listIsFounder)) {

                        flagUserDept = jCloudDB.deleteByWhere(Userdept.class, " org_code = " + "\'" + orgCode + "\' and dept_code = "+StrUtils.QuotedStr(dept_code));
                        //flagUserOrg = jCloudDB.deleteByWhere(Userorg.class, " org_code = " + "\'" + orgCode + "\' and depart_code = "+StrUtils.QuotedStr(dept_code));
                        flagDept = jCloudDB.deleteByWhere(Dept.class, " org_code = " + "\'" + orgCode + "\' and depart_code = "+StrUtils.QuotedStr(dept_code));

                        if (flagUserDept && flagDept) {
                            result = "1";
                        } else {
                            result = "0";
                        }
                    } else {
                        result = "2";
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
                ToastShow("解散成功");
                OttUtils.push("deptAdd_refresh", "");
                hideLoading();
                finish();
            } else if (result.equals("2")) {
                ToastShow("您没有权限解散部门");
                hideLoading();
            } else {
                ToastShow("解散失败");
                hideLoading();
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

    /**
     * 去除多余元素
     *
     * @param list
     */
    public void removeDuplicate(List<UserdeptItem> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getUser_code().equals(list.get(i).getUser_code())) {
                    list.remove(j);
                }
            }
        }
    }
}
