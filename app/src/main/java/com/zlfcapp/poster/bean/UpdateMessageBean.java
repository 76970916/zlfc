package com.zlfcapp.poster.bean;


/**
 * @Description: 更新实体类
 * @Author: lixh
 * @CreateDate: 2020/4/18 17:31
 * @Version: 1.0
 */
public class UpdateMessageBean {

    private String appName;
    private String ico;
    private String versionName;
    private int versionCode;
    private String packageName;
    private String apkPath;
    private boolean comple;
    private boolean update;
    private double apkSize;
    private String update_log;
    private int compleVersion;
    private int updateVersion;

    public int getCompleVersion() {
        return compleVersion;
    }

    public void setCompleVersion(int compleVersion) {
        this.compleVersion = compleVersion;
    }

    public int getUpdateVersion() {
        return updateVersion;
    }

    public void setUpdateVersion(int updateVersion) {
        this.updateVersion = updateVersion;
    }

    public String getUpdate_log() {
        return update_log;
    }

    public void setUpdate_log(String update_log) {
        this.update_log = update_log;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public String getIco() {
        return ico;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setComple(boolean comple) {
        this.comple = comple;
    }

    public boolean getComple() {
        return comple;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean getUpdate() {
        return update;
    }

    public void setApkSize(double apkSize) {
        this.apkSize = apkSize;
    }

    public double getApkSize() {
        return apkSize;
    }

}