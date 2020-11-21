package com.xinlan.imageeditlibrary.editimage.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.PuzzleCallback;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.bean.AlbumInfo;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.EditData;
import com.xinlan.imageeditlibrary.editimage.utils.FileUtil;
import com.xinlan.imageeditlibrary.editimage.utils.GlideEngine;
import com.xinlan.imageeditlibrary.editimage.utils.PhotoHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 工具栏主菜单
 *
 * @author panyi
 */
public class MainMenuFragment extends BaseEditFragment implements View.OnClickListener {
    public static final int INDEX = ModuleConfig.INDEX_MAIN;

    public static final String TAG = MainMenuFragment.class.getName();
    private View mainView;

    private TextView stickerBtn;// 贴图按钮
    private TextView fliterBtn;// 滤镜按钮
    private TextView cropBtn;// 剪裁按钮
    private TextView rotateBtn;// 旋转按钮
    private TextView mTextBtn;//文字型贴图添加
    private TextView mPaintBtn;//编辑按钮
    private TextView mBeautyBtn;//美颜按钮
    private TextView mPhoto;//相册按钮
    //    private View mTrans;//透明度按钮
    String path;
    List<TextView> viewList;

    public static MainMenuFragment newInstance() {
        MainMenuFragment fragment = new MainMenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_main_menu,
                null);
        return mainView;
    }

    @Override
    public void initUI() {

    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_edit_image_main_menu;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        stickerBtn = mainView.findViewById(R.id.btn_stickers);
        fliterBtn = mainView.findViewById(R.id.btn_filter);
        cropBtn = mainView.findViewById(R.id.btn_crop);
        rotateBtn = mainView.findViewById(R.id.btn_rotate);
        mTextBtn = mainView.findViewById(R.id.btn_text);
        mPaintBtn = mainView.findViewById(R.id.btn_paint);
        mBeautyBtn = mainView.findViewById(R.id.btn_beauty);
        mPhoto = mainView.findViewById(R.id.btn_photo);
//        mTrans = mainView.findViewById(R.id.btn_trans);
        viewList = new ArrayList<>();
        viewList.add(fliterBtn);
        viewList.add(cropBtn);
        viewList.add(rotateBtn);
        viewList.add(mPhoto);
        viewList.add(mBeautyBtn);
        viewList.add(mPaintBtn);
//        stickerBtn.setOnClickListener(this);
        fliterBtn.setOnClickListener(this);
        cropBtn.setOnClickListener(this);
        rotateBtn.setOnClickListener(this);
        mTextBtn.setOnClickListener(this);
        mPaintBtn.setOnClickListener(this);
        mBeautyBtn.setOnClickListener(this);
        mPhoto.setOnClickListener(this);
//        mTrans.setOnClickListener(this);
    }

    @Override
    public void onShow() {
        // do nothing
    }

    @Override
    public void backToMain() {
        //do nothing
    }

    @SuppressLint("ResourceAsColor")
    public void setViewTextColor(int id) {
        for (int i = 0; i < viewList.size(); i++) {
            if (viewList.get(i).getId() == id) {
                viewList.get(i).setTextColor(R.color.white);
            } else {
                viewList.get(i).setTextColor(R.color.text_select);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (activity.getMainBit() == null) {
            if (v == mPhoto) {
//                onPhotoClick();
            } else if (v == rotateBtn) {
                onRotateClick();
            } else {
                ToastUtils.showShort("请选择一张图片编辑");
            }

        } else {
            setViewTextColor(v.getId());
            if (v == fliterBtn) {
//                  activity.recyclerPhoto.setVisibility(View.GONE);
                onFilterClick();
            } else if (v == mPaintBtn) {
//                  activity.recyclerPhoto.setVisibility(View.GONE);
                onPaintClick();
            } else if (v == mBeautyBtn) {
//                  activity.recyclerPhoto.setVisibility(View.GONE);
                onBeautyClick();
            } else if (v == mPhoto) {
//                  activity.recyclerPhoto.setVisibility(View.VISIBLE);
//                onPhotoClick();
            } else if (v == cropBtn) {
//                  activity.recyclerPhoto.setVisibility(View.GONE);
                onCropClick();
            } else if (v == rotateBtn) {
//                  activity.recyclerPhoto.setVisibility(View.GONE);
                onRotateClick();
            } else if (v == mTextBtn) {
//                  activity.recyclerPhoto.setVisibility(View.GONE);
                onAddTextClick();
            }
//            if (v == stickerBtn) {
//                onStickClick();
//            }
        }

    }


    private SelectCallback callback = new SelectCallback() {
        @Override
        public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
            path = photos.get(0).path;
            EditData data = new EditData();
            data.setImageUrl(path);
            data.setType(ConstantLogo.ADDIMAGE);
            EventBus.getDefault().post(data);
        }
    };

//    private void onPhotoClick() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (getActivity() != null) {
//                    List<AlbumInfo> list = PhotoHelper.getPhotos(getActivity());
//                    EditData data = new EditData();
//                    data.setList(list);
//                    data.setType(ConstantLogo.ADDIMAGE);
//                    EventBus.getDefault().post(data);
//                }
//            }
//        }).start();

//        EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())//参数说明：上下文，是否显示相机按钮，[配置Glide为图片加载引擎](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)
//                .setFileProviderAuthority("com.huantansheng.easyphotos.sample.fileprovider")//参数说明：见下方`FileProvider的配置`
//                .start(callback);
//    }

    /**
     * 贴图模式
     *
     * @author panyi
     */
    private void onStickClick() {
//        activity.bottomGallery.setCurrentItem(StickerFragment.INDEX);
        activity.mStickerFragment.onShow();
    }

    /**
     * 滤镜模式
     *
     * @author panyi
     */
    private void onFilterClick() {
//        activity.bottomGallery.setCurrentItem(FilterListFragment.INDEX);
        activity.mFilterListFragment.onShow();
    }

    /**
     * 裁剪模式
     *
     * @author panyi
     */
    private void onCropClick() {
//        activity.bottomGallery.setCurrentItem(TransFragment.INDEX);
        activity.mTransFragment.onShow();
    }

    /**
     * 图片拼图模式
     *
     * @author panyi
     */
    private void onRotateClick() {
//        activity.bottomGallery.setCurrentItem(RotateFragment.INDEX);
//        activity.mRotateFragment.onShow();
        String path;
        path = FileUtil.getImageFolderPath(getActivity());
        final File fileList = new File(path);
        if (!fileList.exists()) {
            fileList.mkdir();
        }
        final File file = new File(path + "/" + System.currentTimeMillis() + ".png");

        EasyPhotos.createAlbum(this, false, GlideEngine.getInstance())
                .setCount(9)
                .setPuzzleMenu(false)
                .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                        EasyPhotos.startPuzzleWithPhotos(getActivity(), photos, Environment.getExternalStorageDirectory().getAbsolutePath(), "AlbumBuilder", false, GlideEngine.getInstance(), new PuzzleCallback() {
                            @Override
                            public void onResult(Photo photo) {
                                String path = photo.path;
                                EditData data = new EditData();
                                data.setType(ConstantLogo.PUZZLE);
                                data.setImageUrl(path);
                                EventBus.getDefault().post(data);
                            }
                        });
                    }
                });

    }

    /**
     * 插入文字模式
     *
     * @author panyi
     */
    private void onAddTextClick() {
//        activity.bottomGallery.setCurrentItem(AddTextFragment.INDEX);
        activity.mAddTextFragment.onShow();
    }

    /**
     * 自由绘制模式
     */
    private void onPaintClick() {
//        activity.bottomGallery.setCurrentItem(PaintFragment.INDEX);
        activity.mPaintFragment.onShow();
    }

    private void onBeautyClick() {
//        activity.bottomGallery.setCurrentItem(BeautyFragment.INDEX);
    }

}// end class
