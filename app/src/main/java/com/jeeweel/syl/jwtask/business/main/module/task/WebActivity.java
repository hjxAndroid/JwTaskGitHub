package com.jeeweel.syl.jwtask.business.main.module.task;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.jeeweel.syl.jwtask.R;
import com.jeeweel.syl.lib.api.config.StaticStrUtils;
import com.jeeweel.syl.lib.api.core.activity.baseactivity.JwActivity;
import com.jeeweel.syl.lib.api.core.jwpublic.string.StrUtils;

import butterknife.Bind;
import butterknife.ButterKnife;


public class WebActivity extends JwActivity {
    @Bind(R.id.webView)
    WebView webView;

    private int mode = 1;

    private String id;
    private String friend_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideNavcationBar(true);
        setContentView(R.layout.activity_web);
        friend_code=getIntent().getStringExtra(StaticStrUtils.baseItem);
        ButterKnife.bind(this);

            setTitle("绩效统计");

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
//                ProgressDialog prDialog;


            @Override
            public void onPageStarted(WebView view, String url,
                                      Bitmap favicon) {
                // TODO Auto-generated method stub
                showLoading(getString(com.jeeweel.syl.lib.R.string.jing_cai_nei_rong_ji_jiang_zhan_xian));
//                    prDialog = ProgressDialog.show(UrlWebViewActivity.this, null,
//                            "精彩内容即将展现,请您稍等...");
//                    prDialog.setCancelable(true);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                hideLoading();
                super.onPageFinished(view, url);
            }
        });
        if(StrUtils.IsNotEmpty(friend_code)){
            webView.loadUrl("http://192.168.0.14:8081/mission/mycomrades.php?usercode="+friend_code);
        }


    }

    // 浏览网页历史记录
    // goBack()和goForward()
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    public void btnGoBackClick(View view) {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        this.finish();
    }

    public void btnBackClick(View view) {
        this.finish();
    }


}
