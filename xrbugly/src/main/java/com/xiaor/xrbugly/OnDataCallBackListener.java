/*
 * Copyright (c) 2023. XiaoRGEEK.All Rights Reserved.
 * Author：陈超锋
 * Date：2023年2月25日
 * Describe：
 */

package com.xiaor.xrbugly;

public interface OnDataCallBackListener<T> {
    void onSucceed(T data);
    void onFailure(T error);
    void onError(T error);
    void onCommit(T data);
}
