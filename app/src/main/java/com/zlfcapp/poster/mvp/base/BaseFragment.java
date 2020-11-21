package com.zlfcapp.poster.mvp.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.zlfcapp.poster.App;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.UserEvent;
import com.zlfcapp.poster.mvp.activity.ContentActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/20
 */

public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>> extends MvpFragment<V, P> {

    protected Context context;

    private View rootView;

    private Unbinder mUnbinder;

    public <T extends View> T findView(@IdRes int id) {
        return (T) rootView.findViewById(id);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getActivity();
        rootView = inflater.inflate(getRootViewId(), container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        if (getClass().isAnnotationPresent(UserEvent.class)) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }
//        StatusTitleUtil.setStateTitle(getActivity());
        initUI();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null)
            mUnbinder.unbind();
        if (getClass().isAnnotationPresent(UserEvent.class)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public View getRootView() {
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public void setSupportActionBarBackgroup(Toolbar toolbar) {
        toolbar.setBackgroundColor(ThemeUtils.getColorById(getActivity(), R.color.white));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    public ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }


    public void replaceFragment(@IdRes int id, Fragment fragment) {
        getFragmentManager().beginTransaction().replace(id, fragment).commit();
    }

    public void replaceChildFragment(@IdRes int id, Fragment fragment) {
        getChildFragmentManager().beginTransaction().replace(id, fragment).commit();
    }

    public App getApp() {
        return (App) getActivity().getApplication();
    }

    public <T> void toSetList(List<T> list, List<T> newList, boolean isMore) {

        if (list != null && newList != null) {
            synchronized (BaseFragment.class) {
                if (!isMore) {
                    list.clear();
                }
                list.addAll(newList);
            }
        }
    }


    public static void registerEvent(Object obj) {
        EventBus.getDefault().register(obj);
    }

    public static void unregisterEvent(Object obj) {
        EventBus.getDefault().unregister(obj);
    }

    public static void sendEvent(Object obj) {
        EventBus.getDefault().post(obj);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Object obj) {

    }

    //--------------------------------

    protected Intent getIntent() {
        return getActivity().getIntent();
    }


    protected Intent getFragmentIntent(int fragmentKey) {
        Intent intent = getContentActivityIntent();
        intent.putExtra(Constants.KEY_FRAGMENT, fragmentKey);
        return intent;
    }

    protected Intent getContentActivityIntent() {
        return new Intent(context, ContentActivity.class);
    }

    protected void startActivity(Class<?> cls) {
        startActivity(new Intent(context, cls));
    }


    protected void finish() {
        getActivity().finish();
    }


    protected void startWeb(String title, String url) {
        Intent intent = getFragmentIntent(Constants.WEB_FRAGMENT);
        intent.putExtra(Constants.KEY_TITLE, title);
        intent.putExtra(Constants.KEY_URL, url);
        startActivity(intent);
    }

    protected void startAbout() {
        Intent intent = getFragmentIntent(Constants.ABOUT_FRAGMENT);
        startActivity(intent);
    }

    protected void startFaq() {
        Intent intent = getFragmentIntent(Constants.FAQ_FRAGMENT);
        startActivity(intent);
    }

    protected void showTimeDialog() {

    }



    //--------------------------------


    public abstract int getRootViewId();

    public abstract void initUI();

    public abstract void initData();

    protected boolean isVisBottom(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1) {
            return true;
        } else {
            return false;
        }
    }


}
