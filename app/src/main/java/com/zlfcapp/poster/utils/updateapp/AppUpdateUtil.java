package com.zlfcapp.poster.utils.updateapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.baidu.mobstat.StatService;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.listener.ExceptionHandler;
import com.vector.update_app.listener.ExceptionHandlerHelper;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.bean.UpdateAppBeans;

import java.io.File;

/**
 * author  lixh
 * date 2020/4/1 13:12
 * desc app 版本更新工具类
 **/
public class AppUpdateUtil {
    // 忽略的版本
    public static final String IGNORE_VERSION = "ignore_version";
    // 版本更新xml
    private static final String PREFS_FILE = "update_app_config.xml";
    public static final int REQ_CODE_INSTALL_APP = 99;

    /**
     * author  lixh
     * desc 更新方法
     * 传入URL 上下文 即可下载更新
     */
    public static void updateApp(String url, Activity activity) {
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(activity)
                //更新地址
                .setUpdateUrl(url)
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        StatService.start(activity);
                        e.printStackTrace();
                    }
                })
                // 可指定头部图片
                .setTopPic(R.drawable.top_8)
                // 指定按钮和进度条颜色
                .setThemeColor(0xffffac5d)
                //实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                .build()
                .update();
    }

    /**
     * author  lixh
     * desc  获取apk文件
     */
    public static File getAppFile(UpdateAppBeans updateAppBeans) {
        String appName = getApkName(updateAppBeans);
        return new File(updateAppBeans.getTargetPath()
                .concat(File.separator + updateAppBeans.getVersionCode())
                .concat(File.separator + appName));
    }

    @NonNull
    public static String getApkName(UpdateAppBeans updateAppBeans) {
        String apkUrl = updateAppBeans.getApkPath();
        String appName = apkUrl.substring(apkUrl.lastIndexOf("/") + 1, apkUrl.length());
        if (!appName.endsWith(".apk")) {
            appName = "temp.apk";
        }
        return appName;
    }

    /**
     * @Author lixh
     * @Date 2020/4/3 17:00
     * @Description: 安装APP
     */
    public static boolean installApp(Activity activity, File appFile) {
        try {
            Intent intent = getInstallAppIntent(activity, appFile);
            if (activity.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                activity.startActivityForResult(intent, REQ_CODE_INSTALL_APP);
            }
            return true;
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = ExceptionHandlerHelper.getInstance();
            if (exceptionHandler != null) {
                exceptionHandler.onException(e);
            }
        }
        return false;
    }

    public static Intent getInstallAppIntent(Context context, File appFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", appFile);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(appFile), "application/vnd.android.package-archive");
            }
            return intent;
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = ExceptionHandlerHelper.getInstance();
            if (exceptionHandler != null) {
                exceptionHandler.onException(e);
            }
        }
        return null;
    }

    public static boolean installApp(Fragment fragment, File appFile) {
        return installApp(fragment.getActivity(), appFile);
    }

    private static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

    public static void saveIgnoreVersion(Context context, String newVersion) {
        getSP(context).edit().putString(IGNORE_VERSION, newVersion).apply();
    }

    public static boolean isNeedIgnore(Context context, String newVersion) {
        return getSP(context).getString(IGNORE_VERSION, "").equals(newVersion);
    }
}
