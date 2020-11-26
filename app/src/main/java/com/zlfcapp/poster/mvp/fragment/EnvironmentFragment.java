package com.zlfcapp.poster.mvp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinlan.imageeditlibrary.editimage.bean.ImageResult;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.mvp.base.BaseFragment;
import com.zlfcapp.poster.mvp.presenter.HomePresenter;
import com.zlfcapp.poster.mvp.view.IHomeView;
import com.zlfcapp.poster.utils.CommonUtils;
import com.zlfcapp.poster.utils.produceReqArg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class EnvironmentFragment extends BaseFragment<IHomeView, HomePresenter> implements IHomeView {
    @BindView(R.id.text_tab)
    TextView textTab;
    @BindView(R.id.recycler_viewpager)
    RecyclerView recyclerViewpager;
    BaseQuickAdapter adapter;
    List<ImageResult> mainList = new ArrayList<>();
    @Override
    public int getRootViewId() {
        return R.layout.viewpagerfragment;
    }
    public static EnvironmentFragment newInstance() {
        Bundle args = new Bundle();
        EnvironmentFragment fragment = new EnvironmentFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void initUI() {

    }

    @Override
    public void initData() {
        initRecycler();
        queryOnlieData();
    }
    @Override
    public void queryImgList(List<ImageResult> list) {
        mainList = list;
        initRecycler();
    }

    private void initRecycler() {
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        recyclerViewpager.setLayoutManager(manager);
        adapter = new BaseQuickAdapter<ImageResult, BaseViewHolder>(R.layout.list_simple_temp, mainList) {
            @Override
            protected void convert(BaseViewHolder holder, ImageResult item) {
                holder.setIsRecyclable(false);
                ImageView item_logo = holder.getView(R.id.item_logo);
                if (getActivity() != null) {
                    Glide.with(getActivity()).load(item.getUrl()).into(item_logo);
                }
                item_logo.setOnClickListener(v -> {
                    String device_id = CommonUtils.getDevice_id();
                    Map<String, Object> map = produceReqArg.generateObj(device_id);
                    map.put("device_id", device_id);
                    map.put("id", item.getId());
                    getPresenter().lowerList(map);
                });
            }
        };
        recyclerViewpager.setAdapter(adapter);
    }
    private void queryOnlieData() {
        String device_id = CommonUtils.getDevice_id();
        Map<String, Object> mapImage = produceReqArg.generateObj(device_id);
        mapImage.put("device_id", device_id);
        mapImage.put("type", "环保");        // 查询背景图片素材
        getPresenter().subQueryImgList(mapImage);
    }
    @Override
    public HomePresenter createPresenter() {
        return new HomePresenter(getApp());
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
}
