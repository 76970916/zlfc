package com.zlfcapp.poster.bean;

import java.util.Date;


public class UpdateAppBeans{
    // app 名称
    private String appName;
    // app 图标
    private String iconApp;
    // 版本名称
    private String versionName;
    // 版本号
    private float versionCode;
    // 更新地址
    private String apkPath;
    private String targetPath;
    // 是否强制更新
    private boolean comple;
    // 更新信息
    private String message;
    // 是否更新
    private boolean update;
    // 更新apk的大小
    private String apkSize;

    private String packageName;
    private Date createTime;
    private Date updateTime;

    public String getApkSize() {
        return apkSize;
    }

    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public float getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(float versionCode) {
        this.versionCode = versionCode;
    }

    public boolean isComple() {
        return comple;
    }

    public void setComple(boolean comple) {
        this.comple = comple;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIconApp() {
        return iconApp;
    }

    public void setIconApp(String iconApp) {
        this.iconApp = iconApp;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }
}
