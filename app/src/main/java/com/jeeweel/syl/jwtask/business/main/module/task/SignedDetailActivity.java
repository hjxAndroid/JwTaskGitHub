package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import api.util.Utils;
import api.view.GridNoScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_detail);
        ButterKnife.bind(this);
        setTitle("已签到详情");
        users = JwAppAplication.getInstance().getUsers();
        setData();
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


            tvSignRemark.setText(signed.getRemark());

           /* String wd = publicity.getUnread();
            if (StrUtils.IsNotEmpty(wd)) {
                int intWd = Integer.parseInt(wd);
                if (intWd < 0) {
                    intWd = 0;
                }
                tvWd.setText(intWd + "人未读");
            }
            String yd = publicity.getAlread();
            if (StrUtils.IsNotEmpty(yd)) {
                tvYd.setText(yd + "人已读");
            }*/
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
                if (ListUtils.IsNotNull(pictureList)) {
                    final String imgs[] = new String[pictureList.size()];
//                    CommonAdapter commonAdapter = new CommonAdapter<Picture>(getMy(), pictureList, R.layout.item_signed_detail_img) {
//                        @Override
//                        public void convert(ViewHolder helper, Picture item) {
//                            ImageView imageView = helper.getImageView(R.id.img);
//                            JwImageLoader.displayImage(Utils.getPicUrl() + item.getPic_road(), imageView);
//                        }
//                    };
//                    noScrollgridview.setAdapter(commonAdapter);
//                    noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            if (imgs.length != 0) {
//                                Intent intent = new Intent(getMy(), ImagePagerActivity.class);
//                                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
//                                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, imgs);
//                                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
//                                getMy().startActivity(intent);
//                            }
//                        }
//                    });
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