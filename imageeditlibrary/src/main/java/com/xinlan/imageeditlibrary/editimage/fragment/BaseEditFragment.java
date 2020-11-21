package com.xinlan.imageeditlibrary.editimage.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xinlan.imageeditlibrary.editimage.EditImageActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by panyi on 2017/3/28.
 */

public abstract class BaseEditFragment extends Fragment {
    protected EditImageActivity activity;
    private Unbinder mUnbinder;
    private Context context;
    private View rootView;

    protected EditImageActivity ensureEditActivity(){
        if(activity==null){
            activity = (EditImageActivity)getActivity();
        }
        return activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        rootView = inflater.inflate(getRootViewId(), container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        initUI();
        return rootView;
    }
    public abstract void initUI();
    public abstract int getRootViewId();
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ensureEditActivity();
    }

    public abstract void onShow();

    public abstract void backToMain();
}//end class
