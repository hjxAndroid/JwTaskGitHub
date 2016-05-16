package com.jeeweel.syl.jwtask.business.main.module.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Signed;
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
import butterknife.OnClick;

/**
 * Created by Ragn on 2016/1/8.
 */
public class SignedDetailActivity extends JwActivity {

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_username)
    TextView tv_username;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_sign_remark)
    TextView tvSignRemark;
    @Bind(R.id.tv_signed_location)
    TextView tvSignedLocation;


    Users users;
    Signed signed;
    @Bind(R.id.noScrollgridview)
    GridNoScrollView noScrollgridview;
    @Bind(R.id.tv_signed_content)
    TextView tvSignedContent;
    @Bind(R.id.tv_sign_remark_title)
    TextView tvSignRemarkTitle;
    @Bind(R.id.tv_signed_state)
    TextView tvSignedState;
    @Bind(R.id.bt_login)
    Button btLogin;
    String flag = "";
    @Bind(R.id.lvfile)
    ListNoScrollView lvfile;

    List<Picture> fjList;
    CommonAdapter fileAdapter = null;
    ProgressDialog progress = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_detail);
        ButterKnife.bind(this);
        setTitle("已签到详情");
        flag = getIntent().getStringExtra("my");
        if (StrUtils.IsNotEmpty(flag)) {
            btLogin.setVisibility(View.GONE);
        }
        users = JwAppAplication.getInstance().getUsers();
        setData();
    }


    @OnClick(R.id.bt_login)
    void confirmClick() {
        showLoading();
        new saveState(getMy()).execute();
    }

    private void setData() {
        showLoading();
        signed = (Signed) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        if (null != signed) {
            if (StrUtils.IsNotEmpty(signed.getSign_title())) {
                tvTitle.setText(signed.getSign_title());
            } else {
                tvTitle.setText("未签到，未知");
            }
            if (StrUtils.IsNotEmpty(signed.getNickname())) {
                tv_username.setText(signed.getNickname());
            } else {
                tv_username.setText("未签到，未知");
            }
            if (StrUtils.IsNotEmpty(signed.getCreate_time().substring(0, 16))) {
                tvTime.setText(signed.getCreate_time().substring(0, 16));
            } else {
                tvTime.setText("未签到，未知");
            }
            if (StrUtils.IsNotEmpty(signed.getSign_msg())) {
                tvSignedContent.setText(signed.getSign_msg());
            } else {
                tvSignedContent.setText("未签到，未知");
            }
            if (signed.getConfirm_state().equals("0")) {
                tvSignedState.setText("未确认");
            } else {
                tvSignedState.setText("已确认");
            }

            tvSignRemark.setText(signed.getRemark());

            new FinishRefresh(getMy()).execute();
            hideLoading();
        }
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        List<Picture> pictureList;

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
                fjList = jCloudDB.findAllByWhere(Picture.class, "pic_code=" + StrUtils.QuotedStr(signed.getFile_code()));

                pictureList = jCloudDB.findAllByWhere(Picture.class, " pic_code = " + StrUtils.QuotedStr(signed.getUuid()));
            } catch (CloudServiceException e) {
                result = "0";
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {

                if (ListUtils.IsNotNull(fjList)) {

                    fileAdapter = new CommonAdapter<Picture>(getMy(), fjList, R.layout.item_file) {
                        @Override
                        public void convert(ViewHolder helper, final Picture item) {
                            helper.setText(R.id.tv_name, "签到附件"+helper.getPosition()+":");
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
                                        progress = new ProgressDialog(SignedDetailActivity.this);
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
                                        FileUtils.openFile(file,SignedDetailActivity.this);
                                    }
                                }
                            });
                        }
                    };
                    lvfile.setAdapter(fileAdapter);
                    lvfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Picture picture = (Picture) fileAdapter.getItem(position);
                            String file = picture.getPic_road();

                            if (com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils.IsNotEmpty(file)) {
                                showLoading();
                                String fileName = file.substring(file.lastIndexOf("_") + 1, file.length());
                                downloadApk(Utils.getPicUrl() + file, fileName);
                            }
                        }
                    });
                }


                if (ListUtils.IsNotNull(pictureList)) {
                    final String imgs[] = new String[pictureList.size()];
                    for (int i = 0; i < pictureList.size(); i++) {
                        imgs[i] = Utils.getPicUrl() + pictureList.get(i).getPic_road();
                    }

                    CommonAdapter commonAdapter1 = new CommonAdapter<Picture>(getMy(), pictureList, R.layout.item_signed_detail_img) {
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
                hideLoading();
            }
        }
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
                FileUtils.openFile(file,SignedDetailActivity.this);

            }


            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                JwToast.ToastShow("文件下载异常");
                progress.dismiss();
            }
        });


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


    private class saveState extends AsyncTask<String, Void, String> {
        private Context context;

        public saveState(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "1";
            String sql = "UPDATE signed SET confirm_state='" + 1 + "'WHERE sign_code ='" + signed.getSign_code() + "'";
            try {
                CloudDB.execSQL(sql);
            } catch (CloudServiceException e) {
                result = "0";
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.equals("1")) {
                tvSignedState.setText("已确认");
                ToastShow("确认成功");
            } else {
                ToastShow("数据保存失败");
            }
        }
    }
}