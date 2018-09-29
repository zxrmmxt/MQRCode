package com.xt.qrcodedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xt.m_common_utils.MRuntimePermissionUtil;
import com.xt.qrcode.QRCodeScanActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(MRuntimePermissionUtil.isPermissionsGranted(grantResults)){
            startActivity(new Intent(this, QRCodeScanActivity.class));
        }
    }

    public void scan(View view) {
        if (MRuntimePermissionUtil.requestPermissions(this, MRuntimePermissionUtil.PERMISSIONS_CAMERA, MRuntimePermissionUtil.RUNTIM_EPERMISSION_REQUEST_CODE_CAMERA)) {
            startActivity(new Intent(this, QRCodeScanActivity.class));
        }
    }
}
