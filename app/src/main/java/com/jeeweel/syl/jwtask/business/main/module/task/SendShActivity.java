package com.jeeweel.syl.jwtask.business.main.module.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Submit;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Task;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Taskflow;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.photo.GetPicActivity;
import com.jeeweel.syl.jwtask.business.main.module.photo.PhotoActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.config.publicjsonclass.ResMsgItem;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.http.AjaxParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import api.photoview.Bimp;
import api.photoview.FileUtils;
import api.util.Contants;
import api.util.OttUtils;
import api.util.Utils;
import api.view.GridNoScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendShActivity extends JwActivity {

    Task task;
    @Bind(R.id.et_task_name)
    TextView etTaskName;
    @Bind(R.id.et_confirm_time)
    TextView etConfirmTime;
    @Bind(R.id.li_wcqk)
    EditText liWcqk;
    @Bind(R.id.et_yjfk)
    EditText etYjfk;
    @Bind(R.id.et_zwpj)
    TextView etZwpj;
    @Bind(R.id.li_zwpj)
    LinearLayout liZwpj;

    @Bind(R.id.noScrollgridview)
    GridNoScrollView noScrollgridview;

    Submit submit;
    @Bind(R.id.et_fj)
    TextView etFj;
    private AlertDialog dialog;

    private ScrollView li_fb;
    GridAdapter adapter;
    Activity context;

    Users users;

    int mode = 0;
    String fileNameFj = "";
    String url = "";
    String file_unid = "";
    ProgressDialog progressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sh);
        ButterKnife.bind(this);
        context = this;
        setTitle("递交审核");
        users = JwAppAplication.getInstance().getUsers();
        file_unid = Utils.getUUid();
        task = (Task) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        setData();
        initRight();
        initView();
    }

    private void setData() {
        if (null != task) {
            etTaskName.setText(StrUtils.IsNull(task.getTask_name()));
            etConfirmTime.setText(StrUtils.IsNull(task.getConfirm_time()));
        }
    }

    //开始时间
    @OnClick(R.id.li_fj)
    void fileUpClick() {
        showFileChooser();
    }

    /**
     * 调用文件选择软件来选择文件
     **/
    private void showFileChooser() {
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                    1991);
        } catch (ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getMy(), "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void HttpSuccess(ResMsgItem resMsgItem) {
        if (mode == 1) {
            progressDialog.dismiss();
            ToastShow("文件上传成功");
        } else {
            finish();
        }


    }

    @Override
    public void HttpFail(String strMsg) {
        super.HttpFail(strMsg);
    }

    @Override
    public void HttpFinish() {
        super.HttpFinish();
    }

    private void saveFile() {
        if (StrUtils.IsNotEmpty(url)) {

            fileNameFj = url.substring(url.lastIndexOf("/") + 1, url.length());
            AjaxParams params = new AjaxParams();
            try {
                params.put(file_unid, new File(url));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String apiStr = Utils.uploadPic();
            mode =1;
            progressDialog = ProgressDialog.show(SendShActivity.this, "上传", "正在努力上传中,请稍候！");
            progressDialog.setCancelable(false);
            JwHttpPost(apiStr, params, false);

        }
    }

    @OnClick(R.id.li_zwpj)
    void zwpjClick() {
        List<String> mListItems = new ArrayList<>();
        String[] data = getResources().getStringArray(R.array.zwpj_array);

        for (int i = 0; i < data.length; i++) {
            mListItems.add(data[i]);
        }
        showDialog(mListItems);
    }

    private void initRight() {
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("完成");
        menuTextView.setTextColor(getResources().getColor(R.color.back_blue));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                save();
            }
        });
        addMenuView(menuTextView);
    }

    private void save() {
        String wcqk = liWcqk.getText().toString();
        String yjfk = etYjfk.getText().toString();
        String zwpj = etZwpj.getText().toString();
        if (StrUtils.IsNotEmpty(wcqk)) {
            showLoading();

            submit = new Submit();
            submit.setTask_code(task.getTask_code());
            submit.setTask_name(task.getTask_name());
            submit.setPerformance(wcqk);
            submit.setFile_code(file_unid);
            submit.setFeedback(StrUtils.IsNull(yjfk));
            submit.setEvaluate(StrUtils.IsNull(zwpj));

            new FinishRefresh(getMy()).execute();
        } else {
            ToastShow("请输入完成情况");
        }
    }


    private void showDialog(List<String> mListItems) {

        dialog = new AlertDialog.Builder(getMy()).create();// 创建一个AlertDialog对象
        View view = context.getLayoutInflater().inflate(R.layout.item_task_dialog,
                null);// 自定义布局
        dialog.setView(view, 0, 0, 0, 0);// 把自定义的布局设置到dialog中，注意，布局设置一定要在show之前。从第二个参数分别填充内容与边框之间左、上、右、下、的像素
        dialog.show();// 一定要先show出来再设置dialog的参数，不然就不会改变dialog的大小了
        int width = context.getWindowManager().getDefaultDisplay().getWidth();// 得到当前显示设备的宽度，单位是像素
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();// 得到这个dialog界面的参数对象
        params.width = width - (width / 6);// 设置dialog的界面宽度
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;// 设置dialog高度为包裹内容
        params.gravity = Gravity.CENTER;// 设置dialog的重心
        dialog.getWindow().setAttributes(params);// 最后把这个参数对象设置进去，即与dialog绑定

        ListView listView = (ListView) view.findViewById(R.id.listview);

        final CommonAdapter commonAdapter = new CommonAdapter<String>(getMy(), mListItems, R.layout.item_friend_detail) {
            @Override
            public void convert(ViewHolder helper, String item) {
                helper.setText(R.id.tv_org_name, item);
            }
        };
        listView.setAdapter(commonAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String item = (String) commonAdapter.getItem(position);
                etZwpj.setText(item);
                dialog.cancel();
            }
        });
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

            if (null != submit) {
                try {
                    jCloudDB.save(submit);

                    if (null != users) {
                        String sql = "update task set now_state = 2 , now_state_name = '未审核' where task_code = " + StrUtils.QuotedStr(task.getTask_code()) + "and principal_code like " + StrUtils.QuotedStrLike(users.getUser_code());
                        CloudDB.execSQL(sql);
                    }


                    //保存到流程表里
                    Taskflow taskflow = new Taskflow();
                    taskflow.setUser_code(users.getUser_code());
                    taskflow.setNickname(users.getNickname());
                    taskflow.setTask_code(task.getTask_code());
                    taskflow.setNow_state(2);
                    taskflow.setNow_state_name(Contants.wsh);
                    taskflow.setUser_action(Contants.action_dj);
                    jCloudDB.save(taskflow);


                    //保存图片表
//                    for (String sFile : Bimp.drr) {
//                        // File file = new File(sFile);
//                        CloudFile.upload(sFile, task.getTask_code());
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
            uploadPic();
            ToastShow("审核成功");
            OttUtils.push("job_refresh", "");
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
                params.put(task.getTask_code(), file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //"http://121.199.8.223:8090/JCloud/servlet/CloudFileRest?appkey=58975c511b1bcaddecc906a2c9337665"
            String apiStr = Utils.uploadPic();
            JwHttpPost(apiStr, params);
        }

    }


    private void initView() {
        li_fb = (ScrollView) findViewById(R.id.li_fb);

        adapter = new GridAdapter(context);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case TAKE_PICTURE:
                    if (Bimp.drr.size() < 9 && resultCode == -1) {
                        Bimp.drr.add(path);
                    }
                    break;
                case 1991:
                    Uri uri = data.getData();
                    url = api.util.FileUtils.getImageAbsolutePath(SendShActivity.this, uri);
                    if (StrUtils.IsNotEmpty(url)) {

                        fileNameFj = url.substring(url.lastIndexOf("/") + 1, url.length());
                        String textName = etFj.getText().toString();
                        textName += "/" + fileNameFj;
                        textName = textName.substring(1, textName.length());
                        etFj.setText(textName);
                        saveFile();
                    }
                    break;
            }
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

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
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
