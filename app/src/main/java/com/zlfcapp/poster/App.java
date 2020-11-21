package com.zlfcapp.poster;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.multidex.MultiDex;

import com.baidu.mobstat.StatService;
import com.bilibili.magicasakura.utils.ThemeUtils;
import com.blankj.utilcode.util.Utils;
import com.xinlan.imageeditlibrary.editimage.CrashHandler;
import com.zlfcapp.poster.di.component.AppComponent;
import com.zlfcapp.poster.di.component.DaggerAppComponent;
import com.zlfcapp.poster.di.module.AppModule;
import com.zlfcapp.poster.utils.ThemeHelper;

import org.litepal.LitePal;

import jonathanfinerty.once.Once;

/**
 * Created by ZhangBx on 2018/5/30.
 */

public class App extends Application implements ThemeUtils.switchColor {
    // 应用全局context
    private String ver = "";
    public static Context mContext;
    static App app;
    private SharedPreferences sp = null;
    private boolean isHaveShow = false;
    private int vercode;
    private String chanel = "";
    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        // once 初始化
        Once.initialise(this);
        // 初始化MultiDex
        MultiDex.install(this);
        CrashHandler.getInstance().init(this);
        // blankj 工具类初始化
        Utils.init(this);
        mContext = this.getApplicationContext();
        app = this;
        // 皮肤切换
        ThemeUtils.setSwitchColor(this);
        // LogcatHelper.getInstance(this).start();
        sp = PreferenceManager.getDefaultSharedPreferences(app);
        try {
            loadBase();
            StatService.start(this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this, Constants.BASE_URL)).build();
    }

    private void loadBase() {
        try {
            PackageManager manager = getApplicationContext()
                    .getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            ver = info.versionName;
            vercode = info.versionCode;

            if (TextUtils.isEmpty(ver)) {
                ver = "";
            } else {
            }
            ApplicationInfo appInfo = this.getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
            chanel = appInfo.metaData.getString("BaiduMobAd_CHANNEL");
            launchTime();
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void launchTime() {
        String tm = System.currentTimeMillis() / 1000 + "";
        String tms = getString("launchtime", "");
        if (!TextUtils.isEmpty(tms)) {
            try {
                String[] args = tms.split(",");
                if (args.length == 2) {
                    args[1] = args[2];
                    tms = args[0] + "," + args[1] + "," + tm;
                }
            } catch (Exception e) {

            }
        } else {
            tms = tm + "," + tm + "," + tm;
        }
        saveString("launchtime", tms);
    }


    @Override
    public int replaceColorById(Context context, @ColorRes int colorId) {
        if (ThemeHelper.isDefaultTheme(context)) {
            return context.getResources().getColor(colorId);
        }
        String theme = getTheme(context);
        if (theme != null) {
            colorId = getThemeColorId(context, colorId, theme);
        }
        return context.getResources().getColor(colorId);
    }

    @Override
    public int replaceColor(Context context, @ColorInt int originColor) {
        if (ThemeHelper.isDefaultTheme(context)) {
            return originColor;
        }
        String theme = getTheme(context);
        int colorId = -1;

        if (theme != null) {
            colorId = getThemeColor(context, originColor, theme);
        }
        return colorId != -1 ? getResources().getColor(colorId) : originColor;
    }


    private String getTheme(Context context) {
        if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_STORM) {
            return "blue";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_HOPE) {
            return "purple";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_WOOD) {
            return "green";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_LIGHT) {
            return "green_light";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_THUNDER) {
            return "yellow";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_SAND) {
            return "orange";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_FIREY) {
            return "red";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_SAKURA) {
            return "pink";
        }
        return null;
    }


    private
    @ColorRes
    int getThemeColorId(Context context, int colorId, String theme) {
        switch (colorId) {
            case R.color.theme_color_primary:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
            case R.color.theme_color_primary_dark:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
            case R.color.theme_color_primary_trans:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
        }
        return colorId;
    }

    private
    @ColorRes
    int getThemeColor(Context context, int color, String theme) {
        switch (color) {
            case 0xfffb7299:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
            case 0xffb85671:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
            case 0x99f0486c:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
        }
        return -1;
    }

    public int getVercode() {
        return vercode;
    }


    public String getChanel() {
        return chanel;
    }


    public static App getApp() {
        return app;
    }

    public static AppComponent getAppCommponent() {
        return mAppComponent;
    }

    public SharedPreferences getSp() {
        return sp;
    }

    public void saveString(String key, String value) {
        sp.edit().putString(key, value).commit();
    }

    public String getString(String key, String defvalue) {
        return sp.getString(key, defvalue);
    }

    public void saveInt(String key, int value) {
        sp.edit().putInt(key, value).commit();
    }

    public int getInt(String key, int defvalue) {
        return sp.getInt(key, defvalue);
    }

    public void saveBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defvaule) {
        return sp.getBoolean(key, defvaule);
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public static void setApp(App app) {
        App.app = app;
    }

    public void setSp(SharedPreferences sp) {
        this.sp = sp;
    }
}
