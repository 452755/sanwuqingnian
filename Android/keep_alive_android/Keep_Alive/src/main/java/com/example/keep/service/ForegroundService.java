package com.example.keep.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.keep.R;
import com.example.keep.constant.KeepAlive;


public class ForegroundService extends Service  {
    public static boolean refreshTimeThreadIsRun = false;

    private final static int SERVICE_ID = 1;

    private static final String TAG = ForegroundService.class.getSimpleName();

    private CharSequence notifyTitle = "";

    private CharSequence notifyText = "";

    private static ForegroundService owenr = null;

    private NotificationCompat.Builder builder = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        owenr = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN_MR2){
            //4.3以下
            startForeground(SERVICE_ID,new Notification());
        }else if (Build.VERSION.SDK_INT<Build.VERSION_CODES.O){
            //7.0以下
            startForeground(SERVICE_ID,new Notification());
            //删除通知栏
            startService(new Intent(this,InnerService.class));
        }else {
            //8.0以上
            try {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //NotificationManager.IMPORTANCE_MIN 通知栏消息的重要级别  最低，不让弹出
                //IMPORTANCE_MIN 前台时，在阴影区能看到，后台时 阴影区不消失，增加显示 IMPORTANCE_NONE时 一样的提示
                //IMPORTANCE_NONE app在前台没有通知显示，后台时有
                NotificationChannel channel = new NotificationChannel("channel", "后台运行", NotificationManager.IMPORTANCE_NONE);
                // 取消小红点
                channel.enableLights(false);
                if (notificationManager!=null){
                    this.notifyTitle = intent.getStringExtra("title");
                    this.notifyText = intent.getStringExtra("text");

                    // 获取当前传递的主页面的类名和类
                    String mainActivityClassName = intent.getStringExtra("mainActivityClassName");
                    Class<?> mainActivityClass = Class.forName(mainActivityClassName);

                    Intent appIntent = new Intent(this, mainActivityClass);

                    // 点击跳转页面
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, appIntent, PendingIntent.FLAG_IMMUTABLE);
                    notificationManager.createNotificationChannel(channel);
                    this.builder = new NotificationCompat.Builder(this, "channel");
                    this.builder.setSmallIcon(R.drawable.baseline_lock_24) //图标
                            .setOnlyAlertOnce(true)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 设置优先级 PRIORITY_DEFAULT
                            .setContentIntent(pendingIntent) // 跳转页面
                            .setVisibility(NotificationCompat.VISIBILITY_SECRET) // 屏幕可见性，1、VISIBILITY_PUBLIC 在所有锁定屏幕上完整显示此通知 2、VISIBILITY_PRIVATE 隐藏安全锁屏上的敏感或私人信息 3、VISIBILITY_SECRET 不显示任何部分
                            .setOngoing(true);

                    // 发送通知消息，并且设置消息的内容和发送时间
                    this.SendNotify();
                    // 启动定时刷新时间
                    // this.StartRefreshTimeThread();

                    Log.e(TAG, "onStart");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void SendNotify() {
        Log.e(TAG, "SendNotify");

        this.builder.setContentTitle(this.notifyTitle)
                // .setWhen(System.currentTimeMillis())
                .setShowWhen(false)
                .setContentText(this.notifyText);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(SERVICE_ID,builder.build());
        startForeground(SERVICE_ID,builder.build());
    }

    private static void StartRefreshTimeThread() {
        if (refreshTimeThreadIsRun == true) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                refreshTimeThreadIsRun = true;
                while (true) {
                    try {
                        owenr.SendNotify();

                        Thread.sleep(10000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.cancelAll();
        stopForeground(true);
        super.onDestroy();
    }

    public static void resetNotifyContent(String title, String text) {
        Log.e(TAG, "resetNotifyContent");
        owenr.notifyTitle = title;
        owenr.notifyText = text;
        owenr.SendNotify();
    }

    private static class InnerService extends Service{
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.d(KeepAlive.TAG, "onCreate: ");
            startForeground(SERVICE_ID,new Notification());
            stopSelf();
        }
    }
}
