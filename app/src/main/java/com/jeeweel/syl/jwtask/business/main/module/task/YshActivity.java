package com.jeeweel.syl.jwtask.business.main.module.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Submit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.imagedemo.image.ImagePagerActivity;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.store.StoreUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.toast.JwToast;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.util.List;

import api.util.FileUtils;
import api.util.Utils;
import api.view.GridNoScrollView;
import api.view.ListNoScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class YshActivity extends JwActivity {

    @Bind(R.id.et_task_name)
    TextView etTaskName;
    @Bind(R.id.et_confirm_time)
    TextView etConfirmTime;
    @Bind(R.id.tv_zwpj)
    TextView tvZwpj;
    @Bind(R.id.tv_wcqk)
    TextView tvWcqk;
    @Bind(R.id.tv_yjfk)
    TextView tvYjfk;
    @Bind(R.id.noScrollgridview)
    GridNoScrollView noScrollgridview;
    @Bind(R.id.et_shjl)
    TextView etShjl;
    @Bind(R.id.li_shpj)
    LinearLayout liShpj;
    @Bind(R.id.tv_shpj)
    TextView tvShpj;
    @Bind(R.id.li_fb)
    ScrollView liFb;

    Task task;
    Submit submit;

    Activity context;

    Users users;
    @Bind(R.id.listview)
    ListNoScrollView listview;

    CommonAdapter commonAdapter;
    @Bind(R.id.tv_score)
    TextView tvScore;
    @Bind(R.id.tv_rwnd)
    TextView tvRwnd;

    String flag = "";
    @Bind(R.id.lvfile)
    ListNoScrollView lvfile;
    List<Picture> fjList;
    ProgressDialog progress;
    CommonAdapter fileAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ysh);
        ButterKnife.bind(this);
        context = this;
        setTitle("审核详情");
        users = JwAppAplication.getInstance().getUsers();
        getData();
    }


    private void getData() {
        showLoading();
        flag = getIntent().getStringExtra("flag");
        task = (Task) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        showLoading();
        new FinishRefresh(getMy()).execute();
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
    private void downloadApk(String sDownUrl, String filename) {
        String sApkPath = StoreUtils.getSDPath() + filename;
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
                fileAdapter.notifyDataSetChanged();
                File file = new File(path);
                FileUtils.openFile(file,YshActivity.this);

            }


            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                JwToast.ToastShow("文件下载异常");
                progress.dismiss();
            }
        });


    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        List<Submit> list;
        List<Picture> pictureList;
        List<Taskflow> taskflows;

        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";

            JCloudDB jCloudDB = new JCloudDB();
            try {
                if (null != task) {
                    list = jCloudDB.findAllByWhere(Submit.class,
                            "task_code=" + StrUtils.QuotedStr(task.getTask_code()));

                    if (ListUtils.IsNotNull(list)) {
                        fjList = jCloudDB.findAllByWhere(Picture.class, "pic_code=" + StrUtils.QuotedStr(list.get(0).getFile_code()));
                    }

                    pictureList = jCloudDB.findAllByWhere(Picture.class,
                            "pic_code=" + StrUtils.QuotedStr(task.getTask_code()));

                    String newSql = "select * from  v_taskflow where task_code= " + StrUtils.QuotedStr(task.getTask_code()) + "ORDER BY create_time DESC";
                    //查找数据
                    taskflows = jCloudDB.findAllBySql(Taskflow.class, newSql);
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
                if (ListUtils.IsNotNull(list)) {
                    submit = list.get(0);
                    etTaskName.setText(StrUtils.IsNull(submit.getTask_name()));
                    tvRwnd.setText(StrUtils.IsNull(task.getDegree()));
                    etConfirmTime.setText(StrUtils.IsNull(task.getConfirm_time()));
                    tvWcqk.setText(StrUtils.IsNull(submit.getPerformance()));
                    tvYjfk.setText(StrUtils.IsNull(submit.getFeedback()));
                    tvZwpj.setText(StrUtils.IsNull(submit.getEvaluate()));
                    etShjl.setText(StrUtils.IsNull(submit.getAudit_evaluate()));
                    tvShpj.setText(StrUtils.IsNull(submit.getAudit_content()));
                    tvScore.setText(StrUtils.IsNull(submit.getScore()));
                }

                if (ListUtils.IsNotNull(fjList)) {

                    fileAdapter = new CommonAdapter<Picture>(getMy(), fjList, R.layout.item_file) {
                        @Override
                        public void convert(ViewHolder helper, final Picture item) {
                            helper.setText(R.id.tv_name, "任务附件"+helper.getPosition()+":");
                            String file = item.getPic_road();
                            final String fileName = file.substring(file.lastIndexOf("_") + 1, file.length());
                            helper.setText(R.id.tv_fj, fileName);
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
                                        progress = new ProgressDialog(YshActivity.this);
                                        progress.setTitle("正在下载...");
                                        progress.setCancelable(false);
                                        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        progress.setIndeterminate(false);
                                        progress.setProgress(0);
                                        progress.show();

                                        String path = Utils.getPicUrl()+item.getPic_road();
                                        downloadApk(path,fileName);
                                    }else if(bt_load.getTag().equals("1")){
                                        File file = new File(fliePath);
                                        FileUtils.openFile(file,YshActivity.this);
                                    }
                                }
                            });
                        }
                    };
                    lvfile.setAdapter(fileAdapter);
                }

                if (ListUtils.IsNotNull(pictureList)) {
                    final String imgs[] = new String[pictureList.size()];
                    for (int i = 0; i < pictureList.size(); i++) {
                        imgs[i] = Utils.getPicUrl() + pictureList.get(i).getPic_road();
                    }

                    CommonAdapter commonAdapter1 = new CommonAdapter<Picture>(getMy(), pictureList, R.layout.item_img) {
                        @Override
                        public void convert(ViewHolder helper, Picture item) {
                            ImageView imageView = helper.getImageView(R.id.img);
                            JwImageLoader.displayImage(Utils.getPicUrl() + item.getPic_road(), imageView);
                        }
                    };
                    noScrollgridview.setAdapter(commonAdapter1);

                    noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (imgs.length != 0) {
                                Intent intent = new Intent(getMy(), ImagePagerActivity.class);
                                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, imgs);
                                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                                getMy().startActivity(intent);
                            }
                        }
                    });
                }

                if (ListUtils.IsNotNull(taskflows)) {
                    commonAdapter = new CommonAdapter<Taskflow>(getMy(), taskflows, R.layout.item_task_detail) {
                        @Override
                        public void convert(ViewHolder helper, Taskflow item) {
                            helper.setText(R.id.tv_nickname, item.getNickname());
                            helper.setText(R.id.tv_action, item.getUser_action());
                            helper.setText(R.id.tv_time, item.getCreate_time());

                            ImageView imageView = helper.getImageView(R.id.iv_xz);
                            JwImageLoader.displayImage(Utils.getPicUrl() + item.getPic_road(), imageView);
                        }
                    };
                    listview.setAdapter(commonAdapter);
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Taskflow taskflow = (Taskflow) commonAdapter.getItem(position);
                            int state = taskflow.getNow_state();
                            switch (state) {
                                case 2:
                                    //已递交，未审核，查看自己提交的完成情况信息
                                    JwStartActivity(MyJobDetailActivity.class, task);
                                    break;
                                case 3:
                                    //已审核，查看审核情况
                                    if (flag == null) {
                                    } else {
                                        JwStartActivity(YshActivity.class, taskflow.getTask_code());
                                    }
                                    break;
                                case 4:
                                    //延期申请中，查看自己的延期信息
                                    JwStartActivity(SolveDelayActivity.class, taskflow.getTask_code());
                                    break;
                                case 7:
                                    //放弃申请中，查看自己的放弃信息
                                    Intent intent = new Intent(getMy(), SolveGiveUpActivity.class);
                                    intent.putExtra("flag", "1");
                                    intent.putExtra(StaticStrUtils.baseItem, task);
                                    startActivity(intent);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }
            } else {

            }
            hideLoading();
        }
    }

    @Override
    public void HttpFinish() {
        finish();
        super.HttpFinish();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}
