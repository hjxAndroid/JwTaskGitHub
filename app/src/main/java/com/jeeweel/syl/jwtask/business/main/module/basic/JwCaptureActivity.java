package com.jeeweel.syl.jwtask.business.main.module.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import com.jeeweel.syl.jwtask.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

public class JwCaptureActivity extends Activity {

    private CaptureManager capture;
    private CompoundBarcodeView barcodeScannerView;

    public JwCaptureActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_jw_capture);
        this.barcodeScannerView = (CompoundBarcodeView)this.findViewById(R.id.zxing_barcode_scanner);
        this.capture = new CaptureManager(this, this.barcodeScannerView);
        this.capture.initializeFromIntent(this.getIntent(), savedInstanceState);
        this.capture.decode();
    }

    protected void onResume() {
        super.onResume();
        this.capture.onResume();
    }

    protected void onPause() {
        super.onPause();
        this.capture.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.capture.onDestroy();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.capture.onSaveInstanceState(outState);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
