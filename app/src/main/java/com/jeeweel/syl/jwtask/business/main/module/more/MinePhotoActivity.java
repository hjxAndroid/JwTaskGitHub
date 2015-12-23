package com.jeeweel.syl.jwtask.business.main.module.more;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jeeweel.syl.jcloudlib.db.api.CloudFile;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.control.imageloader.JwImageLoader;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.store.StoreUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalDb;

import api.util.OttUtils;

public class MinePhotoActivity extends JwActivity implements OnClickListener {
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESIZE_REQUEST_CODE = 2;

    private static final String IMAGE_FILE_NAME = "header.jpg";

    private ImageView mImageHeader;
    Users users;
    String sFile;
    String user_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_photo);
        setTitle("设置头像");
        setupViews();
        initRight();

        users = JwAppAplication.getInstance().users;
        user_code = users.getUser_code();
        String pic_road=users.getPic_road();
        if(StrUtils.IsNotEmpty(pic_road)){
            JwImageLoader.displayImage(pic_road, mImageHeader);
        }
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sFile = getImagePath();
                new FinishRefresh(getMy()).execute();
                ToastShow("保存成功");
            }
        });
        addMenuView(menuTextView);
    }

    private void setupViews() {
        mImageHeader = (ImageView) findViewById(R.id.image_header);
        final Button selectBtn1 = (Button) findViewById(R.id.btn_selectimage);
        final Button selectBtn2 = (Button) findViewById(R.id.btn_takephoto);
        selectBtn1.setOnClickListener(this);
        selectBtn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_selectimage:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
                break;
            case R.id.btn_takephoto:
                if (isSdcardExisting()) {
                    Intent cameraIntent = new Intent(
                            "android.media.action.IMAGE_CAPTURE");
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
                    cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                } else {
                    Toast.makeText(v.getContext(), "请插入sd卡", Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    resizeImage(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (isSdcardExisting()) {
                        resizeImage(getImageUri());
                    } else {
                        Toast.makeText(MinePhotoActivity.this, "未找到存储卡，无法存储照片！",
                                Toast.LENGTH_LONG).show();
                    }
                    break;

                case RESIZE_REQUEST_CODE:
                    if (data != null) {
                        showResizeImage(data);
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isSdcardExisting() {
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void resizeImage(Uri uri) {//调整图片大小
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESIZE_REQUEST_CODE);
    }

    private void showResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            mImageHeader.setImageDrawable(drawable);
            try {
                saveFile(photo);
            } catch (IOException e) {

            }
        }
    }

    private Uri getImageUri() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                IMAGE_FILE_NAME));
    }

    private String getImagePath() {
        //   return Utils.getPicUrl() + StoreUtils.getSDPath() + IMAGE_FILE_NAME;
        return StoreUtils.getSDPath() + IMAGE_FILE_NAME;
    }

    /**
     * 保存文件
     *
     * @param bm
     * @throws IOException
     */
    public void saveFile(Bitmap bm) throws IOException {
        File myCaptureFile = new File(getImagePath());
        FileOutputStream bos = new FileOutputStream(myCaptureFile);
        bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
        bos.flush();
        bos.close();
    }

    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;
        List<Users> list;

        /**
         * @param context 上下文
         */
        public FinishRefresh(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";
            if (StrUtils.IsNotEmpty(user_code)) {
                try {
                    //保存图片表
                    String sSql = "pic_code=?";
                    SqlInfo sqlInfo = new SqlInfo();
                    sqlInfo.setSql(sSql);
                    sqlInfo.addValue(user_code);
                    sSql = sqlInfo.getBuildSql();
                    jCloudDB.deleteByWhere(Picture.class, sSql);
                    CloudFile.upload(sFile, user_code);
                    String uSql = "SELECT * FROM v_users_pic where user_code=?";
                    SqlInfo usqlInfo = new SqlInfo();
                    usqlInfo.setSql(uSql);
                    usqlInfo.addValue(user_code);
                    uSql = usqlInfo.getBuildSql();
                    list = jCloudDB.findAllBySql(Users.class, uSql);
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
                SharedPreferencesUtils.save(context, "autologin", true);

                if (ListUtils.IsNotNull(list)) {
                    Users users = list.get(0);
                    FinalDb finalDb = JwAppAplication.getInstance().finalDb;
                    finalDb.deleteAll(Users.class);
                    finalDb.save(users);
                    JwAppAplication.getInstance().setUsers(users);
                    MobclickAgent.onProfileSignIn(users.getUsername());
                    OttUtils.push("photo_refresh","");
                }
            } else {
                ToastShow("上传失败");
            }
            hideLoading();
            MinePhotoActivity.this.finish();
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
