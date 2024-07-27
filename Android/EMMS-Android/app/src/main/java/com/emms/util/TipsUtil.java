package com.emms.util;

import android.app.Activity;
import android.content.Context;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.tencent.bugly.crashreport.CrashReport;


/**
 * Created by Administrator on 2016/10/28.
 * 
 */
public class TipsUtil {
    public static void ShowTips(final Context context, final String tips){
        /*if(LocaleUtils.getLanguage(context)!=null&&LocaleUtils.getLanguage(context)== LocaleUtils.SupportedLanguage.ENGLISH
                || LocaleUtils.SupportedLanguage.getSupportedLanguage(context.getResources().getConfiguration().locale.getLanguage())==LocaleUtils.SupportedLanguage.ENGLISH){
            DataUtil.getDataFromLanguageTranslation(context, tips, new StoreCallback() {
                @Override
                public void success(final DataElement element, String resource) {
                    try {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
                                    ToastUtil.showToastLong(DataUtil.isDataElementNull(element.asArrayElement().get(0).asObjectElement().get("Translation_Display")),context);
                                }else {
                                    ToastUtil.showToastLong(tips,context);
                                }
                            }
                        });
                    }catch (Exception e){
                        CrashReport.postCatchedException(e);
                    }
                }

                @Override
                public void failure(DatastoreException ex, String resource) {
                    try {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastLong(tips,context);
                            }
                        });
                    }catch (Exception e){
                        CrashReport.postCatchedException(e);
                    }

                }
            });
        }else {
            try {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastLong(tips,context);
                    }
                });
            }catch (Exception e){
                CrashReport.postCatchedException(e);
            }
        }*/
        try {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtils.e("获取到对应的tips---->"+tips);
                    int lenth = tips.length();
                    int index = tips.indexOf("|");
                    LogUtils.e("index--->"+index);
                    String tag = tips.substring(0,index);//标签名
                    LogUtils.e("tag---->"+tag);
                    String data = tips.substring(index+1,lenth);//数据
                    LogUtils.e("data--->"+data);
                    String str = LocaleUtils.getI18nValue(tag);//获取到的翻译
                    LogUtils.e("str--->"+str);
                    String msg = str.replace("{0}",data);//替换
                    LogUtils.e("msg--->"+msg);
                    ToastUtil.showToastLong(msg,context);
                }
            });
        }catch (Exception e){
            ToastUtil.showToastLong(LocaleUtils.getI18nValue(tips),context);
            CrashReport.postCatchedException(e);
        }
    }
}
