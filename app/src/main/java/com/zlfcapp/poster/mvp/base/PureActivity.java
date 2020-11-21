package com.zlfcapp.poster.mvp.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.baidu.mobstat.StatService;
import com.bilibili.magicasakura.utils.ThemeUtils;
import com.zlfcapp.poster.App;
import com.zlfcapp.poster.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/3
 */

public abstract class PureActivity extends AppCompatActivity {

    private static final int INVALID_VAL = -1;
    private static final int COLOR_DEFAULT = Color.parseColor("#303F9F");

    protected Context context;
    private Unbinder unbind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        context = this;
        setContentView(getRootViewId());
        unbind = ButterKnife.bind(this);
        initUI();
        //setStatusViewColor(getResources().getColor(R.color.colorPrimary));
        //    setStatusBarDarkMode();
    }

    public void setStatusBarDarkMode() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    public void replaceFragment(Fragment fragmnet) {
        replaceFragment(R.id.fragmentContent, fragmnet);
    }

    public void replaceFragment(@IdRes int id, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(id, fragment).commit();
    }


    public App getApp() {
        return (App) getApplication();
    }

    public void setStatusViewColorDefault() {
        setStatusViewColor(ThemeUtils.getColorById(this, R.color.theme_color_primary_dark));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setStatusViewColor(int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (statusColor != INVALID_VAL) {
                getWindow().setStatusBarColor(statusColor);
            }
            return;
        }
//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int color = COLOR_DEFAULT;
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            if (statusColor != INVALID_VAL) {
                color = statusColor;
            }
            View statusBarView = new View(this);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(this));
            statusBarView.setBackgroundColor(color);
            contentView.addView(statusBarView, lp);
        }
    }

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbind != null) {
            unbind.unbind();
        }
    }

    public abstract int getRootViewId();

    public abstract void initUI();
}
