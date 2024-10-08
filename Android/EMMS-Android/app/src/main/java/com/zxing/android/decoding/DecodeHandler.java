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

package com.zxing.android.decoding;

import java.util.Hashtable;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.emms.activity.StyleChangeCheckInActivity;
import com.emms.activity.StyleChangeTaskDetailsActivity;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.zxing.android.CaptureActivity;
import com.zxing.android.MessageIDs;
import com.zxing.android.camera.PlanarYUVLuminanceSource;

final class DecodeHandler extends Handler {

	private static final String TAG = DecodeHandler.class.getSimpleName();

	private final CaptureActivity activity;
	private final MultiFormatReader multiFormatReader;
//	private final QRCodeReader multiFormatReader;

	private final StyleChangeCheckInActivity styleChangeCheckInActivity;

	private final StyleChangeTaskDetailsActivity styleChangeTaskDetailsActivity;

	DecodeHandler(CaptureActivity activity, Map<DecodeHintType, Object> hints) {
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
		this.activity = activity;
		this.styleChangeCheckInActivity = null;
		this.styleChangeTaskDetailsActivity = null;
	}

	DecodeHandler(StyleChangeCheckInActivity activity, Map<DecodeHintType, Object> hints) {
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
		this.styleChangeCheckInActivity = activity;
		this.activity = null;
		this.styleChangeTaskDetailsActivity = null;
	}

	DecodeHandler(StyleChangeTaskDetailsActivity activity, Map<DecodeHintType, Object> hints) {
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
		this.styleChangeTaskDetailsActivity = activity;
		this.activity = null;
		this.styleChangeCheckInActivity = null;
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case MessageIDs.decode:
			// Log.d(TAG, "Got decode message");
			decode((byte[]) message.obj, message.arg1, message.arg2);
			break;
		case MessageIDs.quit:
			Looper.myLooper().quit();
			break;
		}
	}

	/**
	 * Decode the data within the viewfinder rectangle, and time how long it
	 * took. For efficiency, reuse the same reader objects from one decode to
	 * the next.
	 * 
	 * @param data
	 *            The YUV preview frame.
	 * @param width
	 *            The width of the preview frame.
	 * @param height
	 *            The height of the preview frame.
	 */
	private void decode(byte[] data, int width, int height) {
		long start = System.currentTimeMillis();
		Result rawResult = null;

		/** 竖屏显示开始 **/
		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				rotatedData[x * height + height - y - 1] = data[x + y * width];
		}
		int tmp = width; // Here we are swapping, that's the difference to #11
		width = height;
		height = tmp;
		data = rotatedData;
		PlanarYUVLuminanceSource source = null;
		if (activity!=null){
			/** 竖屏显示结束 **/
//			source = activity.getCameraManager().buildLuminanceSource(data, width, height);
			source = activity.getCameraManager().buildLuminanceSource(rotatedData, width, height);
		}

		if (styleChangeCheckInActivity!=null){
			/** 竖屏显示结束 **/
//			 source = styleChangeCheckInActivity.getCameraManager().buildLuminanceSource(data, width, height);
//			source = styleChangeCheckInActivity.getCameraManager().buildLuminanceSource(rotatedData, width, height);

		}

		if (styleChangeTaskDetailsActivity!=null){
			/** 竖屏显示结束 **/
//			source = styleChangeTaskDetailsActivity.getCameraManager().buildLuminanceSource(data, width, height);
//			source = styleChangeTaskDetailsActivity.getCameraManager().buildLuminanceSource(rotatedData, width, height);
		}

		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		try {
			rawResult = multiFormatReader.decodeWithState(bitmap);
		} catch (ReaderException re) {
			// continue
		} finally {
			multiFormatReader.reset();
		}

		if (rawResult != null) {
			long end = System.currentTimeMillis();
			Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
			Message message = null;
			if (activity!=null){
				 message = Message.obtain(activity.getHandler(), MessageIDs.decode_succeeded, rawResult);
			}

			if (styleChangeCheckInActivity!=null){
				 message = Message.obtain(styleChangeCheckInActivity.getHandler(), MessageIDs.decode_succeeded, rawResult);
			}

			if (styleChangeTaskDetailsActivity!=null){
				message = Message.obtain(styleChangeTaskDetailsActivity.getHandler(), MessageIDs.decode_succeeded, rawResult);
			}

			Bundle bundle = new Bundle();
			bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
			message.setData(bundle);
			// Log.d(TAG, "Sending decode succeeded message...");
			message.sendToTarget();
		} else {
			Message message = null;
			if (activity!=null){
				message = Message.obtain(activity.getHandler(), MessageIDs.decode_failed);
			}

			if (styleChangeCheckInActivity!=null){
				message = Message.obtain(styleChangeCheckInActivity.getHandler(), MessageIDs.decode_failed);
			}

			if (styleChangeTaskDetailsActivity!=null){
				message = Message.obtain(styleChangeTaskDetailsActivity.getHandler(), MessageIDs.decode_failed);
			}

			message.sendToTarget();
		}
	}

}
