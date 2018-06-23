package com.keredwell.scanandgo.ui.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;
import com.keredwell.scanandgo.ui.base.BaseActivity;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.keredwell.scanandgo.util.LogUtil.makeLogTag;

public class SimpleScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private static final String TAG = makeLogTag(SimpleScannerActivity.class);
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        Intent intent = new Intent();
        intent.putExtra("SCAN_RESULT", rawResult.getText());
        intent.putExtra("SCAN_RESULT_FORMAT", rawResult.getBarcodeFormat().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }
}
