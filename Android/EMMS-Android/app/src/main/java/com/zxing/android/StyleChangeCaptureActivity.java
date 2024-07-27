package com.zxing.android;

import android.content.Intent;
import android.graphics.Bitmap;

import com.emms.activity.CusActivity;
import com.emms.schema.Factory;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.google.zxing.Result;
import com.zxing.android.decoding.InactivityTimer;

import java.math.BigInteger;

/**
 * Created by Administrator on 2018-10-24.
 */

public class StyleChangeCaptureActivity extends CaptureActivity {

    private InactivityTimer inactivityTimer;

    @Override
    protected void showResult(final Result rawResult, Bitmap barcode) {
        inactivityTimer = new InactivityTimer(this);
        inactivityTimer.onActivity();
        Intent it = new Intent(StyleChangeCaptureActivity.this, StyleChangeCaptureActivity.class);
        it.putExtra("result", rawResult.getText());
        setResult(Constants.RESULT_CODE_CAPTURE_ACTIVITY_TO_TASK_DETAIL, it);
        finish();
    }
}
