package com.emms.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.emms.ui.KProgressHUD;
import com.emms.ui.LoadingDialog;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2016/8/4.
 *
 */
public class BaseFragment extends Fragment{
    private KProgressHUD hud=null;
    private LoadingDialog loadingDialog=null;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    public KProgressHUD initCustomDialog(String label) {
        if(hud==null){
        hud=KProgressHUD.create(getActivity());}
        hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(label)
                .setCancellable(true);
        return  hud;
    }
    public void showCustomDialog(String label){
        if(Build.VERSION.SDK_INT>14) {
            initCustomDialog(label);
            if (hud != null && !hud.isShowing()) {
                hud.show();
            }
        }else {
            if(loadingDialog==null) {
                loadingDialog = new LoadingDialog(getActivity());
            }
            if(!loadingDialog.isShowing()){
                loadingDialog.show();
            }
        }
    }
    public void dismissCustomDialog(){
        try {
            if(Build.VERSION.SDK_INT>14) {
                if (isAdded()){
                    if (hud != null && hud.isShowing()) {
                        hud.dismiss();
                    }
                }

            }else {
                if (isAdded()){
                    if(loadingDialog!=null&&loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }

            }
        }catch (Exception e){
            CrashReport.postCatchedException(e);
        }

    }
}
