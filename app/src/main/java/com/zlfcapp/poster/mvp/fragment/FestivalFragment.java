package com.zlfcapp.poster.mvp.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.bean.ImageResult;
import com.xinlan.imageeditlibrary.editimage.bean.LogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.LogoTemplate;
import com.xinlan.imageeditlibrary.editimage.bean.LowerListBean;
import com.xinlan.imageeditlibrary.editimage.bean.TypeFace;
import com.xinlan.imageeditlibrary.editimage.view.Constants;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.mvp.base.BaseFragment;
import com.zlfcapp.poster.mvp.presenter.HomePresenter;
import com.zlfcapp.poster.mvp.view.IHomeView;
import com.zlfcapp.poster.utils.CommonUtils;
import com.zlfcapp.poster.utils.produceReqArg;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class FestivalFragment extends BaseFragment<IHomeView, HomePresenter> implements IHomeView {
    @BindView(R.id.recycler_viewpager)
    RecyclerView recyclerViewpager;
    BaseQuickAdapter adapter;
    String type = "节日";
    String getTypename;
    Dialog tipDialog;
    List<ImageResult> mainList = new ArrayList<>();

    public static FestivalFragment newInstance() {
        Bundle args = new Bundle();
        FestivalFragment fragment = new FestivalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getRootViewId() {
        return R.layout.viewpagerfragment;
    }

    @Override
    public void initUI() {

    }

    @Override
    public void queryFont(List<TypeFace> list) {

    }

    @Override
    public void queryFontId(String url) {

    }

    @Override
    public void queryImgList(List<ImageResult> list) {
        mainList = list;
        initRecycler();
    }

    private void initRecycler() {
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

    @Override
    public void queryLowerList(List<LowerListBean> list, String imageUrl, int l_id) {
        if (list != null) {
            LitePal.deleteAll(LogoBean.class, "fid=?", String.valueOf(l_id));
        }
        for (LowerListBean bean : list) {
            LogoBean logoBean = new LogoBean();
            logoBean.setFid(l_id);
            logoBean.setLeftRect(bean.getLayout_x());
            logoBean.setTopRect(bean.getLayout_y());
            logoBean.setType(bean.getType());
            logoBean.setmAlpha(255);
            logoBean.setText(bean.getText());
            logoBean.setTextColor(bean.getTextColor());
            logoBean.setTextSize(bean.getTextsize());
            logoBean.setTextfontid(bean.getTextfontid());
            boolean save = logoBean.save();
            Log.d("LogoBeansave", String.valueOf(save));
        }
        if (list != null) {
            Intent intent = new Intent(getActivity(), EditImageActivity.class);
            intent.putExtra(Constants.POSTER_ID, l_id);
            intent.putExtra("imageUrl", imageUrl);
            startActivity(intent);
        }

    }

    @Override
    public void queryAllTemplate(List<LogoTemplate> list) {

    }

    @Override
    public void initData() {
        initRecycler();
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
