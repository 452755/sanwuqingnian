package com.emms.activity;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ProgressBar;

import com.emms.R;
import com.emms.push.PushReceiver;
import com.emms.push.PushService;
import com.emms.schema.Operator;
import com.emms.util.LocaleUtils;
import com.emms.util.SharedPreferenceManager;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Linkgoo.zdc on 2018/5/21.
 */

public class SplashActivity extends NfcActivity{
    private BroadcastReceiver receiver = null;
    private static ProgressBar mProgressBarHorizontal;
    private MyHandler mHandler;
    private final static int PROGRESSBAR_START = 0;
    private Handler pushHandler = PushService.mHandler;
    private final static int PROGRESSBAR_STOP = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //语言设置
        LocaleUtils.setLanguage(this, LocaleUtils.getLanguage(this), false);
        setContentView(R.layout.activity_splash);
        initProgressBar();
        initLanguageBroadcastReceiver();
        LocaleUtils.initI18n(this);
        ChangeServerConnectBaseOnNetwork();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initProgressBar(){
        mProgressBarHorizontal = (ProgressBar) findViewById(R.id.progressBarHorizontal);
        mHandler = new MyHandler();
        mHandler.sendEmptyMessage(PROGRESSBAR_START);
    }

    private void initLanguageBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mHandler.sendEmptyMessage(PROGRESSBAR_STOP);
//                startActivity(new Intent(context, LoginActivity.class));
                if (SharedPreferenceManager.getLoginData(context)==null|| TextUtils.isEmpty(SharedPreferenceManager.getLoginData(context))){
                    startActivity(new Intent(context, LoginActivity.class));
                }else {
                    Intent pintent = new Intent(context,CusActivity.class);
                    pintent.putExtra("isFromSplash",true);
                    initPush(context,SharedPreferenceManager.getLoginData(context));
                    startActivity(pintent);
                }

                finish();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocaleUtils.LANGUAGE_SETTING_ACTION);
        registerReceiver(receiver, filter);
    }

    private void initPush(Context context,String data){
        try {
            //调用JPush API设置Tag\
            String Organise_ID = new JSONObject(data).get("Organise_ID").toString();
            Set<String> tagSet = new LinkedHashSet<>();
            //tagSet.add(userid);
            tagSet.add("1002");
            String[] or = Organise_ID.split(",");
            Collections.addAll(tagSet, or);
            //tagSet.add(new JSONObject(data).get(Operator.OPERATOR_ID).toString());
            if(PushReceiver.PushTagOrAliasList==null){
                PushReceiver.PushTagOrAliasList=new ArrayList<>();
            }
            PushReceiver.PushTagOrAliasList.clear();
            Collections.addAll(PushReceiver.PushTagOrAliasList,or);
            PushReceiver.PushTagOrAliasList.add(new JSONObject(data).get(Operator.OPERATOR_ID).toString());
            JPushInterface.resumePush(context);
            //setStyleBasic();
            setStyleCustom();
            pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_TAGS, tagSet));
            pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_ALIAS, new JSONObject(data).get(Operator.OPERATOR_ID).toString()));
        }catch (Throwable e){
            CrashReport.postCatchedException(e);
        }
    }

    /**
     * 极光通知栏样式
     * 从loginActivity中转移过来
     */
    private void setStyleCustom(){
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(this,R.layout.customer_notitfication_layout,R.id.icon, R.id.title, R.id.text);
        builder.layoutIconDrawable = R.drawable.ic_emms;
        builder.layoutIconId=R.drawable.ic_emms;
        builder.statusBarDrawable=R.drawable.ic_emms;
//        builder.layoutIconDrawable = R.mipmap.emmsa;
//        builder.layoutIconId=R.mipmap.emmsa;
//        builder.statusBarDrawable=R.mipmap.emmsa;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
        //builder.notificationDefaults = Notification.DEFAULT_VIBRATE;
        builder.notificationDefaults = Notification.DEFAULT_ALL;
        builder.developerArg0 = "developerArg2";
        JPushInterface.setDefaultPushNotificationBuilder(builder);
        JPushInterface.setPushNotificationBuilder(2, builder);
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }

    static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESSBAR_START:
                    // 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
                    this.removeMessages(0);
                    if (mProgressBarHorizontal.getProgress() == 100){
                        mProgressBarHorizontal.setProgress(0);
                        this.sendEmptyMessageDelayed(0, 3000);
                    } else {
                        mProgressBarHorizontal.incrementProgressBy(5);
                        // 再次发出msg，循环更新
                        this.sendEmptyMessageDelayed(0, 100);
                    }
                    break;

                case PROGRESSBAR_STOP:
                    // 直接移除，定时器停止
                    this.removeMessages(0);
                    break;

                default:
                    break;
            }
        };

    }
}
