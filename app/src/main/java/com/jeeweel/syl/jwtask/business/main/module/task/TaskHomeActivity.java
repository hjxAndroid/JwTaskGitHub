package com.jeeweel.syl.jwtask.business.main.module.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.jeeweel.syl.jcloudlib.db.api.JCloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Friend;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Userorg;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.contact.FriendAddActivity;
import com.jeeweel.syl.jwtask.business.main.tab.TabHostActivity;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.CommonAdapter;
import com.jeeweel.syl.lib.api.component.adpter.comadpter.ViewHolder;
import com.jeeweel.syl.lib.api.component.viewcontroller.pull.PullToRefreshListView;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.list.ListUtils;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;
import com.jeeweel.syl.lib.api.core.jwutil.SharedPreferencesUtils;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.List;

import api.adapter.GridViewAdapter;
import api.util.Contants;
import api.util.Utils;
import api.view.LineGridView;
import api.viewpage.CBViewHolderCreator;
import api.viewpage.ConvenientBanner;
import api.viewpage.NetworkImageHolderView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class TaskHomeActivity extends JwActivity {


    @Bind(R.id.convenientBanner)
    ConvenientBanner convenientBanner;
    @Bind(R.id.li_img)
    LinearLayout liImg;
    @Bind(R.id.line_gv)
    LineGridView lineGv;
    private String[] data;

    private ArrayList<Integer> localImages = new ArrayList<Integer>();
    private List<String> networkImages;
    private String[] images;

    private AlertDialog dialog;

    List<Userorg> mListItems;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_home);
        setTitle(getString(R.string.任务));
        ButterKnife.bind(this);
        initView();
        initRight();
        getData();

    }

    private void initRight(){
        MenuTextView menuTextView = new MenuTextView(getMy());
        menuTextView.setText("切换");
        menuTextView.setTextColor(getResources().getColor(R.color.white));
        menuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showDialog();
            }
        });
        addMenuView(menuTextView);
    }


    private void initView() {
        self = this;

/*        WindowManager wm = getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();

        LinearLayout li_img = (LinearLayout) findViewById(R.id.li_img);
        ViewGroup.LayoutParams lp = li_img.getLayoutParams();
        int high = width * 10 / 16;
        lp.height = high;*/

        data = getResources().getStringArray(R.array.home_array);
        TypedArray imagesArrays = getResources().obtainTypedArray(
                R.array.home_image_array);
        GridViewAdapter gridViewAdapter = new GridViewAdapter(getMy(), data,
                imagesArrays, R.layout.item_home);
        lineGv.setAdapter(gridViewAdapter);

        lineGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        //公告
                        JwStartActivity(PublicyListActivity.class);
                        break;
                    case 1:
                        //签到
                        JwStartActivity(StartSignUpActivity.class);
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }

            }
        });
    }

    private void getData() {
        showLoading();
        new FinishRefresh(getMy()).execute();

        networkImages = new ArrayList<String>();
        networkImages.add("1");
        networkImages.add("2");
        initBanner();


    }

    private void initBanner() {
        // 网络加载例子
        convenientBanner
                .setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, networkImages) // 设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(
                        new int[]{R.drawable.ic_page_indicator,
                                R.drawable.ic_page_indicator_focused})
                        // 设置翻页的效果，不需要翻页效果可用不设
                .setPageTransformer(ConvenientBanner.Transformer.DefaultTransformer);
        convenientBanner.startTurning(2000);
    }

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        // 停止翻页
        convenientBanner.stopTurning();
    }


    private void showDialog() {

        dialog = new AlertDialog.Builder(getMy()).create();// 创建一个AlertDialog对象
        View view = TaskHomeActivity.this.getLayoutInflater().inflate(R.layout.item_task_dialog,
                null);// 自定义布局
        dialog.setView(view, 0, 0, 0, 0);// 把自定义的布局设置到dialog中，注意，布局设置一定要在show之前。从第二个参数分别填充内容与边框之间左、上、右、下、的像素
        dialog.show();// 一定要先show出来再设置dialog的参数，不然就不会改变dialog的大小了
        int width = TaskHomeActivity.this.getWindowManager().getDefaultDisplay().getWidth();// 得到当前显示设备的宽度，单位是像素
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();// 得到这个dialog界面的参数对象
        params.width = width - (width / 6);// 设置dialog的界面宽度
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;// 设置dialog高度为包裹内容
        params.gravity = Gravity.CENTER;// 设置dialog的重心
        // dialog.getWindow().setLayout(width-(width/6),
        // LayoutParams.WRAP_CONTENT);//用这个方法设置dialog大小也可以，但是这个方法不能设置重心之类的参数，推荐用Attributes设置
        dialog.getWindow().setAttributes(params);// 最后把这个参数对象设置进去，即与dialog绑定

        ListView listView = (ListView) view.findViewById(R.id.listview);

        final CommonAdapter commonAdapter = new CommonAdapter<Userorg>(getMy(), mListItems, R.layout.item_friend_detail) {
            @Override
            public void convert(ViewHolder helper, Userorg item) {
                helper.setText(R.id.tv_org_name, item.getOrg_name());
            }
        };
        listView.setAdapter(commonAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Userorg userorg = (Userorg)commonAdapter.getItem(position);
                SharedPreferencesUtils.save(getMy(), Contants.org_code,userorg.getOrg_code());
                SharedPreferencesUtils.save(getMy(), Contants.org_name,userorg.getOrg_name());
                dialog.cancel();
            }
        });
    }


    /**
     * 保存到数据库
     */
    private class FinishRefresh extends AsyncTask<String, Void, String> {
        private Context context;

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

            Users users = JwAppAplication.getInstance().getUsers();
            if(null!=users){
                try {
                    mListItems = jCloudDB.findAllByWhere(Userorg.class,
                            "user_name=" + StrUtils.QuotedStr(users.getUsername()));
                } catch (CloudServiceException e) {
                    result = "0";
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("1")){

            }else{
                ToastShow("组织获取出错");
            }
            hideLoading();
        }
    }
}
