package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userdept;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
import com.jeeweel.syl.lib.api.core.jwpublic.json.JwJSONUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import java.util.List;

import api.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;

public class PublicyUnReadListActivity extends JwActivity {

    List<Userorg> unReads;
    @Bind(R.id.listview)
    ListView listview;

    CommonAdapter commonAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicy_read_list);
        ButterKnife.bind(this);
        getData();
    }

    private void getData(){
        String json = getIntent().getStringExtra(StaticStrUtils.baseItem);

        if (StrUtils.IsNotEmpty(json)) {
            unReads = JwJSONUtils.getParseArray(json, Userorg.class);
            setTitle("未读"+unReads.size()+"人");
            //setTitle("未读人数");

            showLoading();
            new FinishRefresh(getMy()).execute();
        }

    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "0";

            if (null != unReads) {
                try {

                    if (ListUtils.IsNotNull(unReads)) {
                        result = "1";
                        for (Userorg userorg : unReads) {
                            //取头像
                            String friend_code = userorg.getUser_code();

                            String sSql = "pic_code=?";
                            SqlInfo sqlInfo = new SqlInfo();
                            sqlInfo.setSql(sSql);
                            sqlInfo.addValue(friend_code);
                            sSql = sqlInfo.getBuildSql();
                            List<Picture> pictureList = jCloudDB.findAllByWhere(Picture.class, sSql);
                            if (ListUtils.IsNotNull(pictureList)) {
                                Picture picture = pictureList.get(0);
                                String path = picture.getPic_road();
                                if (StrUtils.IsNotEmpty(path)) {
                                    //存头像
                                    userorg.setRemark(path);
                                }
                            }
                        }
                    } else {
                        result = "0";
                    }
                } catch (CloudServiceException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("1")) {
                commonAdapter = new CommonAdapter<Userorg>(getMy(), unReads, R.layout.item_friend) {
                    @Override
                    public void convert(ViewHolder helper, Userorg item) {
                        String nickname = item.getNickname();
                        helper.setText(R.id.tv_name, item.getUser_name());
                        helper.setText(R.id.tv_nick_name, nickname);

                        ImageView iv_photo = helper.getImageView(R.id.iv_xz);
                        JwImageLoader.displayImage(Utils.getPicUrl() + item.getRemark(), iv_photo);
                    }
                };
                listview.setAdapter(commonAdapter);
            } else {
                //没有加载到数据
            }

            hideLoading();
        }
    }
}
