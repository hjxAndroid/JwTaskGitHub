package com.jeeweel.syl.jwtask.business.main.module.contact;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.DeptFileItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.FriendItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Orgunit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.UserdeptItem;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.config.jsonclass.V_publicityunread;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.basic.GetUserPicture;
import com.jeeweel.syl.jwtask.business.main.module.task.OverTaskListActivity;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.config.publicjsonclass.ResMsgItem;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwListActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
import com.jeeweel.syl.lib.api.core.jwpublic.integer.IntUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.o.OUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.store.StoreUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.jeeweel.syl.lib.api.core.otto.ActivityMsgEvent;
import com.jeeweel.syl.lib.api.core.toast.JwToast;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.HttpHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import api.util.Contants;
import api.util.FileUtils;
import api.util.Utils;
import api.view.CustomDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DeptFileListActivity extends JwListActivity {
    List<Picture> mListItems = new ArrayList<Picture>();

    @Bind(R.id.listview)
    PullToRefreshListView listview;
    private CommonAdapter commonAdapter;

    private int pageStart = 0; //截取的开始
    private int pageEnd = 10; //截取的尾部
    private int addNum = 10;//下拉加载更多条数

    List<Picture> list;

    private Users users;
    private String dept_code;

    /**
     * 用于判断是从哪请求过来的
     */
    private String tag = "";
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        users = JwAppAplication.getInstance().users;
        dept_code = getIntent().getStringExtra(StaticStrUtils.baseItem);
        setTitle("文件列表");
        ButterKnife.bind(this);
        initListViewController();
    }

    /**
     * 字节转换为kb
     *
     * @param bt
     * @return kb大小
     */
    private int byteToKB(int bt) {
        return Math.round(bt / 1024);
    }

    /**
     * 下载文件
     */
    private void downloadApk(String sDownUrl,String filename) {
        String sApkPath = StoreUtils.getSDPath()  + filename;
        FinalHttp jwHttp = new FinalHttp();
        jwHttp.download(sDownUrl, sApkPath, new AjaxCallBack<File>() {

            @Override
            public int getRate() {
                return super.getRate();
            }

            @Override
            public AjaxCallBack<File> progress(boolean progress, int rate) {
                return super.progress(progress, rate);
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
                progress.setProgressNumberFormat("%1d k/%2d k");
                progress.setMax(byteToKB((int) count));
                progress.setProgress(byteToKB((int) current));
            }

            @Override
            public void onSuccess(File f) {

                progress.dismiss();
                String path = f.getAbsolutePath();
                commonAdapter.notifyDataSetChanged();
                File file = new File(path);
                FileUtils.openFile(file,DeptFileListActivity.this);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                JwToast.ToastShow("文件下载异常");
                progress.dismiss();
            }
        });


    }



    @Override
    public void initListViewController() {
        commonAdapter = new CommonAdapter<Picture>(getMy(), mListItems, R.layout.item_dept_file) {
            @Override
            public void convert(final ViewHolder helper, final Picture item) {
                String file = item.getPic_road();
                final String fileName = file.substring(file.lastIndexOf("_") + 1, file.length());
                helper.setText(R.id.tv_file, fileName);
               // helper.setText(R.id.tv_name, item.getNickname());
                helper.setText(R.id.tv_time, item.getCreate_time());
                final Button bt_load = helper.getView(R.id.bt_load);



                final String fliePath = StoreUtils.getSDPath()+ fileName;
                if(FileUtils.fileIsExists(fliePath)){
                    //已下载，显示打开，按钮可点击 1
                    bt_load.setText("打开");
                    bt_load.setTag("1");
                    bt_load.setClickable(true);
                    bt_load.setBackgroundResource(R.drawable.bg_dk);
                }else{
                    //还未下载，按钮显示下载,可点击
                    bt_load.setTag("0");
                    bt_load.setClickable(true);
                    bt_load.setBackgroundResource(R.drawable.bg_xz);
                }

                bt_load.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(bt_load.getTag().equals("0")){
                            //显示下载中，不可点击
                            bt_load.setText("下载中");
                            bt_load.setBackgroundResource(R.drawable.bg_xzz);
                            bt_load.setClickable(false);
                            //显示加载进度条
                            progress = new ProgressDialog(DeptFileListActivity.this);
                            progress.setCancelable(false);
                            progress.setTitle("正在下载...");
                            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progress.setIndeterminate(false);
                            progress.setProgress(0);
                            progress.show();

                            String path = Utils.getPicUrl()+item.getPic_road();
                            downloadApk(path,fileName);
                        }else if(bt_load.getTag().equals("1")){
                            File file = new File(fliePath);
                            FileUtils.openFile(file,DeptFileListActivity.this);
                        }
                    }
                });
            }
        };
        setCommonAdapter(commonAdapter);

        super.initListViewController();
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

            if (null != users) {
                try {
                    if (mode == 0) {
                        setPage(true);
                        String sql = "SELECT * from picture where pic_code = "+ StrUtils.QuotedStr(dept_code) +" ORDER BY create_time DESC  limit " + pageStart + "," + pageEnd;
                        list = jCloudDB.findAllBySql(Picture.class, sql);
                        mListItems.clear();
                    } else {
                        setPage(false);
                        String sql = "SELECT * from picture where pic_code = "+ StrUtils.QuotedStr(dept_code) +" ORDER BY create_time DESC  limit " + pageStart + "," + pageEnd;
                        list = jCloudDB.findAllBySql(Picture.class, sql);
                    }


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

    /**
     * 去除多余元素
     *
     * @param list
     */
    public void removeDuplicate(List<DeptFileItem> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getPic_road().equals(list.get(i).getPic_road())) {
                    list.remove(j);
                }
            }
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

    @Subscribe
    public void resultInfo(ActivityMsgEvent activityMsgEvent) {
        String msg = activityMsgEvent.getMsg();
        if (StrUtils.IsNotEmpty(msg) && msg.equals("friend_del_refresh")) {
            list.clear();
            showLoading();
            onListViewHeadRefresh();
        }
    }

}
