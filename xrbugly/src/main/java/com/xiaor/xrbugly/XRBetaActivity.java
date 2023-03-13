/*
 * Copyright (c) 2023. XiaoRGEEK.All Rights Reserved.
 * Author：陈超锋
 * Date：2023年2月28日
 * Describe：
 */

package com.xiaor.xrbugly;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import java.io.File;

public class XRBetaActivity extends AppCompatActivity {

    private static final String TAG = "XRBetaActivity";
    private String appName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xrbeta);
        String[] permissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, 1001);
        RequestData data = (RequestData) getIntent().getSerializableExtra("requestForm");
        if (data != null)
            showUpgradeDialog(this, data);
    }
    int downloadId;
    private void showUpgradeDialog(Context context, RequestData reqData) {
        ((Activity) context).runOnUiThread(() -> {
            XRUpgradeDialog dialog = new XRUpgradeDialog(context)
                    .setIsForceUpdate(reqData.isForceUpdate());
            dialog.setUpdateSubtitle(reqData.getAppTitle());
            dialog.setUpdateInfo(context.getString(R.string.update_info_template, reqData.getVersionName(),
                    reqData.getFileSize(), reqData.getCreateAt()));
            dialog.setUpdateFeature(reqData.getUpdateInfo());
            dialog.setOnDataCallBackListener(new OnDataCallBackListener<String>() {
                @Override
                public void onSucceed(String data) {

                }

                @Override
                public void onFailure(String error) {

                }

                @Override
                public void onError(String error) {

                }

                @Override
                public void onCommit(String data) {
                    if (context.getString(R.string.strUpgradeDialogUpgradeBtn).equals(data)) {
//                        XRNotificationUtils.getInstance().createNotification(context);
                        appName = reqData.getVersionName()+".apk";
                        downloadId = PRDownloader.download(reqData.getFilePath(), XRBugly.DOWNLOAD_DIR, appName)
                                .build()
                                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                    @Override
                                    public void onStartOrResume() {

                                    }
                                })
                                .setOnPauseListener(new OnPauseListener() {
                                    @Override
                                    public void onPause() {

                                    }
                                })
                                .setOnCancelListener(new OnCancelListener() {
                                    @Override
                                    public void onCancel() {

                                    }
                                })
                                .setOnProgressListener(new OnProgressListener() {
                                    @Override
                                    public void onProgress(Progress progress) {
//                                        Log.e(TAG, "onProgress: 下载进度==" + progress.currentBytes);
                                        double percent = (progress.currentBytes * 1.0) / progress.totalBytes * 100;
//                                        XRNotificationUtils.getInstance().updateProgress("downloading", (int) Math.round(percent));
                                        dialog.setBtnUpgradeString(Math.round(percent) + "%");
                                    }
                                })
                                .start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {
//                                        Log.e(TAG, "onDownloadComplete: 下载完成");
//                                    dialog.close();
                                        XRNotificationUtils.getInstance().updateProgress(context.getString(R.string.strNotificationClickToInstall), 100);
                                        dialog.setBtnUpgradeString(context.getString(R.string.strNotificationClickToInstall));

                                    }

                                    @Override
                                    public void onError(Error error) {
                                        Log.e(TAG, "onError: 下载错误" + error.getServerErrorMessage());
                                        Toast.makeText(context, "下载错误", Toast.LENGTH_SHORT).show();
                                        dialog.setBtnUpgradeString(context.getString(R.string.strNotificationClickToRetry));
//                                        dialog.close();
                                    }
                                });
                    } else if (context.getString(R.string.strNotificationClickToInstall).equals(data)) {
                        installApk(context);
                        dialog.dismiss();
//                        finish();
                    }else if (context.getString(R.string.strNotificationClickToRetry).equals(data)) {
//                        Log.e(TAG, "onCommit: " + downloadId);
//                        PRDownloader.resume(downloadId);
                        dialog.setBtnUpgradeString(context.getString(R.string.strUpgradeDialogUpgradeBtn));
                    }
                    else if ("close".equals(data)){
                        XRBugly.hasNewVersion = false;
                        finish();
                    }
                    else {
                        Log.e(TAG, "onCommit: 正在更新中，无效点击");
                    }


                }
            });
        });

    }

    /**
     * 安装apk
     *
     * @param context 上下文
     */
    private void installApk(Context context) {
        Intent installIntent = new Intent();
        installIntent.setAction(Intent.ACTION_VIEW);
        //在Boradcast中启动活动需要添加Intent.FLAG_ACTIVITY_NEW_TASK
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        File f = new File(XRBugly.DOWNLOAD_DIR + appName); //缓存路径

        if (f.exists())
            Toast.makeText(context, context.getString(R.string.start_install_apk), Toast.LENGTH_SHORT).show();  //判断下载的apk是否存在  存在true  不存在false
        else Toast.makeText(context, "false!", Toast.LENGTH_SHORT).show();

        String type = "application/vnd.android.package-archive";    //安装路径
        try {
            String packageName = context.getPackageName();
            if (Build.VERSION.SDK_INT >= 24) {   //判断安卓系统是否大于7.0  大于7.0使用以下方法
                Uri uri = FileProvider.getUriForFile(context, packageName + ".fileProvider", f);      //转为URI格式  第二个参数为AndroidManifest.xml中定义的authorities的值
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                installIntent.addFlags(installIntent.FLAG_GRANT_READ_URI_PERMISSION);
                installIntent.setDataAndType(uri, type);
            } else {
                installIntent.setDataAndType(Uri.fromFile(f), type);//存储位置为Android/data/包名/file/Download文件夹
            }
            context.startActivity(installIntent);   //启动生命周期
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: XRBetaActivity" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: XRBetaActivity Destory" );
//        RequestData data = (RequestData) getIntent().getSerializableExtra("requestForm");
//        if (data != null)
//            showUpgradeDialog(this, data);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            Log.e(TAG, "onWindowFocusChanged: focuse change" );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: XRBetaActivity Destory" );
    }
}