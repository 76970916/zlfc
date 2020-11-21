package com.zlfcapp.poster.mvp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.king.base.SplashActivity;
import com.zlfcapp.poster.App;
import com.zlfcapp.poster.MainActivity;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.mvp.dialog.GrantPerissionDialog;
import com.zlfcapp.poster.mvp.dialog.ProtocolDialog;
import com.zlfcapp.poster.utils.NetWorkHelper;
import com.zlfcapp.poster.utils.StatusTitleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/15
 */

public class WelcomeActivity extends SplashActivity {
    private TextView mTextVer;
    private boolean flag = false;
    private LinearLayout mLayoutContent;
    private TextView skipView;
    private int type = 0;
    List<String> lackedPermission = new ArrayList<String>();
    ProtocolDialog protocoldialog;

    @Override
    public int getContentViewId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void initData() {
        // 设置状态栏
        StatusTitleUtil.setStatusBarColor(this, R.color.whitefa);
        StatusTitleUtil.StatusBarLightMode(this);
        mTextVer = (TextView) findViewById(R.id.splansh_text_ver);
        skipView = (TextView) findViewById(R.id.skip_view);
        mLayoutContent = (LinearLayout) findViewById(R.id.layout_content);
        skipView.setVisibility(View.VISIBLE);
        // getSupportActionBar().hide();
        mTextVer.setText("版本:" + App.getApp().getVer());

        // 获取配置文件
        NetWorkHelper.getInstance().getConfigInfo();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, 2000);
        if (App.getApp().getChanel().equals("myapp")) {
            App.getApp().saveBoolean("banner", true);
        }
        if (System.currentTimeMillis() / 1000 > 1541599200) {
            App.getApp().saveBoolean("banner", true);
        }
        boolean ishowProtocol = SPUtils.getInstance().getBoolean("isShowProtocol", true);
        if (ishowProtocol) {
            showDialogProtocol();
        } else {
            // 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAD接口。
            if (Build.VERSION.SDK_INT >= 23) {
                checkAndRequestPermission();
            } else {
                mHandler.sendEmptyMessage(0);
            }
        }
    }

    /**
     * @Author lixh
     * @Date 2020/4/3 15:02
     * @Description: 显示隐私协议弹窗
     */
    public void showDialogProtocol() {
        protocoldialog = new ProtocolDialog(this);
        protocoldialog.setCancelable(false);
        protocoldialog.show();
        protocoldialog.setOnDismissListener((DialogInterface dialog) -> {
            // 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAD接口。
            if (Build.VERSION.SDK_INT >= 23) {
                checkAndRequestPermission();
            } else {
                mHandler.sendEmptyMessage(0);
            }
        });
    }

    Handler mHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {

            if (flag) {
                Intent intent = new Intent(WelcomeActivity.this,
                        MainActivity.class);
                WelcomeActivity.this.startActivity(intent);
                WelcomeActivity.this.finish();
            } else {
                flag = true;
                skipView.setVisibility(View.VISIBLE);
            }
        }

    };

    @Override
    public Animation.AnimationListener getAnimationListener() {

        return new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //TODO: 启动动画结束，可执行跳转逻辑
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        // 手机状态
        if (!(this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }
        // 文件读写权限
        if (!(this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        // 位置信息
//        if (!(this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
//            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            mHandler.sendEmptyMessage(0);
            //  fetchSplashAD(this, mLayoutContent, skipView, Constants.APPID, Constants.SplashPosID, this, 0);
        } else {
//            请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
//            String[] requestPermissions = new String[lackedPermission.size()];
//            lackedPermission.toArray(requestPermissions);
//            requestPermissions(requestPermissions, 1024);
            GrantPerissionDialog dialog = new GrantPerissionDialog(this, this, lackedPermission);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
            //    fetchSplashAD(this, mLayoutContent, skipView, Constants.APPID, Constants.SplashPosID, this, 0);
            mHandler.sendEmptyMessage(0);
        } else {
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            try {
//                Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                intent.setData(Uri.parse("package:" + getPackageName()));
//                startActivity(intent);
                mHandler.sendEmptyMessage(0);
            } catch (Exception e) {

            }

        }
    }

    @Override
    public void addListeners() {

    }


}
