/*
 * Copyright (c) 2023. XiaoRGEEK.All Rights Reserved.
 * Author：陈超锋
 * Date：2023年2月25日
 * Describe：
 */

package com.xiaor.xrbugly;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class XRBugly {
    private static boolean debug = false;
    private static String productCode;
    //    public static boolean autoCheckUpdate;
    private final static String TAG = "XRBugly";
    //    private static String DOWNLOAD_DIR = ;
    public static int upgradeDialogLayoutId;
    public static String versionCode;
    public static String DOWNLOAD_DIR = "";
    public static boolean hasNewVersion;

    /**
     * 初始化XRBugly
     *
     * @param context     上下文
     * @param productCode 产品编码
     * @param debug       是否开启调试功能
     */
    public static void init(Context context, String productCode, boolean debug) {
        XRBugly.debug = debug;
        XRBugly.productCode = productCode;
        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        XRBugly.upgradeDialogLayoutId = R.layout.item_layout_dialog;
        XRBugly.versionCode = getVersionCode(context);
        logPrint("init: " + context.getPackageName());
        XRBugly.DOWNLOAD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/XRBugly/" +
                context.getPackageName() + File.separator;
        File dirFile = new File(DOWNLOAD_DIR);
        if (!dirFile.exists()) {
            if (!createFileDir(dirFile)) {
                Log.e(TAG, "createFile dirFile.mkdirs fail");
            }
        }
        PRDownloader.initialize(context.getApplicationContext(), config);
    }

    public static void logPrint(String data) {
        if (XRBugly.debug) {
            Log.e(TAG, "logPrint: " + data);
        }
    }

    /**
     * 自动检查更新
     * @param context 上下文
     * @param upgradeUrl 更新url
     */
    public static void autoUpgrade(Context context, String upgradeUrl) {
        checkUpgrade(context, upgradeUrl, null);
    }

    /**
     * 手动检查更新
     * @param context 上下文
     * @param upgradeUrl 更新的url
     * @param dataCallBackListener 数据回调
     */
    public static void checkUpgrade(Context context, String upgradeUrl, OnDataCallBackListener<String> dataCallBackListener) {
        try{
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder().proxy(Proxy.NO_PROXY).build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            final JSONObject json = new JSONObject();
            try {
                logPrint(XRBugly.versionCode);
                json.put("productCode", XRBugly.productCode);
                json.put("versionCode", XRBugly.versionCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
            Request request = new Request.Builder()
                    .url(upgradeUrl)
                    .post(requestBody)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    logPrint(e.getMessage());
                    hasNewVersion = false;
                    if (dataCallBackListener != null)
                        dataCallBackListener.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        if (response.body() != null) {
                            String data = Objects.requireNonNull(response.body()).string();
                            logPrint(data);
                            JSONObject jsonObject = new JSONObject(data);
//                        logPrint(jsonObject.toString());
                            int code = jsonObject.optInt("code");
                            if (code == 200) {
                                hasNewVersion = true;
//                            JSONObject.(jsonObject, RequestData.class);
//                            new Gson().fromJson(jsonObject.optJSONObject("message").toString(), RequestData.class);
//                            showUpgradeDialog(context, new Gson().fromJson(
//                                    Objects.requireNonNull(jsonObject.optJSONObject("message")).toString(), RequestData.class));
                                RequestData data1 = new Gson().fromJson(
                                        Objects.requireNonNull(jsonObject.optJSONObject("message")).toString(), RequestData.class);
                                logPrint("onResponse: "+data1.toString() );
                                Intent startIntent = new Intent(context, XRBetaActivity.class);
                                startIntent.putExtra("requestForm", data1);
                                startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                context.startActivity(startIntent);
                            } else {
                                hasNewVersion = false;
                                if (dataCallBackListener != null)
                                    dataCallBackListener.onSucceed(jsonObject.optString("message"));
                            }

                        }
                    } catch (JSONException e) {
                        hasNewVersion = false;
                        if (dataCallBackListener != null)
                            dataCallBackListener.onError(e.getMessage());
                        e.printStackTrace();

                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static String getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode + "";

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 创建文件夹---之所以要一层层创建，是因为一次性创建多层文件夹可能会失败！
     */
    public static boolean createFileDir(File dirFile) {
        if (dirFile == null) return true;
        if (dirFile.exists()) {
            return true;
        }
        File parentFile = dirFile.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            //父文件夹不存在，则先创建父文件夹，再创建自身文件夹
            return createFileDir(parentFile) && createFileDir(dirFile);
        } else {
            boolean mkdirs = dirFile.mkdirs();
            boolean isSuccess = mkdirs || dirFile.exists();
            if (!isSuccess) {
                Log.e("FileUtil", "createFileDir fail " + dirFile);
            }
            return isSuccess;
        }
    }

//    private static int downloadId;

//    //    private
//    private static void showUpgradeDialog(Context context, RequestData reqData) {
//        ((Activity) context).runOnUiThread(() -> {
//            XRUpgradeDialog dialog = new XRUpgradeDialog(context)
//                    .setIsForceUpdate(reqData.isForceUpdate());
//            dialog.setUpdateSubtitle(reqData.getAppTitle());
//            dialog.setUpdateInfo(context.getString(R.string.update_info_template, reqData.getVersionName(),
//                    reqData.getFileSize(), reqData.getCreateAt()));
//            dialog.setUpdateFeature(reqData.getUpdateInfo());
//            dialog.setOnDataCallBackListener(new OnDataCallBackListener<String>() {
//                @Override
//                public void onSucceed(String data) {
//
//                }
//
//                @Override
//                public void onFailure(String error) {
//
//                }
//
//                @Override
//                public void onError(String error) {
//
//                }
//
//                @Override
//                public void onCommit(String data) {
//
//                    if (context.getString(R.string.strUpgradeDialogUpgradeBtn).equals(data)) {
////                        XRNotificationUtils.getInstance().createNotification(context);
//                        downloadId = PRDownloader.download(reqData.getFilePath(), XRBugly.DOWNLOAD_DIR, "app.apk")
//                                .build()
//                                .setOnStartOrResumeListener(() -> {
//                                    Log.e(TAG, "onCommit: 网络重试");
//                                })
//                                .setOnPauseListener(() -> {
//
//                                })
//                                .setOnCancelListener(() -> {
//
//                                })
//                                .setOnProgressListener(progress -> {
////                                        Log.e(TAG, "onProgress: 下载进度==" + progress.currentBytes);
//                                    double percent = (progress.currentBytes * 1.0) / progress.totalBytes * 100;
////                                    XRNotificationUtils.getInstance().updateProgress("downloading", (int) Math.round(percent));
//                                    dialog.setBtnUpgradeString(Math.round(percent) + "%");
//                                })
//                                .start(new OnDownloadListener() {
//                                    @Override
//                                    public void onDownloadComplete() {
////                                        Log.e(TAG, "onDownloadComplete: 下载完成");
////                                    dialog.close();
//                                        XRNotificationUtils.getInstance().updateProgress(context.getString(R.string.strNotificationClickToInstall), 100);
//                                        dialog.setBtnUpgradeString(context.getString(R.string.strNotificationClickToInstall));
//                                    }
//
//                                    @Override
//                                    public void onError(Error error) {
//                                        Log.e(TAG, "onError: 下载错误" + error.getServerErrorMessage());
//                                        Toast.makeText(context, "下载错误", Toast.LENGTH_SHORT).show();
//                                        dialog.setBtnUpgradeString(context.getString(R.string.strNotificationClickToRetry));
////                                        dialog.close();
//                                    }
//                                });
//                        Log.e(TAG, "onCommit: start" + downloadId);
//                    } else if (context.getString(R.string.strNotificationClickToInstall).equals(data)) {
//                        installApk(context);
//                    } else if (context.getString(R.string.strNotificationClickToRetry).equals(data)) {
//                        Log.e(TAG, "onCommit: " + downloadId);
////                        PRDownloader.resume(downloadId);
//                        dialog.setBtnUpgradeString(context.getString(R.string.strUpgradeDialogUpgradeBtn));
//                    } else {
//                        Log.e(TAG, "onCommit: 正在更新中，无效点击");
//                    }
//
//
//                }
//            });
//        });
//
//    }
//
//    public static boolean isApkExits() {
//        File f = new File(XRBugly.DOWNLOAD_DIR + "app.apk"); //缓存路径
//        return f.exists();
//    }
//
//    /**
//     * 安装apk
//     *
//     * @param context 上下文
//     */
//    private static void installApk(Context context) {
//        Intent installIntent = new Intent();
//        installIntent.setAction(Intent.ACTION_VIEW);
//        //在Boradcast中启动活动需要添加Intent.FLAG_ACTIVITY_NEW_TASK
//        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        File f = new File(XRBugly.DOWNLOAD_DIR + "app.apk"); //缓存路径
//
//        if (f.exists())
//            Toast.makeText(context, "true!", Toast.LENGTH_SHORT).show();  //判断下载的apk是否存在  存在true  不存在false
//        else Toast.makeText(context, "false!", Toast.LENGTH_SHORT).show();
//
//        String type = "application/vnd.android.package-archive";    //安装路径
//        try {
//            String packageName = context.getPackageName();
//            if (Build.VERSION.SDK_INT >= 24) {   //判断安卓系统是否大于7.0  大于7.0使用以下方法
//                Uri uri = FileProvider.getUriForFile(context, packageName + ".fileProvider", f);      //转为URI格式  第二个参数为AndroidManifest.xml中定义的authorities的值
//                //添加这一句表示对目标应用临时授权该Uri所代表的文件
//                installIntent.addFlags(installIntent.FLAG_GRANT_READ_URI_PERMISSION);
//                installIntent.setDataAndType(uri, type);
//            } else {
//                installIntent.setDataAndType(Uri.fromFile(f), type);//存储位置为Android/data/包名/file/Download文件夹
//            }
//            context.startActivity(installIntent);   //启动生命周期
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
