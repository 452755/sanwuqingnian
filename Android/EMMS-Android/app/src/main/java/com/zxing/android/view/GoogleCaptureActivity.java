//package com.zxing.android.view;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.pm.PackageManager;
//import android.content.res.AssetFileDescriptor;
//import android.graphics.Bitmap;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Vibrator;
//import android.support.v4.app.ActivityCompat;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.util.SparseArray;
//import android.view.KeyEvent;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.widget.TextView;
//
//import com.emms.R;
//import com.emms.activity.BaseActivity;
//import com.emms.activity.CusActivity;
//import com.emms.schema.Factory;
//import com.emms.util.BaseData;
//import com.emms.util.Constants;
//import com.emms.util.DataUtil;
//import com.google.android.gms.vision.CameraSource;
//import com.google.android.gms.vision.Detector;
//import com.google.android.gms.vision.barcode.Barcode;
//import com.google.android.gms.vision.barcode.BarcodeDetector;
//import com.google.zxing.Result;
//import com.zxing.android.CaptureActivity;
//import com.zxing.android.MessageIDs;
//import com.zxing.android.camera.CameraManager;
//import com.zxing.android.decoding.CaptureActivityHandler;
//
//import java.io.IOException;
//import java.math.BigInteger;
//
//public class GoogleCaptureActivity extends BaseActivity {
//
//    private BarcodeDetector barcodeDetector;
//    private CameraSource cameraSource;
//    private SurfaceView cameraView;
//    private TextView codeInfo;
//    private boolean hasSurface;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_google_capture);
//        cameraView = (SurfaceView) findViewById(R.id.camera_view);
//        codeInfo = (TextView) findViewById(R.id.code_info);
//
//        barcodeDetector = new BarcodeDetector.Builder(this)
//                .setBarcodeFormats(Barcode.ALL_FORMATS)
//                .build();
//
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        int width = metrics.widthPixels;
//        int height = metrics.heightPixels;
//
//        cameraSource = new CameraSource.Builder(this, barcodeDetector)
//                .setFacing(CameraSource.CAMERA_FACING_BACK)
//                .setAutoFocusEnabled(true)
//                .setRequestedPreviewSize(width, height)
//                .build();
//
//        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                try {
//                    if (ActivityCompat.checkSelfPermission(GoogleCaptureActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        return;
//                    }
//                    cameraSource.start(cameraView.getHolder());
//                } catch (IOException ie) {
//                    Log.e("CAMERA SOURCE", ie.getMessage());
//                }
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                cameraSource.stop();
//            }
//        });
//
//        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
//            @Override
//            public void release() {
//            }
//
//            @Override
//            public void receiveDetections(Detector.Detections<Barcode> detections) {
//                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
//
//                if (barcodes.size() != 0) {
//                    showGoogleResult(barcodes.valueAt(0).displayValue);
//                }
//            }
//        });
//    }
//
//    protected void showGoogleResult(final String barCode) {
//        Intent it = new Intent(GoogleCaptureActivity.this, CusActivity.class);
//        if(BaseData.getConfigData().get(BaseData.TASK_GET_EQUIPMENT_DATA_FROM_ICCARD_ID)==null) {
//            switch (getLoginInfo().getFromFactory()) {
//                case Factory.FACTORY_GEW: {
//                    it.putExtra("result", barCode);
//                    break;
//                }
//                case Factory.FACTORY_EGM: {
//                    try {
//                        it.putExtra("result",new BigInteger(barCode,16).toString());
//                    } catch (Exception e) {
//                        it.putExtra("result", barCode);
//                    }
//                    break;
//                }
//                default: {
//                    it.putExtra("result", barCode);
//                    break;
//                }
//            }
//        }else {
//            switch (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.TASK_GET_EQUIPMENT_DATA_FROM_ICCARD_ID))){
//                case "1":{
//                    try {
//                        it.putExtra("result", new BigInteger(barCode,16).toString());
//                    } catch (Exception e) {
//                        it.putExtra("result", barCode);
//                    }
//                    break;
//                }
//                case "2":{
//                    it.putExtra("result", barCode);
//                    break;
//                }
//                default:{
//                    it.putExtra("result", barCode);
//                    break;
//                }
//            }
//        }
//        setResult(Constants.RESULT_CODE_CAPTURE_ACTIVITY_TO_TASK_DETAIL, it);
//        finish();
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            setResult(RESULT_CANCELED);
//            finish();
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        cameraSource.release();
//        barcodeDetector.release();
//    }
//}