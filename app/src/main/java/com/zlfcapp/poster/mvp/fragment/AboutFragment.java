package com.zlfcapp.poster.mvp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.baidu.mobstat.StatService;
import com.google.android.material.appbar.AppBarLayout;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.mvp.base.BaseFragment;
import com.zlfcapp.poster.mvp.base.BasePresenter;
import com.zlfcapp.poster.mvp.base.BaseView;
import com.zlfcapp.poster.utils.SPUtils;
import com.zlfcapp.poster.utils.updateapp.AppUpdateUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ZhangBx on 2018/4/30.
 */
public class AboutFragment extends BaseFragment<BaseView, BasePresenter<BaseView>> implements BaseView {
    Unbinder unbinder;
    @BindView(R.id.about_tv_ver)
    TextView mTvVer;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.tv_about_copyright)
    TextView mTvAboutCopyright;
    @BindView(R.id.appbar)
    AppBarLayout appbar;

    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void showProgress() {
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_about;
    }

    @Override
    public void initUI() {
        setSupportActionBarBackgroup(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initData() {
        mTvVer.setText(getContext().getString(R.string.app_name) + "\n Version " + getApp().getVer());
        mTvAboutCopyright.setText("Copyright © 2020-2025 \n张量方程 版权所有");
    }

    @Override
    public BasePresenter<BaseView> createPresenter() {
        return new BasePresenter<>(getApp());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_accord_update, R.id.tv_feedback, R.id.tv_about_protocol, R.id.tv_about_secret})
    public void onViewClicked(View view) {
        Intent intent = getContentActivityIntent();
        switch (view.getId()) {
            case R.id.tv_accord_update:
                SPUtils.getInstance(context).put(Constants.ISUSERCLICK, true);
                Map<String, String> attributes = new HashMap<>();
                attributes.put("type", "home");
                StatService.onEvent(getActivity(), "click_update", "无", 1, attributes);
                AppUpdateUtil.updateApp(Constants.BASE_CONFIG_INFO, getActivity());
                break;
            case R.id.tv_feedback:
                intent.putExtra(Constants.KEY_FRAGMENT, Constants.FEEDBACK_FRAGMENT);
                getActivity().startActivity(intent);
                break;
            // 隐私协议
            case R.id.tv_about_protocol:
                intent.putExtra(Constants.KEY_FRAGMENT, Constants.WEB_FRAGMENT);
                intent.putExtra(Constants.KEY_URL, Constants.YS_URL);
                getActivity().startActivity(intent);
                break;
            // 用户服务
            case R.id.tv_about_secret:
                intent.putExtra(Constants.KEY_FRAGMENT, Constants.WEB_FRAGMENT);
                intent.putExtra(Constants.KEY_URL, Constants.USER_SERVE_URL);
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
    }
}
