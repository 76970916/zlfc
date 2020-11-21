package com.xinlan.imageeditlibrary.editimage.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huantansheng.easyphotos.EasyPhotos;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.R2;
import com.xinlan.imageeditlibrary.editimage.bean.AlbumInfo;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.EditData;
import com.xinlan.imageeditlibrary.editimage.utils.FileUtil;
import com.xinlan.imageeditlibrary.editimage.utils.GlideEngine;
import com.xinlan.imageeditlibrary.editimage.utils.PhotoHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class PhotoFragment extends BaseEditFragment {
    @BindView(R2.id.recycler_photo)
    RecyclerView recyclerPhoto;
    BaseQuickAdapter photoAdapter;
    List<AlbumInfo> infoList;
    Handler mHandler;
    int photoPosition = -1;
    Handler handler;
    int CAMERACODE = 001;
    String path;

    public static PhotoFragment newInstance() {
        PhotoFragment fragment = new PhotoFragment();
        return fragment;
    }

    @Override
    public void initUI() {
        EventBus.getDefault().register(this);
        infoList = new ArrayList<>();
        initHandler();
//        initAdapter();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPhotos(getActivity());
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        EditData data = new EditData();
                        data.setType(ConstantLogo.PUZZLE);
                        data.setImageUrl(path);
                        EventBus.getDefault().post(data);
                        break;
                    case 3:
                        infoList = (List<AlbumInfo>) msg.obj;
                        initAdapter();
                        break;
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(EditData data) {
        switch (data.getType()) {
            case ConstantLogo.CAMER:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getPhotos(getActivity());
                    }
                }).start();
                break;
        }
    }

    private void initAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false);
        recyclerPhoto.setLayoutManager(gridLayoutManager);
        photoAdapter = new BaseQuickAdapter<AlbumInfo, BaseViewHolder>(R.layout.item_photo, infoList) {
            @Override
            protected void convert(final BaseViewHolder holder, final AlbumInfo item) {
                ImageView imageView = holder.getView(R.id.list_photo);
                ImageView image_normal = holder.getView(R.id.normal_photo);
                RelativeLayout relativeLayout = holder.getView(R.id.relative_bg);
                holder.setIsRecyclable(false);
                RelativeLayout relative_normal = holder.getView(R.id.relatvie_normal);
                if (item.title.equals("拍照")) {
                    relative_normal.setVisibility(View.VISIBLE);
                    image_normal.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(getResources().getDrawable(R.drawable.paizhao)).into(image_normal);
                } else if (item.title.equals("拼图")) {
                    relative_normal.setVisibility(View.VISIBLE);
                    image_normal.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(getResources().getDrawable(R.drawable.pintu)).into(image_normal);
                }
                if (item.path != null) {
                    Glide.with(getActivity()).load(item.path).into(imageView);
                }
                relative_normal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.getAdapterPosition() == 0) {
                            EasyPhotos.createCamera(getActivity())//参数说明：上下文
                                    .setFileProviderAuthority("com.huantansheng.easyphotos.sample.fileprovider")//参数说明：见下方`FileProvider的配置`
                                    .start(CAMERACODE);
                        }

                    }
                });

                relativeLayout.setVisibility(View.GONE);
                if (photoPosition != -1) {
                    if (photoPosition == holder.getAdapterPosition()) {
                        relativeLayout.setVisibility(View.VISIBLE);
                    }
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoPosition = holder.getAdapterPosition();
                        EditData data = new EditData();
                        data.setType(ConstantLogo.GET_PHOTO);
                        data.setImageUrl(item.path);
                        data.setBitmap(FileUtil.getBitmapForFile(item.path));
                        EventBus.getDefault().post(data);
                        photoAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        recyclerPhoto.setAdapter(photoAdapter);
    }

    public void getPhotos(Context context) {
        List<AlbumInfo> list = new ArrayList<>();
        AlbumInfo albumInfo1 = new AlbumInfo();
        albumInfo1.title = "拍照";
        list.add(albumInfo1);
        String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " desc";
        String[] projection = new String[]{MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID, // 直接包含该图片文件的文件夹ID，防止在不同下的文件夹重名
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
                MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名
                MediaStore.Images.Media.DATA, // 图片绝对路径
                MediaStore.Images.Media.TITLE, //
        };
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, null, orderBy);

        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    AlbumInfo albumInfo = new AlbumInfo();
                    albumInfo.path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File file = new File(albumInfo.path);
                    if (file.exists() && file.length() > 0) {
                        albumInfo.folderId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                        albumInfo.folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                        albumInfo.fileId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                        albumInfo.imageName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        albumInfo.title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                        albumInfo.count = cursor.getInt(5);//该文件夹下一共有多少张图片
                        list.add(albumInfo);
                    }
                }
                Message message = new Message();
                message.what = 3;
                message.obj = list;
                mHandler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }


    /**
     * 图片拼图模式
     *
     * @author panyi
     */
    private void onRotateClick() {
        EasyPhotos.createAlbum(getActivity(), false, GlideEngine.getInstance())
                .setCount(4)
                .setPuzzleMenu(false)
                .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                .start(ConstantLogo.STARTPUZZLE);
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_photo;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void backToMain() {

    }
}
