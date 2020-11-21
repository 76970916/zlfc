package com.xinlan.imageeditlibrary.editimage.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.R2;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.EditData;
import com.xinlan.imageeditlibrary.editimage.model.RatioItem;
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;
import com.xinlan.imageeditlibrary.editimage.utils.FileUtil;
import com.xinlan.imageeditlibrary.editimage.utils.Matrix3;
import com.xinlan.imageeditlibrary.editimage.utils.PxUtil;
import com.xinlan.imageeditlibrary.editimage.view.CropImageView;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouch;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CropActivity extends BaseActivity {
    @BindView(R2.id.imageTouch)
    ImageViewTouch imageTouch;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.crop_view)
    CropImageView cropView;
    @BindView(R2.id.image_queren)
    ImageView imageQueren;
    @BindView(R2.id.crop_refresh)
    QMUILoadingView cropRefresh;
    private Unbinder mUnbinder;
    String path;
    Bitmap mBitmap;
    private int imageWidth, imageHeight;// 展示图片控件 宽 高
    LoadImageTask mLoadImageTask;
    int imagePosition;
    int listPosition;
    boolean netWork = false;
    Bitmap bitmap;
    Handler mHandler;
    int bitmapWith;
    int bitmapHeight;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        mUnbinder = ButterKnife.bind(this);
        setStatusViewColor(Color.parseColor("#333333"));
        toolbar.setBackgroundColor(Color.parseColor("#333333"));
        EventBus.getDefault().register(this);
        toolbar.setTitle("裁剪");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initHandler();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels / 2;
        imageHeight = metrics.heightPixels / 2;
      int  toolBarHeight = PxUtil.dip2px(CropActivity.this, 62f);
        imageHeight = metrics.heightPixels - toolBarHeight;
        imageWidth = metrics.widthPixels;
        mLoadImageTask = new LoadImageTask();
        path = getIntent().getStringExtra("path");
        netWork = getIntent().getBooleanExtra("NetWork", false);
        listPosition = getIntent().getIntExtra("listPosition", 0);
        bitmapWith = getIntent().getIntExtra("bitmapWith", 0);
        bitmapHeight = getIntent().getIntExtra("bitmapHeight", 0);
//        Bitmap bitmap=  BitmapUtils.getSampledBitmap(path, imageWidth,
//                imageHeight);
        cropRefresh.setVisibility(View.VISIBLE);
        imageTouch.post(new Runnable() {
            @Override
            public void run() {
                //            imageTouch.setImageBitmap(mBitmap);
//            imageTouch.setImageBitmap(mBitmap);
                Message message = new Message();
                message.what = 0;
                mHandler.sendMessage(message);
            }
        });
        imageQueren.setOnClickListener(v -> {
            applyCropImage();
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(EditData data) {
        switch (data.getType()) {
            case ConstantLogo.ADD_CROP:
                mBitmap = data.getBitmap();
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 0:
                        if (netWork) {
                            //网络图片
                            BitmapUtils.returnUrlBitMap(path, mHandler);
                        } else {
                            //本地图片
                            bitmap = FileUtil.getBitmapForFile(path);
                            bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);
                            initImage();
                            mLoadImageTask.execute(path);
                        }
                        break;
                    case 3:
                        //网络图片
                        bitmap = (Bitmap) msg.obj;
                        bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);
                        initImage();
                        mLoadImageTask.execute(path);
                        break;
                }
            }
        };
    }

    public void initImage() {
        String filePath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filePath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/"
                    + FileUtil.getPackageName(CropActivity.this) + "/appList";
        } else {
            filePath = "/storage/emulated/0/Android/data/" + FileUtil.getPackageName(CropActivity.this) + "/appList";
        }
        File fileList = new File(filePath);
        if (!fileList.exists()) {
            fileList.mkdir();
        }
        File file = new File(filePath + "/" + "crop" + ".png");
        path = file.getAbsolutePath();
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCrop() {
        int height;
        int with;
        int toolBarHeight;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        toolBarHeight = PxUtil.dip2px(CropActivity.this, 62f);
        height = metrics.heightPixels - toolBarHeight;
        with = metrics.widthPixels;
        RectF rectF = new RectF();
        rectF.set(0, 0, with, height);
        RatioItem dataItem = new RatioItem("none", -1f);
        cropView.setRatioCropRect(rectF,
                dataItem.getRatio(), with, height);
        cropView.setVisibility(View.VISIBLE);
        imageTouch.setScaleEnabled(false);// 禁用缩放
        // System.out.println(r.left + "    " + r.top);
        //  bug  fixed  https://github.com/siwangqishiq/ImageEditor-Android/issues/59
        // 设置完与屏幕匹配的尺寸  确保变换矩阵设置生效后才设置裁剪区域
        imageTouch.post(new Runnable() {
            @Override
            public void run() {
                int with = imageTouch.getMeasuredWidth();
                int height = imageTouch.getMeasuredHeight();
//                final RectF r = imageTouch.getBitmapRect();
                RectF r = new RectF();
                int df_with = metrics.widthPixels / 2;
                int top_height = metrics.heightPixels / 2 - df_with-toolBarHeight;
                int left_with = metrics.widthPixels / 2 - df_with;
                int bottom_height = metrics.heightPixels / 2 + df_with;
                int right_with = metrics.widthPixels / 2 + df_with;
                r.set(left_with, top_height, right_with, bottom_height);
                cropView.setImageRect(rectF);
                cropView.setCropRect(r);
                cropRefresh.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 导入文件图片任务
     */
    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @SuppressLint("WrongThread")
        @Override
        protected Bitmap doInBackground(String... params) {

            return BitmapUtils.getSampledBitmap(params[0], imageWidth,
                    imageHeight);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mBitmap = result;
            Glide.with(CropActivity.this).load(mBitmap).into(imageTouch);
            imageTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            initCrop();
        }
    }// end inner class
    // end inner class
//        else {
//            mainImage.setImageBitmap(mainBitmap);
//        }
//        mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        EventBus.getDefault().unregister(this);
    }

    public void applyCropImage() {
        // System.out.println("保存剪切图片");
        CropImageTask task = new CropImageTask();
        task.execute(mBitmap);
    }

    /**
     * 图片剪裁生成 异步任务
     *
     * @author panyi
     */
    private final class CropImageTask extends AsyncTask<Bitmap, Void, Bitmap> {
        private Dialog dialog;

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = BaseActivity.getLoadingDialog(CropActivity.this, R.string.saving_image,
                    false);
            dialog.show();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            RectF cropRect = cropView.getCropRect();// 剪切区域矩形
            Matrix touchMatrix = imageTouch.getImageViewMatrix();
            // Canvas canvas = new Canvas(resultBit);
            float[] data = new float[9];
            touchMatrix.getValues(data);// 底部图片变化记录矩阵原始数据
            Matrix3 cal = new Matrix3(data);// 辅助矩阵计算类
            Matrix3 inverseMatrix = cal.inverseMatrix();// 计算逆矩阵
            Matrix m = new Matrix();
            m.setValues(inverseMatrix.getValues());
            m.mapRect(cropRect);// 变化剪切矩形
            // Paint paint = new Paint();
            // paint.setColor(Color.RED);
            // paint.setStrokeWidth(10);
            // canvas.drawRect(cropRect, paint);
            // Bitmap resultBit = Bitmap.createBitmap(params[0]).copy(
            // Bitmap.Config.ARGB_8888, true);
            Bitmap resultBit = Bitmap.createBitmap(params[0],
                    (int) cropRect.left, (int) cropRect.top,
                    bitmapWith, bitmapHeight);
            //saveBitmap(resultBit, activity.saveFilePath);
            return resultBit;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result == null)
                return;
//            activity.changeMainBitmap(result,true);
            //裁剪好的图片
            EditData data = new EditData();
            data.setType(ConstantLogo.CROP_IMAGE);
            data.setBitmap(result);
            data.setListPosition(listPosition);
            data.setImageUrl(path);
//            imageTouch.setImageBitmap(result);
            EventBus.getDefault().post(data);
            finish();
        }
    }// end inner class

}
