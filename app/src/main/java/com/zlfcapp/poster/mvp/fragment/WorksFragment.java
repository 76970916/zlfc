package com.zlfcapp.poster.mvp.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinlan.imageeditlibrary.editimage.activity.CanvasLargerActivity;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.LogoBean;
import com.xinlan.imageeditlibrary.editimage.utils.FileUtil;
import com.zlfcapp.poster.App;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.mvp.base.BaseFragment;
import com.zlfcapp.poster.mvp.presenter.HomePresenter;
import com.zlfcapp.poster.mvp.view.IHomeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class WorksFragment extends BaseFragment<IHomeView, HomePresenter> implements IHomeView {
    public static final int REQUEST_PERMISSON_SORAGE = 1;
    public static final int REQUEST_PERMISSON_CAMERA = 2;
    public static final int SELECT_GALLERY_IMAGE_CODE = 7;
    public static final int TAKE_PHOTO_CODE = 8;
    public static final int ACTION_REQUEST_EDITIMAGE = 9;
    public static final int ACTION_STICKERS_IMAGE = 10;
    public static final int GET_PHOTO = 11;
    //    @BindView(R.id.btn_selectImage)
//    Button btnSelectImage;
    @BindView(R.id.recycler_image)
    RecyclerView recyclerImage;
    @BindView(R.id.relative_create)
    RelativeLayout relativeCreate;
    private Bitmap mainBitmap;
    private String path;
    List<LogoBean> beanList;
    private RequestManager mGlide;
    BaseQuickAdapter adapter;
    List<LogoBean> editList;
    Bitmap currentBitmap;

    @Override
    public int getRootViewId() {
        return R.layout.fragment_works;
    }

    @Override
    public void initUI() {
        editList = new ArrayList<>();
        mGlide = Glide.with(getActivity());
        beanList = new ArrayList<>();
        EventBus.getDefault().register(this);
        relativeCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //编辑之前选择画布大小

                editImageClick();
            }
        });
        IntentFilter filter = new IntentFilter();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerImage.setLayoutManager(layoutManager);
        adapter = new BaseQuickAdapter<LogoBean, BaseViewHolder>(R.layout.item_image, editList) {
            @Override
            protected void convert(BaseViewHolder holder, LogoBean item) {
                ImageView imageView = holder.getView(R.id.image_list);
                Glide.with(getActivity()).load(FileUtil.getBitmapForByte(item.getImage())).into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogoBean logoBean = new LogoBean();
                        logoBean.setBitmap(FileUtil.getBitmapForByte(item.getImage()));
                        logoBean.setType(ConstantLogo.SHOWVIEW);
                        logoBean.setPath(item.getPath());
                        EventBus.getDefault().post(logoBean);
                    }
                });
            }
        };
        initBitmap();

    }

    private void initBitmap() {
        editList.clear();
        editList.addAll(LitePal.findAll(LogoBean.class));
        recyclerImage.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(LogoBean logoBean) {
        switch (logoBean.getType()) {
            case ConstantLogo.GET_PHOTO:
                path = logoBean.getPath();
                break;
            case ConstantLogo.UPDATE_PHOTO:
                beanList.clear();
                beanList = LitePal.findAll(LogoBean.class);
                if (beanList != null) {
                    if (beanList.size() > 0) {
                        path = beanList.get(0).getPath();
                    }
                }
                currentBitmap = logoBean.getBitmap();
                editList.clear();
                editList.addAll(LitePal.findAll(LogoBean.class));
                adapter.notifyDataSetChanged();
                break;
        }

    }

    //    /**
//     * 编辑选择的图片
//     *
//     * @author panyi
//     */
    private void editImageClick() {
        Intent it = new Intent(context, CanvasLargerActivity.class);
        startActivityForResult(it, ACTION_REQUEST_EDITIMAGE);
    }


    @Override
    public void initData() {
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
    public HomePresenter createPresenter() {
        return new HomePresenter(App.getApp());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}
