package com.xinlan.imageeditlibrary.editimage.fragment;


import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.huantansheng.easyphotos.EasyPhotos;
import com.qmuiteam.qmui.alpha.QMUIAlphaFrameLayout;
import com.vector.update_app.HttpManager;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.R2;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.EditData;
import com.xinlan.imageeditlibrary.editimage.bean.ImageLogo;
import com.xinlan.imageeditlibrary.editimage.bean.ImgList;
import com.xinlan.imageeditlibrary.editimage.bean.Result;
import com.xinlan.imageeditlibrary.editimage.dialog.ColorSelectDialog;
import com.xinlan.imageeditlibrary.editimage.utils.GlideEngine;
import com.xinlan.imageeditlibrary.editimage.utils.NetWorkHelper;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class ColorFragment extends BaseEditFragment {
    @BindView(R2.id.recycler_color)
    RecyclerView recyclerColor;

    @BindView(R2.id.recycler_images)
    RecyclerView recycler_images;

    BaseQuickAdapter adapter;
    BaseQuickAdapter colorsAdapter;
    List<Integer> colorList;
    //    @BindView(R2.id.relatvie_normal)
//    RelativeLayout relatvieNormal;
    private int photoPosition = -1;
    List<ImageLogo> imageLogoList = new ArrayList<>();
    private static boolean hasMore = true; // 是否有下一页
    private static int currentPage;
    // 若是上拉加载更多的网络请求 则不需要删除数据
    private boolean isLoadingMore = false;
    // 最后一个条目位置
    private static int lastVisibleItem = 0;
    GridLayoutManager layoutManager = null;
    GridLayoutManager mLayoutManager = null;

    public static final int ADD_COLOR = 10086102;
    public static final int DEFAULT_COLOR = 10086101;

    @Override
    public void initUI() {
        colorList = new ArrayList<>();
        colorList.add(getResources().getColor(R.color.materialcolorpicker__red));
        colorList.add(getResources().getColor(R.color.materialcolorpicker__green));
        colorList.add(getResources().getColor(R.color.materialcolorpicker__blue));
        colorList.add(getResources().getColor(R.color.materialcolorpicker__dialogcolor));
        colorList.add(getResources().getColor(R.color.purple_backup));
        colorList.add(getResources().getColor(R.color.orange_backup));
        colorList.add(getResources().getColor(R.color.gray_cc));
        colorList.add(getResources().getColor(R.color.whitefa));
        colorList.add(DEFAULT_COLOR);
        colorList.add(ADD_COLOR);
        layoutManager = new GridLayoutManager(getActivity(), 6);
        recyclerColor.setLayoutManager(layoutManager);
        initAdapter();
        initImages();
        currentPage = 1;
        loadingMore();
        // 初始currentPage为1
    }

    /**
     * @Author lixh
     * @Date 2020/10/22 16:10
     * @Description: 加载中
     */
    private void loadingMore() {
        recycler_images.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (!isLoadingMore) {        // 若不是加载更多 才 加载
                // 在newState为滑到底部时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 如果没有隐藏footView，那么最后一个条目的位置(带数据）就比我们的getItemCount少1
                    if (lastVisibleItem + 1 == colorsAdapter.getItemCount()) {
                        // 然后调用updateRecyclerview方法更新RecyclerView
                        updateRecyclerView();
                    }
                    // 如果隐藏了提示条，我们又上拉加载时，那么最后一个条目(带数据）就要比getItemCount要少2
                    if (lastVisibleItem + 2 == colorsAdapter.getItemCount()) {
                        // 然后调用updateRecyclerview方法更新RecyclerView
                        updateRecyclerView();    // 要调
                    }
                    // 如果隐藏了提示条，我们又上拉加载时，那么最后一个条目(带数据）就要比getItemCount要少2
                    if (lastVisibleItem + 3 == colorsAdapter.getItemCount()) {
                        // 然后调用updateRecyclerview方法更新RecyclerView
                        updateRecyclerView();    // 要调
                    }
                    // 如果隐藏了提示条，我们又上拉加载时，那么最后一个条目(带数据）就要比getItemCount要少2
                    if (lastVisibleItem + 4 == colorsAdapter.getItemCount()) {
                        // 然后调用updateRecyclerview方法更新RecyclerView
                        updateRecyclerView();    // 要调
                    }
                }
//                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 在滑动完成后，拿到最后一个可见的item的位置
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    // 上拉加载时调用的更新RecyclerView的方法
    private void updateRecyclerView() {
        if (hasMore) {
            // 还有下一页 网络请求 第二页 第三页
            currentPage++;    // 加1
            isLoadingMore = true;
            String device_id = AddTextFragment.getDevice_id(getActivity());
            Map<String, String> map = AddTextFragment.generate(device_id);
            map.put("device_id", device_id);
            map.put("page", "" + currentPage);
            map.put("limit", "20");
            String url = "http://battery.zlfc.mobi/api/admin/font/imgList";
            NetWorkHelper.getInstance().asyncPost(url, map, new HttpManager.Callback() {
                @Override
                public void onResponse(String result) {
                    Gson gson = new Gson();
                    Result result1 = gson.fromJson(result, Result.class);
                    String data = (String) result1.getData();
                    ImgList imgList = gson.fromJson(data, ImgList.class);
                    imageLogoList.addAll(imgList.getResults());
                    colorsAdapter.notifyLoadMoreToLoading();
                }

                @Override
                public void onError(String error) {

                }
            });
        }
    }

    private void initImages() {
        imageLogoList = LitePal.findAll(ImageLogo.class);
        ImageLogo imageLogo = new ImageLogo();
        imageLogo.setId(0);
        imageLogoList.add(0, imageLogo);
        mLayoutManager = new GridLayoutManager(getActivity(), 4);
        recycler_images.setLayoutManager(mLayoutManager);
        colorsAdapter = new BaseQuickAdapter<ImageLogo, BaseViewHolder>(R.layout.item_image_bg, imageLogoList) {
            @Override
            protected void convert(BaseViewHolder holder, ImageLogo item) {
                // 选择颜色
                ImageView iv_logo = holder.getView(R.id.iv_logo);
                ImageView iv_add_bg = holder.getView(R.id.iv_add_bg);
                if (item.getId() == 0) {
                    iv_logo.setVisibility(View.GONE);
                    iv_add_bg.setVisibility(View.VISIBLE);
                } else {
                    iv_logo.setVisibility(View.VISIBLE);
                    iv_add_bg.setVisibility(View.GONE);
                    // 展示缩略图
                    Glide.with(mContext).load(item.getThumb_url()).into(iv_logo);
                    if (item.isSelected()) {
                        iv_add_bg.setBackground(mContext.getResources().getDrawable(R.drawable.item_radiu));
                    } else {
                        // 空背景
                        iv_add_bg.setBackground(mContext.getResources().getDrawable(R.drawable.item_radis_teans));
                    }
                }
                holder.addOnClickListener(R.id.iv_logo);
                holder.addOnClickListener(R.id.iv_add_bg);
            }
        };
        colorsAdapter.setOnItemChildClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            ImageLogo item = imageLogoList.get(position);
            int id = view.getId();
            if (id == R.id.iv_add_bg) {
                //参数说明：上下文，是否显示相机按钮，[配置Glide为图片加载引擎]
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EasyPhotos.createAlbum(getActivity(), true, GlideEngine.getInstance())
                                .setFileProviderAuthority("com.huantansheng.easyphotos.sample.fileprovider")
                                //参数说明：见下方`FileProvider的配置`
                                .start(ConstantLogo.COLORPHOTO);
                    }
                }).start();
            } else if (id == R.id.iv_logo) {
                // 点击选择背景图片
                activity.imageSpace.setVisibility(View.VISIBLE);
                item.setSelected(true);
                Glide.with(getActivity()).load(item.getUrl()).into(activity.imageSpace);
                EditData data = new EditData();
                data.setImageUrl(item.getUrl());
                data.setType(ConstantLogo.COLORPHOTO);
                EventBus.getDefault().post(data);
                adapter.notifyDataSetChanged();
            }
        });
        colorsAdapter.bindToRecyclerView(recycler_images);
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<Integer, BaseViewHolder>(R.layout.item_color_image, colorList) {
            @Override
            protected void convert(BaseViewHolder holder, Integer item) {
                holder.setIsRecyclable(false);
                FrameLayout imageView = holder.getView(R.id.color_frame);
                imageView.setBackgroundColor(item);
                RelativeLayout relativeLayout = holder.getView(R.id.relative_bg);
                RelativeLayout addLayout = holder.getView(R.id.relative_add);
                QMUIAlphaFrameLayout layout = holder.getView(R.id.color_frame);
                RelativeLayout relative_default = holder.getView(R.id.relative_default);
                if (item == DEFAULT_COLOR) {
                    relative_default.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.GONE);
                    addLayout.setVisibility(View.GONE);
                }
                if (item == ADD_COLOR) {
                    layout.setVisibility(View.GONE);
                    addLayout.setVisibility(View.VISIBLE);
                }
                addLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ColorSelectDialog colorSelectDialog = new ColorSelectDialog(getActivity(), getResources().getColor(R.color.green), "颜色自定义");
                        colorSelectDialog.setAlphaSliderVisible(true);
                        colorSelectDialog.setHexValueEnabled(true);
                        colorSelectDialog.setOnColorListener(new ColorSelectDialog.OnColorPickerListener() {
                            @Override
                            public void commitColorClick(int color) {
                                activity.imageSpace.setVisibility(View.VISIBLE);
                                if (getActivity() != null) {
                                    Glide.with(getActivity()).clear(activity.imageSpace);
                                }
                                activity.imageSpace.setBackgroundColor(color);
                                activity.isExit = true;
                                EditData data = new EditData();
                                data.setType(ConstantLogo.COLOR);
                                data.setColor(color);
                                data.setImageUrl(String.valueOf(color));
                                EventBus.getDefault().post(data);
                            }
                        });
                        colorSelectDialog.show();
                    }
                });
                relative_default.setOnClickListener(v -> {
                    activity.imageSpace.setVisibility(View.VISIBLE);
                    int colors = 5025616;
                    activity.imageSpace.setBackgroundColor(colors);
                    activity.isExit = true;
                    EditData data = new EditData();
                    data.setType(ConstantLogo.COLOR);
                    data.setColor(colors);
                    data.setImageUrl(String.valueOf(colors));
                    EventBus.getDefault().post(data);
                });
                if (photoPosition != -1) {
                    if (photoPosition == holder.getAdapterPosition()) {
                        relativeLayout.setVisibility(View.VISIBLE);
                    } else {
                        relativeLayout.setVisibility(View.GONE);
                    }
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoPosition = holder.getAdapterPosition();
                        activity.isExit = true;
                        EditData data = new EditData();
                        data.setType(ConstantLogo.COLOR);
                        data.setColor(item);
                        data.setImageUrl(String.valueOf(item));
                        EventBus.getDefault().post(data);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };
        recyclerColor.setAdapter(adapter);
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_color;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void backToMain() {

    }
}
