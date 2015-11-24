package com.jeeweel.syl.jwtask.business.main.module.basic;

/**
 * Created by Administrator on 2015-08-10.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;

import java.util.ArrayList;


/**
 * @author 苏逸龙     317616660@qq.com
 * @ClassName: welcom
 * @Description: TODO(安装APP后首次打开 欢迎界面3张图片)
 * @date 2014-3-10 下午10:30:50
 */
public class WelcomActivity extends JwActivity {
    private ImageView mPage0, mPage1, mPage2;
    private ViewPager mViewPager;
    //private int IntImageIndex=0;

    public void startbutton(View v) {
        SharedPreferences welcomPreferences = getSharedPreferences("welcom",
                MODE_PRIVATE);
        Editor wcEdit = welcomPreferences.edit();
        wcEdit.putBoolean("IsPass", true);
        wcEdit.commit();
//        ImageLoaderUtil.getImageLoader().clearDiscCache();
        Intent intent = new Intent();
        intent.setClass(WelcomActivity.this, AppStartActivity.class);
        startActivity(intent);
        WelcomActivity.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        SharedPreferences welcomPreferences = getSharedPreferences("welcom",
                MODE_PRIVATE);
        boolean IsPass = false;
        IsPass = welcomPreferences.getBoolean("IsPass", false);
        if (IsPass) {
//            JwStartActivity(AppStartActivity.class);
            Intent intent = new Intent();
            intent.setClass(WelcomActivity.this, AppStartActivity.class);
            startActivity(intent);
            this.finish();
//            return;
        }
        setHideNavcationBar(true);
        setContentView(R.layout.activity_welcom);
        mViewPager = (ViewPager) findViewById(R.id.welcom_viewpager);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        mPage0 = (ImageView) findViewById(R.id.page0);
        mPage1 = (ImageView) findViewById(R.id.page1);
        mPage2 = (ImageView) findViewById(R.id.page2);

        LayoutInflater mLif = LayoutInflater.from(this);
        View view1 = mLif.inflate(R.layout.welimage0, null);
        View view2 = mLif.inflate(R.layout.welimage1, null);
        View view3 = mLif.inflate(R.layout.welimage2, null);

        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);

        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return views.size();

            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                // TODO Auto-generated method stub
                ((ViewPager) container).removeView(views.get(position));
            }

            @Override
            public Object instantiateItem(View container, int position) {
                // TODO Auto-generated method stub
                ((ViewPager) container).addView(views.get(position));
                return views.get(position);
            }
        };

        mViewPager.setAdapter(mPagerAdapter);

    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 0:
                    mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
                    mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    break;
                case 1:
                    mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
                    mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    break;
                case 2:
                    mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
                    break;
            }
        }
    }


}
