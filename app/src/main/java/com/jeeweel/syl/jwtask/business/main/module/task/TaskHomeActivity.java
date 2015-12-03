package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import java.util.ArrayList;
import java.util.List;

import api.adapter.GridViewAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_home);
        setTitle(getString(R.string.任务));
        ButterKnife.bind(this);
        initView();
        getData();
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
}
