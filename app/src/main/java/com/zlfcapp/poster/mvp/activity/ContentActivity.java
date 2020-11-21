package com.zlfcapp.poster.mvp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.king.base.util.LogUtils;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.mvp.fragment.AboutFragment;
import com.zlfcapp.poster.mvp.fragment.FeedBackFragment;
import com.zlfcapp.poster.mvp.fragment.TypeFaceFragment;
import com.zlfcapp.poster.mvp.fragment.WebFragment;
import com.zlfcapp.poster.utils.StatusTitleUtil;

/**
 * @Description: Fragment承载页面
 * @Author: lixh
 * @CreateDate: 2020/9/22 19:56
 * @Version: 1.0
 */
public class ContentActivity extends AppCompatActivity {
    private OnBackPressLinstener mOnBackPressLinstener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusTitleUtil.setStatusBarColor(this, R.color.whitefa);
        StatusTitleUtil.StatusBarLightMode(this);
        setContentView(R.layout.content);
        //   setStatusBarDarkMode();
        swichFragment(getIntent());
    }

    public void setStatusBarDarkMode() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public void swichFragment(Intent intent) {
        int fragmentKey = intent.getIntExtra(Constants.KEY_FRAGMENT, 0);
        switch (fragmentKey) {
            case Constants.ABOUT_FRAGMENT:
                replaceFragment(AboutFragment.newInstance());
                break;
            case Constants.WEB_FRAGMENT:
                replaceFragment(WebFragment.newInstance(intent.getStringExtra(Constants.KEY_URL), intent.getStringExtra(Constants.KEY_TITLE)));
                break;
            case Constants.FAQ_FRAGMENT:
                break;
            case Constants.FEEDBACK_FRAGMENT:
                // 反馈页面
                replaceFragment(FeedBackFragment.newInstance());
                break;
            case Constants.TYPEFACE_FRAGMENT:
                // 字体设置页面
                replaceFragment(TypeFaceFragment.newInstance());
                break;
            default:
                LogUtils.d("Not found fragment:" + Integer.toHexString(fragmentKey));
                break;
        }
    }

    public void setOnBackPressLinstener(OnBackPressLinstener mOnBackPressLinstener) {
        this.mOnBackPressLinstener = mOnBackPressLinstener;
    }

    public void replaceFragment(Fragment fragmnet) {
        replaceFragment(R.id.fragmentContent, fragmnet);
    }

    public void replaceFragment(@IdRes int id, Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(id, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (mOnBackPressLinstener != null) {
            mOnBackPressLinstener.onBackPress();
        } else {
            finish();
        }
    }

    public void OnBack(View view) {

        finish();
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        TextView text_title = (TextView) findViewById(R.id.tvTitle);
        if (text_title != null) {
            text_title.setText(title);
        }
    }

    public interface OnBackPressLinstener {
        void onBackPress();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
