package com.xinlan.imageeditlibrary.editimage.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.bumptech.glide.Glide;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.utils.CommUtils;
import com.xinlan.imageeditlibrary.editimage.view.Constants;

import java.io.File;


/**
 * @Author lixh
 * @Date 2020/11/9 14:38
 * @Description: 保存页面
 */
public class ImagePreviewActivity extends Activity {
    Toolbar toolbar;
    ImageView imageView;
    Button btn_share;
    Button btn_success;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.imagePreview);
        btn_share = findViewById(R.id.btn_share);
        btn_success = findViewById(R.id.btn_success);
        toolbar.setTitle("图片预览");
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.black));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        String path = getIntent().getStringExtra(Constants.IMAGE_PATH);
        if (ObjectUtils.isNotEmpty(path)) {
            Glide.with(imageView).load(path).into(imageView);
        }
        // 分享图片
        btn_share.setOnClickListener(v -> {
            if (ObjectUtils.isNotEmpty(path)) {
                File file = new File(path);
                CommUtils.shareFile(this, file);
            }
        });
        // 结束页面
        btn_success.setOnClickListener(v -> {
            // 销毁之前的activity
            CommUtils.removeActivity("EditImageActivity");
            finish();
        });
    }
}
