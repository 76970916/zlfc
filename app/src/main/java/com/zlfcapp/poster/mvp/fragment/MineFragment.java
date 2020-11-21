package com.zlfcapp.poster.mvp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.bean.BaseConfig;
import com.zlfcapp.poster.utils.SPUtils;
import com.zlfcapp.poster.utils.ShareUtils;
import com.zlfcapp.poster.utils.updateapp.AppUpdateUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Description: 我的 页面
 * @Author: lixh
 * @CreateDate: 2020/9/22 17:29
 * @Version: 1.0
 */
public class MineFragment extends SimpleFragment {

    @BindView(R.id.nickname)
    TextView nickname;
    @BindView(R.id.switch_mode)
    TextView switch_mode;
    @BindView(R.id.appbar)
    AppBarLayout appbar;

    public static MineFragment newInstance() {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_more;
    }

    @Override
    public void initUI() {
    }

    @Override
    public void initData() {
        nickname.setText(getContext().getString(R.string.app_name));
    }

    @OnClick({R.id.tv_more_about, R.id.tv_more_feedback, R.id.tv_more_good, R.id.tv_accord_update,
            R.id.tv_more_share, R.id.line_skin, R.id.tv_more_protocol, R.id.tv_more_secret})
    public void onViewClicked(View view) {
        Intent intent = getContentActivityIntent();
        switch (view.getId()) {
            case R.id.tv_more_about:
                intent.putExtra(Constants.KEY_FRAGMENT, Constants.ABOUT_FRAGMENT);
                getActivity().startActivity(intent);
                break;
            case R.id.tv_more_feedback:
                intent.putExtra(Constants.KEY_FRAGMENT, Constants.FEEDBACK_FRAGMENT);
                getActivity().startActivity(intent);
                break;
            case R.id.tv_more_good:
                startActivity(new Intent("android.intent.action.VIEW", Uri
                        .parse("market://details?id=" + getActivity().getPackageName())));
                break;
            case R.id.tv_more_share:
                BaseConfig config = BaseConfig.getInstance(true);
                String shareUrl = config.getShareurl();
                ShareUtils.shareFile(getActivity(), "快来一起使用" +
                        getContext().getString(R.string.app_name) + "app，下载地址(请用浏览器打开下载):\n" + shareUrl);
                break;
            case R.id.line_skin:
                break;
            // 隐私协议
            case R.id.tv_more_protocol:
                intent.putExtra(Constants.KEY_FRAGMENT, Constants.WEB_FRAGMENT);
                intent.putExtra(Constants.KEY_URL, Constants.YS_URL);
                getActivity().startActivity(intent);
                break;
            case R.id.tv_more_secret:
                intent.putExtra(Constants.KEY_FRAGMENT, Constants.WEB_FRAGMENT);
                intent.putExtra(Constants.KEY_URL, Constants.USER_SERVE_URL);
                getActivity().startActivity(intent);
                break;
            // 检查更新
            case R.id.tv_accord_update:
                SPUtils.getInstance(context).put(Constants.ISUSERCLICK, true);
                AppUpdateUtil.updateApp(Constants.BASE_CONFIG_INFO, getActivity());
                break;
            default:
                break;
        }
    }
}
