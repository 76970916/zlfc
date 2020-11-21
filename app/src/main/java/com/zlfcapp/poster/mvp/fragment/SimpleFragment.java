package com.zlfcapp.poster.mvp.fragment;


import com.zlfcapp.poster.mvp.base.BaseFragment;
import com.zlfcapp.poster.mvp.base.BasePresenter;
import com.zlfcapp.poster.mvp.base.BaseView;


/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/10
 */

public abstract class SimpleFragment extends BaseFragment<BaseView,BasePresenter<BaseView>> {

    @Override
    public BasePresenter<BaseView> createPresenter() {
        return new BasePresenter<>(getApp());
    }
}
