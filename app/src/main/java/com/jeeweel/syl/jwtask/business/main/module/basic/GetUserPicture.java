package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jcloudlib.db.utils.StrUtils;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;

import java.util.List;

import api.util.Utils;

/**
 * Created by Administrator on 2015/12/9.
 * 将数据库图片在ImageView上显示
 */
public class GetUserPicture extends AsyncTask<String, Void, String> {
        private Context context;
        private ImageView imageView;
        private String user_code;

        public GetUserPicture(Context context,ImageView imageView,String user_code) {
            this.context = context;
            this.imageView = imageView;
            this.user_code = user_code;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "0";
            List<Picture> list;
            try {
                JCloudDB jCloudDB=new JCloudDB();
                String sSql = "pic_code=?";
                SqlInfo sqlInfo = new SqlInfo();
                sqlInfo.setSql(sSql);
                sqlInfo.addValue(user_code);
                sSql = sqlInfo.getBuildSql();
                list = jCloudDB.findAllByWhere(Picture.class, sSql);
                if (ListUtils.IsNotNull(list)) {
                    Picture picture = list.get(0);
                    String path=picture.getPic_road();
                    String uri= Utils.getPicUrl()+path;
                    if (StrUtils.IsNotEmpty(path)){
                        result = uri;
                    }
                }
            }catch (CloudServiceException e){

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("0")){
     //           this.imageView.setVisibility(View.GONE);
            }else{
                JwImageLoader.displayImage(result,this.imageView);
            }
        }
    }

