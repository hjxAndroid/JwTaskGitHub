package com.jeeweel.syl.jwtask.business.main.module.more;

import com.jeeweel.syl.jcloudlib.db.api.CloudDB;
import com.jeeweel.syl.jcloudlib.db.exception.CloudServiceException;
import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.jwtask.business.config.jsonclass.Users;
import com.jeeweel.syl.jwtask.business.main.JwAppAplication;
import com.jeeweel.syl.jwtask.business.main.module.more.cascade.BaseActivity;
import com.jeeweel.syl.jwtask.business.main.module.more.cascade.widget.OnWheelChangedListener;
import com.jeeweel.syl.jwtask.business.main.module.more.cascade.widget.WheelView;
import com.jeeweel.syl.jwtask.business.main.module.more.cascade.widget.adapters.ArrayWheelAdapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MineAddressActivity extends BaseActivity implements OnClickListener, OnWheelChangedListener {
    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private Button mBtnConfirm;
    String str1;
    Users users;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_address);
        setUpViews();
        setUpListener();
        setUpData();
        setTitle("设置地区");
        users = JwAppAplication.getInstance().users;
        phone = users.getUsername();
    }

    private void setUpViews() {
        mViewProvince = (WheelView) findViewById(R.id.id_province);
        mViewCity = (WheelView) findViewById(R.id.id_city);
        mViewDistrict = (WheelView) findViewById(R.id.id_district);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
    }

    private void setUpListener() {
        // 添加change事件
        mViewProvince.addChangingListener(this);
        // 添加change事件
        mViewCity.addChangingListener(this);
        // 添加change事件
        mViewDistrict.addChangingListener(this);
        // 添加onclick事件
        mBtnConfirm.setOnClickListener(this);
    }

    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(MineAddressActivity.this, mProvinceDatas));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[] { "" };
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
        mViewDistrict.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[] { "" };
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                str1= mCurrentProviceName+","+mCurrentCityName+","+mCurrentDistrictName;
                new saveAddress(getMy()).execute();
//                showSelectedResult();
                break;
            default:
                break;
        }
    }

    private void showSelectedResult() {
        Toast.makeText(MineAddressActivity.this, "当前选中:"+mCurrentProviceName+","+mCurrentCityName+","
                +mCurrentDistrictName+","+mCurrentZipCode, Toast.LENGTH_SHORT).show();
    }

    private class saveAddress extends AsyncTask<String, Void, String> {
        private Context context;
        public saveAddress(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "1";
            users.setArea(str1);
            String sql="UPDATE users SET area='"+str1+"'WHERE username ='"+phone+"'";
            try{
                CloudDB.execSQL(sql);
            }catch (CloudServiceException e){
                result = "0";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if(result.equals("1")){
                JwAppAplication.getFinalDb().update(users);
            }else{
                ToastShow("数据保存失败");
            }
            MineAddressActivity.this.finish();
        }
    }
}
