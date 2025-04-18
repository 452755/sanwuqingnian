package com.king.zxing;
/*
 * Copyright (C) 2010 ZXing authors
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


import android.content.Context;
import android.graphics.Bitmap;

import com.emms.R;
import com.emms.activity.StyleChangeCheckInActivity;
import com.emms.activity.StyleChangeTaskDetailsActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;

final class DecodeHandler extends Handler {

    private static final String TAG = DecodeHandler.class.getSimpleName();

    private final CaptureActivity activity;
    private final StyleChangeTaskDetailsActivity styleChangeTaskDetailsActivity;
    private final StyleChangeCheckInActivity styleChangeCheckInActivity;
//    private final MultiFormatReader multiFormatReader;
    private boolean running = true;
    private final QRCodeReader multiFormatReader;

    DecodeHandler(CaptureActivity activity, Map<DecodeHintType,Object> hints) {
//        multiFormatReader = new MultiFormatReader();
//        multiFormatReader.setHints(hints);
        multiFormatReader = new QRCodeReader();
        hints.put(DecodeHintType.CHARACTER_SET,"utf-8");
        hints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS,BarcodeFormat.QR_CODE);
        this.activity = activity;
        this.styleChangeCheckInActivity = null;
        this.styleChangeTaskDetailsActivity = null;
    }

    DecodeHandler(StyleChangeCheckInActivity activity, Map<DecodeHintType,Object> hints) {
        //        multiFormatReader = new MultiFormatReader();
//        multiFormatReader.setHints(hints);
        multiFormatReader = new QRCodeReader();
        hints.put(DecodeHintType.CHARACTER_SET,"utf-8");
        hints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS,BarcodeFormat.QR_CODE);
        this.styleChangeCheckInActivity = activity;
        this.styleChangeTaskDetailsActivity = null;
        this.activity = null;
    }

    DecodeHandler(StyleChangeTaskDetailsActivity activity, Map<DecodeHintType,Object> hints) {
        //        multiFormatReader = new MultiFormatReader();
//        multiFormatReader.setHints(hints);
        multiFormatReader = new QRCodeReader();
        hints.put(DecodeHintType.CHARACTER_SET,"utf-8");
        hints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS,BarcodeFormat.QR_CODE);
        this.styleChangeTaskDetailsActivity = activity;
        this.styleChangeCheckInActivity = null;
        this.activity = null;
    }

    @Override
    public void handleMessage(Message message) {
        if (message == null || !running) {
            return;
        }
        if (message.what == R.id.decode) {
            decode((byte[]) message.obj, message.arg1, message.arg2,isScreenPortrait());

        } else if (message.what == R.id.quit) {
            running = false;
            Looper.myLooper().quit();

        }
    }

    private boolean isScreenPortrait(){
        WindowManager manager = null;
        if (activity != null){
            manager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        }

        if (styleChangeTaskDetailsActivity != null){
            manager = (WindowManager) styleChangeTaskDetailsActivity.getSystemService(Context.WINDOW_SERVICE);
        }

        if (styleChangeCheckInActivity != null){
            manager = (WindowManager) styleChangeCheckInActivity.getSystemService(Context.WINDOW_SERVICE);
        }

        Display display = manager.getDefaultDisplay();
        Point screenResolution = new Point();
        display.getSize(screenResolution);
        return screenResolution.x < screenResolution.y;
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height,boolean isScreenPortrait) {
        long start = System.currentTimeMillis();
        Result rawResult = null;
        PlanarYUVLuminanceSource source = null;
        if(isScreenPortrait){
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++)
                    rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
            int tmp = width;
            width = height;
            height = tmp;
            if (activity!=null){
                source = activity.getCameraManager().buildLuminanceSource(rotatedData, width, height);
            }

            if (styleChangeCheckInActivity!=null){
                source = styleChangeCheckInActivity.getCameraManager().buildLuminanceSource(rotatedData, width, height);
            }

            if (styleChangeTaskDetailsActivity!=null){
                source = styleChangeTaskDetailsActivity.getCameraManager().buildLuminanceSource(rotatedData, width, height);
            }

        }else{
            if (activity!=null){
                source = activity.getCameraManager().buildLuminanceSource(data, width, height);
            }

            if (styleChangeTaskDetailsActivity!=null){
                source = styleChangeTaskDetailsActivity.getCameraManager().buildLuminanceSource(data, width, height);
            }

            if (styleChangeCheckInActivity!=null){
                source = styleChangeCheckInActivity.getCameraManager().buildLuminanceSource(data, width, height);
            }

        }
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
//                rawResult = multiFormatReader.decodeWithState(bitmap);
                rawResult = multiFormatReader.decode(bitmap);
            } catch (Exception e) {
                BinaryBitmap bitmap1 = new BinaryBitmap(new GlobalHistogramBinarizer(source));
                try {
//                    rawResult = multiFormatReader.decode(bitmap1);
                    rawResult = multiFormatReader.decode(bitmap1);
                } catch (Exception ne) {

                }
            } finally {
                multiFormatReader.reset();
            }
        }
        Handler handler = null;

        if (activity!=null){
            handler = activity.getHandler();
        }

        if (styleChangeCheckInActivity!=null){
            handler = styleChangeCheckInActivity.getHandler();
        }

        if (styleChangeTaskDetailsActivity!=null){
            handler = styleChangeTaskDetailsActivity.getHandler();
        }

        if (rawResult != null) {
            // Don't log the barcode contents for security.
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode in " + (end - start) + " ms");
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, rawResult);
                Bundle bundle = new Bundle();
                bundleThumbnail(source, bundle);
                message.setData(bundle);
                message.sendToTarget();
            }
        } else {
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_failed);
                message.sendToTarget();
            }
        }
    }

    private static void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
        bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width / source.getWidth());
    }

}