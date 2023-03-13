/*
 * Copyright (c) 2023. XiaoRGEEK.All Rights Reserved.
 * Author：陈超锋
 * Date：2023年2月25日
 * Describe：
 */

package com.xiaor.xrbugly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;


public class XRUpgradeDialog extends Dialog {
    private TextView btnCancel, btnUpgrade, updateTitle, updateSubtitle, updateInfo, updateFeature;
    private boolean isForceUpdate;
    private OnDataCallBackListener<String> listener;
    private AlertDialog dialog;

    public XRUpgradeDialog(@NonNull Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(XRBugly.upgradeDialogLayoutId, null);
        initView(context, layout);

    }

    public XRUpgradeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(XRBugly.upgradeDialogLayoutId, null);
        initView(context, layout);
    }


    private void initView(Context context, View view) {

        btnCancel = view.findViewById(R.id.btnCancel);
        btnUpgrade = view.findViewById(R.id.btnUpgrade);
        updateTitle = view.findViewById(R.id.updateTitle);
        updateSubtitle = view.findViewById(R.id.updateSubtitle);
        updateInfo = view.findViewById(R.id.updateInfo);
        updateFeature = view.findViewById(R.id.updateFeature);
        dialog = new AlertDialog.Builder(context, R.style.MyAppDialogTheme).create();
        dialog.show();
        dialog.setContentView(view);
        dialog.setOnKeyListener((dialog1, keyCode, event) -> {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                return isForceUpdate;
            }
            return false;
        });
//        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 高度设置为屏幕的0.8
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.CENTER);
        btnCancel.setOnClickListener(view1 -> {
            if (listener != null)
                listener.onCommit("close");
            this.close();
        });
        btnUpgrade.setOnClickListener(view1 -> {
            if (listener != null)
                listener.onCommit(btnUpgrade.getText().toString());
        });
    }

    public String getBtnCancelString() {
        return btnCancel.getText().toString();
    }

    public void setBtnCancelString(String btnCancel) {
        this.btnCancel.setText(btnCancel);
    }

    public String getBtnUpgradeString() {
        return btnUpgrade.getText().toString();
    }

    public void setBtnUpgradeString(String btnUpgrade) {
        this.btnUpgrade.setText(btnUpgrade);
    }

    public String getUpdateTitle() {
        return updateTitle.getText().toString();
    }

    public void setUpdateTitle(String updateTitle) {
        this.updateTitle.setText(updateTitle);
    }

    public String getUpdateSubtitle() {
        return updateSubtitle.getText().toString();
    }

    public void setUpdateSubtitle(String updateSubtitle) {
        this.updateSubtitle.setText(updateSubtitle);
    }

    public String getUpdateInfo() {
        return updateInfo.getText().toString();
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo.setText(updateInfo);
    }

    public String getUpdateFeature() {
        return updateFeature.getText().toString();
    }

    public void setUpdateFeature(String updateFeature) {
        this.updateFeature.setText(updateFeature);
    }

    public XRUpgradeDialog setIsForceUpdate(boolean forceUpdate) {
        setCanceledOnTouchOutside(!forceUpdate);
        this.isForceUpdate = forceUpdate;
        setCancelable(!forceUpdate);
        if (forceUpdate) {
            btnCancel.setVisibility(View.GONE);
        }
        return this;
    }

    public void close(){
        dialog.dismiss();
    }

    public void setOnDataCallBackListener(OnDataCallBackListener<String> listener){
        this.listener = listener;
    }
}
