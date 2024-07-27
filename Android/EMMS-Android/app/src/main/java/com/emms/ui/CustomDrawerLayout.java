package com.emms.ui;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.View;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2016/7/20.
 */

public class CustomDrawerLayout extends DrawerLayout {

    public CustomDrawerLayout(Context context) {
        super(context);
    }

    public CustomDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
   @Override
    public void closeDrawer(View drawerView){
       super.closeDrawer(drawerView);
       try {
           closeDrawerListener.close();
       }catch (Exception e){
           CrashReport.postCatchedException(e);
       }

   }
    public void setCloseDrawerListener(CloseDrawerListener closeDrawerListener) {
        try {
            this.closeDrawerListener = closeDrawerListener;
        }catch (Exception e){
            CrashReport.postCatchedException(e);
        }

    }

    private CloseDrawerListener closeDrawerListener;

}
