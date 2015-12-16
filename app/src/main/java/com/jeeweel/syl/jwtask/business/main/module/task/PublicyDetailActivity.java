package com.jeeweel.syl.jwtask.business.main.module.task;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Publicity;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import java.util.ArrayList;
import java.util.List;

import api.util.Utils;
import api.viewpage.CBViewHolderCreator;
import api.viewpage.ConvenientBanner;
import api.viewpage.NetworkImageHolderView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class PublicyDetailActivity extends JwActivity {

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_org_name)
    TextView tvOrgName;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.convenientBanner)
    ConvenientBanner convenientBanner;

    private List<String> networkImages;
    Publicity publicity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicy_detail);
        ButterKnife.bind(this);
        setTitle("公告详情");
        setData();
    }

    private void setData() {
        publicity = (Publicity) getIntent().getSerializableExtra(StaticStrUtils.baseItem);
        if (null != publicity) {
            tvTitle.setText(publicity.getPublicity_title());
            tvName.setText(publicity.getNickname());
            tvTime.setText(publicity.getCreate_time());
            tvOrgName.setText(publicity.getAccept_org_name());
            tvContent.setText(publicity.getPublicity_content());

            networkImages = new ArrayList<String>();
            String path=publicity.getPictureListSting();
            if(path!=null){
                String[] st=path.split(",");
                for(int i=0;i<st.length;i++){
                    networkImages.add(Utils.getPicUrl() + st[i]);
                }
                initBanner();
            }else{
                LinearLayout li_img=(LinearLayout)findViewById(R.id.li_img);
                li_img.setVisibility(View.GONE);
            }

        }
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
