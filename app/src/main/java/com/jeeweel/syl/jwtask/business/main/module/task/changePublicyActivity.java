package com.jeeweel.syl.jwtask.business.main.module.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Toast;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.CloudFile;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jcloudlib.db.sqlite.SqlInfo;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Picture;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Publicity;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.config.jsonclass.V_publicityunread;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.photo.GetPicActivity;
import com.jeeweel.syl.jwtask.business.main.module.photo.PhotoActivity;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import net.tsz.afinal.http.AjaxParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import api.photoview.Bimp;
import api.photoview.FileUtils;
import api.util.OttUtils;
import api.util.Utils;
import api.view.GridNoScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ragn on 2015/12/24.
 */
public class ChangePublicyActivity extends JwActivity {
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.li)
    LinearLayout li;
    @Bind(R.id.et_content)
    EditText etContent;

    private List<Publicity> list;
    private V_publicityunread vPublicityunrea;
    private Publicity publicity;
    private String title;
    private String content;
    Users users;
    GridView noScrollgridview;
    private LinearLayout li_fb;
    GridAdapter adapter;
    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_publicy);
        ButterKnife.bind(this);
        context = this;
        users = JwAppAplication.getInstance().getUsers();
        getDate();
        initRight();
        initView();
    }


    private void initView() {
        li_fb = (LinearLayout) findViewById(R.id.li_fb);
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        adapter = new GridAdapter(ChangePublicyActivity.this);
        adapter.update1();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.bmp.size()) {
                    new PopupWindows(context, li_fb);
                } else {
                    Intent intent = new Intent(context,
                            PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成修改");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                title = etTitle.getText().toString();
                content = etContent.getText().toString();
                showLoading();
                new FinishRefreshChangeClick(getMy()).execute();
            }
        });
        addMenuView(menuTextView);
    }


    private void getDate() {
        vPublicityunrea = (V_publicityunread) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        new FinishRefresh(getMy()).execute();
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

            String result = "1";
            try {
                list = jCloudDB.findAllByWhere(Publicity.class, " publicity_code = " + "\'" + vPublicityunrea.getPublicity_code() + "\'" + " and prouser_code = " + "\'" + users.getUser_code() + "\'");
                if (ListUtils.IsNotNull(list)) {
                    result = "1";
                } else {
                    result = "0";
                }


            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if ("1".equals(result)) {
                publicity = list.get(0);
                etTitle.setText(publicity.getPublicity_title());
                etContent.setText(publicity.getPublicity_content());
            }
            hideLoading();
        }
    }

    /**
     * 保存到数据库
     */
    private class FinishRefreshChangeClick extends AsyncTask<String, Void, String> {
        private Context context;
        private JCloudDB jCloudDB;

        /**
         * @param context 上下文
         */
        public FinishRefreshChangeClick(Context context) {
            this.context = context;
            jCloudDB = new JCloudDB();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "1";
            SqlInfo sqlInfo = new SqlInfo();
            sqlInfo.setSql(" update publicity set publicity_title = ? , publicity_content = ? where publicity_code = ? and prouser_code = ?");
            sqlInfo.addValue(title);
            sqlInfo.addValue(content);
            sqlInfo.addValue(vPublicityunrea.getPublicity_code());
            sqlInfo.addValue(users.getUser_code());
            String sql = sqlInfo.getBuildSql();
            try {
                if (!(CloudDB.execSQL(sql))) {
                    result = "0";
                } else {
                    //删除原有图片
                    jCloudDB.deleteByWhere(Picture.class, " pic_code = " + StrUtils.QuotedStr(vPublicityunrea.getPublicity_code()));
//                    保存图片表
//                    for (String sFile : Bimp.drr) {
//                        CloudFile.upload(sFile, vPublicityunrea.getPublicity_code());
//                    }
                }
            } catch (CloudServiceException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            uploadPic();
            ToastShow("修改成功");
            finish();
            hideLoading();
        }
    }

    void uploadPic() {
        AjaxParams params = new AjaxParams();

        int i = 0;
        for (String sFile : Bimp.drr) {
            File file = new File(sFile);
            try {
                params.put(vPublicityunrea.getPublicity_code(), file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //"http://121.199.8.223:8090/JCloud/servlet/CloudFileRest?appkey=58975c511b1bcaddecc906a2c9337665"
            String apiStr = Utils.uploadPic();
            JwHttpPost(apiStr, params);
        }

    }


    //相册popwin
    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            super(mContext);

            View view = View
                    .inflate(mContext, R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(GridLayout.LayoutParams.FILL_PARENT);
            setHeight(GridLayout.LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    photo();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context,
                            GetPicActivity.class);
                    startActivity(intent);
                    dismiss();
                }
            });
            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater; // 视图容器
        private int selectedPosition = -1;// 选中的位置
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update1() {
            loading1();
        }

        public int getCount() {
            return (Bimp.bmp.size() + 1);
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        /**
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            //final int coord = position;
            ViewHolder holder = null;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.image.setVisibility(View.VISIBLE);

            if (position == Bimp.bmp.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));

            } else {
                holder.image.setImageBitmap(Bimp.bmp.get(position));
            }

            if (position == 9) {
                holder.image.setVisibility(View.GONE);
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading1() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.drr.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            try {
                                String path = Bimp.drr.get(Bimp.max);
                                System.out.println(path);
                                Bitmap bm = Bimp.revitionImageSize(path);
                                Bimp.bmp.add(bm);
                                String newStr = path.substring(
                                        path.lastIndexOf("/") + 1,
                                        path.lastIndexOf("."));
                                FileUtils.saveBitmap(bm, "" + newStr);
                                Bimp.max += 1;
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } catch (IOException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }


    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void photo() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStorageDirectory() + "/myimage/");
            if (!dir.exists()) dir.mkdirs();

            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(dir, String.valueOf(System.currentTimeMillis())
                    + ".jpg");
            path = file.getPath();
            Uri imageUri = Uri.fromFile(file);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        } else {
            Toast.makeText(context, "没有储存卡", Toast.LENGTH_LONG).show();
        }
    }

    protected void onRestart() {
        adapter.update1();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Bimp.bmp.clear();
        Bimp.drr.clear();
        Bimp.max = 0;
        super.onDestroy();
    }

}
