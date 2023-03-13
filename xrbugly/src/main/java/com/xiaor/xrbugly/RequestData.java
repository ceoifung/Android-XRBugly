/*
 * Copyright (c) 2023. XiaoRGEEK.All Rights Reserved.
 * Author：陈超锋
 * Date：2023年2月25日
 * Describe：
 */

package com.xiaor.xrbugly;

import java.io.Serializable;

public class RequestData implements Serializable {
    private String appTitle;
    private String versionName;
    private String createAt;
    private String filePath;
    private String updateInfo;
    private boolean forceUpdate;
    private String fileSize;

    public RequestData(String appTitle, String versionName, String createAt, String filePath, String updateInfo, boolean forceUpdate, String fileSize) {
        this.appTitle = appTitle;
        this.versionName = versionName;
        this.createAt = createAt;
        this.filePath = filePath;
        this.updateInfo = updateInfo;
        this.forceUpdate = forceUpdate;
        this.fileSize = fileSize;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "{" +
                "\"versionName\":\"" + versionName + "\"" +
                ", \"createAt\":\"" + createAt + "\"" +
                ", \"filePath\":\"" + filePath + "\"" +
                ", \"updateInfo\":\"" + updateInfo + "\"" +
                ", \"forceUpdate\":" + forceUpdate +
                ", \"fileSize\":\"" + fileSize + "\"" +
                "}";
    }
}
