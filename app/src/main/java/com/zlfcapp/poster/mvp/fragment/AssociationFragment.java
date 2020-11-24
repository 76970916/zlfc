package com.zlfcapp.poster.mvp.fragment;

import android.app.Dialog;
import android.os.Bundle;
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

public class AssociationFragment extends BaseFragment<IHomeView, HomePresenter> implements IHomeView {
    @BindView(R.id.text_tab)
    TextView textTab;
    @BindView(R.id.recycler_viewpager)
    RecyclerView recyclerViewpager;
    BaseQuickAdapter adapter;
    String type = "节日";
    String getTypename;
    Dialog tipDialog;
    List<ImageResult> mainList = new ArrayList<>();
    @Override
    public int getRootViewId() {
        return R.layout.viewpagerfragment;
    }

    public static AssociationFragment newInstance() {
        Bundle args = new Bundle();
        AssociationFragment fragment = new AssociationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initUI() {

    }
    @Override
    public void queryImgList(List<ImageResult> list) {
        mainList = list;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void initData() {
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
//        recyclerViewpager = rootView.findViewById(R.id.recycler_viewpager);
//        text_tab = rootView.findViewById(R.id.text_tab);
//        text_tab.setText(type);
        recyclerViewpager.setLayoutManager(manager);
        adapter = new BaseQuickAdapter<ImageResult, BaseViewHolder>(R.layout.list_simple_temp, mainList) {
            @Override
            protected void convert(BaseViewHolder holder, ImageResult item) {
                holder.setIsRecyclable(false);
                ImageView item_logo = holder.getView(R.id.item_logo);
                if (getActivity() != null) {
                    Glide.with(getActivity()).load(item.getUrl()).into(item_logo);
                }
            }
        };
        recyclerViewpager.setAdapter(adapter);
        queryOnlieData();
    }
    private void queryOnlieData() {
        String device_id = CommonUtils.getDevice_id();
        Map<String, Object> mapImage = produceReqArg.generateObj(device_id);
        mapImage.put("device_id", device_id);
        mapImage.put("type", "节日");        // 查询背景图片素材
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
