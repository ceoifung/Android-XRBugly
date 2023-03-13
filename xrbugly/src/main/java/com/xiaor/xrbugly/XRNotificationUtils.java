/*
 * Copyright (c) 2023. XiaoRGEEK.All Rights Reserved.
 * Author：陈超锋
 * Date：2023年2月28日
 * Describe：
 */

package com.xiaor.xrbugly;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class XRNotificationUtils {
    private Notification notification;
    private NotificationManagerCompat notificationManager;
    private final String TAG = "XRNotificationUtils";

    private static class InstanceHolder {
        public static XRNotificationUtils xrNotificationUtils = new XRNotificationUtils();
    }

    public static XRNotificationUtils getInstance() {
        return InstanceHolder.xrNotificationUtils;
    }

    public void createNotification(Context context) {
        Intent intent = new Intent(context, XRBetaActivity.class);
//        Intent it = new Intent(Intent.ACTION_MAIN);
//        it.setPackage(pkg);//pkg为包名
//        it.addCategory(Intent.CATEGORY_LAUNCHER);
//        ComponentName ac = it.resolveActivity(mPackageManager);//mPackageManager为PackageManager实例
//        String classname =ac.getClassName() ;
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        String channelId = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N
        ) {
            channelId = createNotificationChannel(context, "com.xiaor.xrbugly", context.getPackageName(), NotificationManager.IMPORTANCE_DEFAULT );
        }
        Log.e(TAG, "createNotification: "+channelId );
        if (channelId != null){
            notification = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle("通知")
                    .setContentText("收到一条消息")
                    .setContentIntent(pendingIntent)
//                    .setSmallIcon(R.drawable.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true).build();
//            notification.setProgress(100, 0, false);
            notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, notification);
//            context.getClass().getPackageName().
//            com.xiaor.xrbugly.
        }else{
            Log.e(TAG, "createNotification: channelId 为空" );
        }

    }

    public void updateProgress(String title, int progress) {
        if (notification != null && notificationManager != null) {
//            notification.setContentText(title)
//                    .setProgress(100, progress, false);
//            notificationManager.notify(1, notification.build());
        }
    }

    private String createNotificationChannel(Context context, String channelID, String channelNAME, int level) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, channelNAME, level);
            manager.createNotificationChannel(channel);
            return channelID;
        } else {
            return null;
        }
    }

}
