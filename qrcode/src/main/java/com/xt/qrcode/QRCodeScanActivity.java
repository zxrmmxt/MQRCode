package com.xt.qrcode;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.IOException;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class QRCodeScanActivity extends AppCompatActivity implements QRCodeView.Delegate, View.OnClickListener {
    private static final String TAG = QRCodeScanActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private static final int OPEN_ALBUM = 11;

    private QRCodeView mQRCodeView;
    private ImageView btn_goToAlbum;
    private ImageView btn_switchLight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scan);
        initViews();
    }

    protected void initViews() {
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        btn_goToAlbum = (ImageView) findViewById(R.id.scan_btn_goToAlbum);
        btn_switchLight = (ImageView) findViewById(R.id.scan_btn_switchLight);
        btn_goToAlbum.setOnClickListener(this);
        btn_switchLight.setOnClickListener(this);
        closeLight();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mQRCodeView.stopCamera();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
        /*if (sMTask != null) {
            sMTask.cancel(true);
        }*/
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        vibrate();
        onScanSuccess(result);
//        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.start_spot:
//                mQRCodeView.startSpot();
//                break;
//            case R.id.stop_spot:
//                mQRCodeView.stopSpot();
//                break;
//            case R.id.start_spot_showrect:
//                mQRCodeView.startSpotAndShowRect();
//                break;
//            case R.id.stop_spot_hiddenrect:
//                mQRCodeView.stopSpotAndHiddenRect();
//                break;
//            case R.id.show_rect:
//                mQRCodeView.showScanRect();
//                break;
//            case R.id.hidden_rect:
//                mQRCodeView.hiddenScanRect();
//                break;
//            case R.id.start_preview:
//                mQRCodeView.startCamera();
//                break;
//            case R.id.stop_preview:
//                mQRCodeView.stopCamera();
//                break;
//            case R.id.open_flashlight:
//                mQRCodeView.openFlashlight();
//                break;
//            case R.id.close_flashlight:
//                mQRCodeView.closeFlashlight();
//                break;
//            case R.id.scan_barcode:
//                mQRCodeView.changeToScanBarcodeStyle();
//                break;
//            case R.id.scan_qrcode:
//                mQRCodeView.changeToScanQRCodeStyle();
//                break;
//            case R.id.choose_qrcde_from_gallery:
//                /*
//                从相册选取二维码图片，这里为了方便演示，使用的是
//                https://github.com/bingoogolapple/BGAPhotoPicker-Android
//                这个库来从图库中选择二维码图片，这个库不是必须的，你也可以通过自己的方式从图库中选择图片
//                 */
//                startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
//                break;
//        }
        if (v.getId() == R.id.scan_btn_goToAlbum) {/*
                从相册选取二维码图片，这里为了方便演示，使用的是
                https://github.com/bingoogolapple/BGAPhotoPicker-Android
                这个库来从图库中选择二维码图片，这个库不是必须的，你也可以通过自己的方式从图库中选择图片
                 */
            /*Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, OPEN_ALBUM);*/

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, OPEN_ALBUM);

        } else if (v.getId() == R.id.scan_btn_switchLight) {
            if (isOpen()) {
                closeLight();
            } else {
                openLight();
            }
        }
    }

    private void openLight() {
        btn_switchLight.setTag("open");
        btn_switchLight.setBackgroundResource(R.drawable.qrcode_scan_btn_flash_down2x);
        mQRCodeView.openFlashlight();
    }

    private void closeLight() {
        btn_switchLight.setTag("close");
        btn_switchLight.setBackgroundResource(R.drawable.qrcode_scan_btn_flash_nor2x);
        mQRCodeView.closeFlashlight();
    }

    private boolean isOpen() {
        String tag = (String) btn_switchLight.getTag();
        return TextUtils.equals(tag, "open");
    }

    private String getImagePath(Intent data) {
        if (null == data) {
            return null;
        }
        Uri selectedImage = data.getData();
        if (null == selectedImage) {
            return null;
        }
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePathColumns[0]);
        String imagePath = c.getString(columnIndex);
        c.close();
        return imagePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mQRCodeView.showScanRect();
        if (resultCode == Activity.RESULT_OK && requestCode == OPEN_ALBUM) {
            /**
             * 这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
             * 请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
             * */
            /*sMTask = new MTask(this);
            sMTask.execute(data);*/

            doSelectImageResult(data);
            /*new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    ContentResolver resolver = getContentResolver();
                    try {
                        Uri originalUri = data.getData();
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        return QRCodeDecoder.syncDecodeQRCode(photo);
                    } catch (IOException e) {
                        e.printStackTrace();
                        final String imagePath = getImagePath(data);
                        return QRCodeDecoder.syncDecodeQRCode(imagePath);
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    if (TextUtils.isEmpty(result)) {
                        Toast.makeText(QRCodeScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).open();
                    } else {
                        onScanSuccess(result);
                    }
                }
            }.execute();*/
        }
    }

    private void doSelectImageResult(final Intent data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver resolver = getContentResolver();
                String result;
                try {
                    Uri originalUri = data.getData();
                    Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                    /*Bitmap smallBitmap = QRCodeTool.zoomBitmap(photo, photo.getWidth() / 2, photo.getHeight() / 2);// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                    photo.recycle(); // 释放原始图片占用的内存，防止out of memory异常发生*/
                    result = QRCodeDecoder.syncDecodeQRCode(photo);
                    photo.recycle(); // 释放原始图片占用的内存，防止out of memory异常发生
                    if (TextUtils.isEmpty(result)) {
                        final String imagePath = getImagePath(data);
//                return QRCodeDecoder.syncDecodeQRCode(imagePath);
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                        result = QRCodeDecoder.syncDecodeQRCode(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    final String imagePath = getImagePath(data);
//                return QRCodeDecoder.syncDecodeQRCode(imagePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    result = QRCodeDecoder.syncDecodeQRCode(bitmap);
                }
                if (TextUtils.isEmpty(result)) {
                    doSelectImageResult1(data);
                } else {
                    final String finalResult = result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onScanSuccess(finalResult);
                        }
                    });
                }
            }
        }).start();
    }

    private void doSelectImageResult1(final Intent data) {
        ContentResolver resolver = getContentResolver();
        Result result = null;
        try {
            Uri originalUri = data.getData();
            Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
            result = QRCodeTool.decodeFromPhoto(photo);
            photo.recycle();
            if (result == null) {
                final String imagePath = getImagePath(data);
                result = QRCodeTool.decodeFromPhoto(BitmapFactory.decodeFile(imagePath));
            }
            if (result == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QRCodeScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                String text = result.getText();
                if (TextUtils.isEmpty(text)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QRCodeScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    final String finalResult = text;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onScanSuccess(finalResult);
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onScanSuccess(String result) {
        Intent data = new Intent();
        data.putExtra("result", result);
        setResult(RESULT_OK, data);
        finish();
    }

    public static String getResult(Intent data) {
        return data.getStringExtra("result");
    }
}