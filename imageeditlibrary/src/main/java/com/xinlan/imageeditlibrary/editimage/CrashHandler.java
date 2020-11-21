package com.xinlan.imageeditlibrary.editimage;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";

    //系统默认的UncaughtException处理类
    private static Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private static Context mContext;
    //用来存储设备信息和异常信息
    private static Map<String, String> infos = new HashMap<String, String>();

    private static String appInfo = null;
    private static String deviceInfo = null;
    private static String errorInfo = null;
    //用于格式化日期,作为日志文件名的一部分
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");


    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                if (mContext != null) {
                    try {
                        Toast.makeText(mContext.getApplicationContext(), "抱歉!程序出现了一个BUG,即将退出,BUG将会尽快被修复", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
                }
                Looper.loop();
            }
        }.start();
        if (mDefaultHandler != null) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
//            //如果用户没有处理则让系统默认的异常处理器来处理
//            mDefaultHandler.uncaughtException(thread, ex);
        }

    }

    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null) {
            return (runningTaskInfos.get(0).topActivity).getClassName();
        } else
            return null;
    }


    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private static boolean handleException(Throwable ex) {
        Log.e("shea", "uncaught-----2");
        if (ex == null) {
            return false;
        }

        ex.printStackTrace();
        Log.e(TAG, "error : ", ex);

        //收集App信息
        collectAppInfo(mContext);

        //收集设备信息
        collectDeviceInfo();

        //收集设备参数信息
        collectDeviceInfo(mContext);
        collectErrorInfo(ex);

        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public static void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
            }
        }

    }

    /**
     * 搜集App信息
     */
    private static void collectAppInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                appInfo = versionName + ";" + versionCode;
            }
        } catch (NameNotFoundException e) {
        }
    }

    /**
     * 搜集设备信息
     */
    private static void collectDeviceInfo() {

        String device_model = Build.MODEL; // 设备型号
        String version_sdk = Build.VERSION.SDK; // 设备SDK版本
        String version_release = Build.VERSION.RELEASE; // 设备的系统版本
        String phone_serial = Build.SERIAL; // 设备码
        deviceInfo = device_model + ";" + version_sdk + ";" + version_release + ";" + phone_serial;
    }

    /**
     * 搜集错误信息
     *
     * @param ex
     */
    private static void collectErrorInfo(Throwable ex) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ex.printStackTrace(new PrintStream(baos));
        } finally {
            try {
                baos.close();
                errorInfo = baos.toString();

                String regEx = "Caused by:(.*)";
                Pattern pat = Pattern.compile(regEx);
                Matcher mat = pat.matcher(baos.toString());

                //只记录Caused by的内容
                errorInfo = mat.group(1);
            } catch (Exception e) {

            }
        }
    }

}
