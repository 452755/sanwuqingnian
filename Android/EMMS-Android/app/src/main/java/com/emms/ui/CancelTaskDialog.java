package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.emms.R;
import com.emms.util.LocaleUtils;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;

/**
 * Created by Administrator on 2016/8/26.
 *
 */
public class CancelTaskDialog extends Dialog{
    private Context context;
    private EditText cancelReason;
    private String Tips;
    private int TaskType=2;
    public CancelTaskDialog(Context context) {
        super(context, R.style.Base_Theme_AppCompat_Dialog);
        this.context = context;
        setContentView(R.layout.cancel_dialog);
       initview();
        Tips= LocaleUtils.getI18nValue("NoCancelReason");
        setCancelable(false);
    }
    public CancelTaskDialog(Context context,int type) {
        super(context, R.style.Base_Theme_AppCompat_Dialog);
        this.context = context;
        TaskType=type;
        setContentView(R.layout.cancel_dialog);
        initview();
        Tips= LocaleUtils.getI18nValue("NoCancelReason");
        setCancelable(false);
    }
    public void setDialogTitle(String title){
        ((TextView)findViewById(R.id.DialogTitle)).setText(title);
    }
    public void setTips(String tips){
        Tips=tips;
    }
    private void initview(){
        cancelReason=(EditText)findViewById(R.id.CancelReason);
        //cancelReason.setHint(LocaleUtils.getI18nValue("pleaseInputCancleTaskReason"));
        cancelReason.setHint("请输入原因");
        findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelReason.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
            }
        });
        Button cancel =  (Button)findViewById(R.id.cancel);
        cancel.setText(LocaleUtils.getI18nValue("cancel"));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button comfirm =  (Button)findViewById(R.id.comfirm);
        comfirm.setText(LocaleUtils.getI18nValue("sure"));
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cancelReason.getText().toString().equals("") && TaskType==2){
                    ToastUtil.showToastShort(Tips,context);
                    return;
                }
                taskCancelListener.submitCancel(cancelReason.getText().toString());
                dismiss();
            }
        });
    }

    public void setTaskCancelListener(TaskCancelListener taskCancelListener) {
        this.taskCancelListener = taskCancelListener;
    }

    public void setHint(String hint){
        cancelReason.setHint(hint);
    }

    private TaskCancelListener taskCancelListener;

}
