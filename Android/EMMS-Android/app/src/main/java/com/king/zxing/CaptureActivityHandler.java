package com.king.zxing;

/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.provider.Browser;

import com.emms.R;
import com.emms.activity.StyleChangeCheckInActivity;
import com.emms.activity.StyleChangeTaskDetailsActivity;
import com.emms.util.DataUtil;
import com.emms.util.LogUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.king.zxing.camera.CameraManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

    private static final String TAG = CaptureActivityHandler.class.getSimpleName();

    private final CaptureActivity activity;
    private DecodeThread decodeThread = null;
    private State state;
    private final CameraManager cameraManager;

    private final StyleChangeCheckInActivity styleChangeCheckInActivity;
    private final StyleChangeTaskDetailsActivity styleChangeTaskDetailsActivity;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    CaptureActivityHandler(CaptureActivity activity,
                           Collection<BarcodeFormat> decodeFormats,
                           Map<DecodeHintType,?> baseHints,
                           String characterSet,
                           CameraManager cameraManager) {
        this.activity = activity;
        styleChangeCheckInActivity = null;
        styleChangeTaskDetailsActivity = null;
        decodeThread = new DecodeThread(activity, decodeFormats, baseHints, characterSet,
                new ViewfinderResultPointCallback(activity.getViewfinderView()));
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    public CaptureActivityHandler(StyleChangeCheckInActivity activity,
                                  Collection<BarcodeFormat> decodeFormats,
                                  Map<DecodeHintType, ?> baseHints,
                                  String characterSet,
                                  CameraManager cameraManager) {
        this.styleChangeCheckInActivity = activity;
        this.activity = null;
        styleChangeTaskDetailsActivity = null;
        decodeThread = new DecodeThread(activity, decodeFormats, baseHints, characterSet,
                new ViewfinderResultPointCallback(activity.getViewfinderView()));
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    public CaptureActivityHandler(StyleChangeTaskDetailsActivity activity,
                                  Collection<BarcodeFormat> decodeFormats,
                                  Map<DecodeHintType, ?> baseHints,
                                  String characterSet,
                                  CameraManager cameraManager) {
        this.styleChangeTaskDetailsActivity = activity;
        this.activity = null;
        this.styleChangeCheckInActivity = null;
        decodeThread = new DecodeThread(activity, decodeFormats, baseHints, characterSet,
                new ViewfinderResultPointCallback(activity.getViewfinderView()));
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.restart_preview) {
            restartPreviewAndDecode();
        } else if (message.what == R.id.decode_succeeded) {
            state = State.SUCCESS;
            Bundle bundle = message.getData();
            Bitmap barcode = null;
            float scaleFactor = 1.0f;
            if (bundle != null) {
                byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                if (compressedBitmap != null) {
                    barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                    // Mutable copy:
                    barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
                }
                scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
            }
            if (activity!=null){
//                message.obj = DataUtil.replaceBlank(message.obj.toString());
                activity.handleDecode((Result) message.obj, barcode, scaleFactor);
            }

            if (styleChangeCheckInActivity != null){
                styleChangeCheckInActivity.handleDecode(((Result) message.obj), barcode, scaleFactor);
            }

            if (styleChangeTaskDetailsActivity != null){
                LogUtils.e("扫描结果---->"+(Result) message.obj+"----->"+barcode+"----->"+scaleFactor);
                styleChangeTaskDetailsActivity.handleDecode((Result) message.obj, barcode, scaleFactor);
            }

        } else if (message.what == R.id.decode_failed) {// We're decoding as fast as possible, so when one decode fails, start another.
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);

        } else if (message.what == R.id.return_scan_result) {
            if (activity!=null){
                activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
                activity.finish();
            }

            if (styleChangeTaskDetailsActivity != null){
                styleChangeTaskDetailsActivity.setResult(Activity.RESULT_OK, (Intent) message.obj);
                styleChangeTaskDetailsActivity.finish();
            }

            if (styleChangeCheckInActivity != null){
                styleChangeCheckInActivity.setResult(Activity.RESULT_OK, (Intent) message.obj);
                styleChangeCheckInActivity.finish();
            }


        } else if (message.what == R.id.launch_product_query) {
            String url = (String) message.obj;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intents.FLAG_NEW_DOC);
            intent.setData(Uri.parse(url));
            ResolveInfo resolveInfo = null;
            if (activity != null){
                 resolveInfo =
                        activity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

            }

            if (styleChangeCheckInActivity != null){
                resolveInfo =
                        styleChangeCheckInActivity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

            }

            if (styleChangeTaskDetailsActivity != null){
                resolveInfo =
                        styleChangeTaskDetailsActivity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

            }
            String browserPackageName = null;
            if (resolveInfo != null && resolveInfo.activityInfo != null) {
                browserPackageName = resolveInfo.activityInfo.packageName;
                Log.d(TAG, "Using browser in package " + browserPackageName);
            }

            // Needed for default Android browser / Chrome only apparently
            if (browserPackageName != null) {
                switch (browserPackageName) {
                    case "com.android.browser":
                    case "com.android.chrome":
                        intent.setPackage(browserPackageName);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, browserPackageName);
                        break;
                }
            }

            try {
                if (activity!=null){
                    activity.startActivity(intent);
                }

                if (styleChangeTaskDetailsActivity!=null){
                    styleChangeTaskDetailsActivity.startActivity(intent);
                }

                if (styleChangeCheckInActivity!=null){
                    styleChangeCheckInActivity.startActivity(intent);
                }

            } catch (ActivityNotFoundException ignored) {
                Log.w(TAG, "Can't find anything to handle VIEW of URI " + url);
            }

        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause() will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    public void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            if (activity!=null){
                activity.drawViewfinder();
            }

            if (styleChangeCheckInActivity!=null){
                styleChangeCheckInActivity.drawViewfinder();
            }

            if (styleChangeTaskDetailsActivity!=null){
                styleChangeTaskDetailsActivity.drawViewfinder();
            }

        }
    }



}