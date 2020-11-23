package com.xinlan.imageeditlibrary.editimage.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.R2;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.EditData;
import com.xinlan.imageeditlibrary.editimage.view.Constants;
import com.xinlan.imageeditlibrary.viewpager.ViewPagerFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CanvasLargerActivity extends BaseActivity {
    @BindView(R2.id.back_btn)
    ImageView backBtn;
    @BindView(R2.id.queren_btn)
    ImageView querenBtn;
    @BindView(R2.id.btn_work)
    ImageView btnWork;
    @BindView(R2.id.image_update)
    ImageView imageUpdate;
    @BindView(R2.id.relative_canvas)
    RelativeLayout relativeCanvas;
    @BindView(R2.id.viewpager_canvas)
    ViewPager viewpagerCanvas;
    @BindView(R2.id.text_pixel)
    TextView textPixel;
    private Unbinder mUnbinder;
    int key = 0;
    int imageHeight;
    int imageWith;
    RelativeLayout.LayoutParams params;
    List<ImageData> dataList;
    ViewPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<>();
    DisplayMetrics metrics;
    int code = 2;
    int percentWith = 100;
    int percentHeight = 100;
    int maxHeight;
    int maxWith;
    int layer;
    boolean ret =true;
    int position;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvaslarger);
        mUnbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setStatusViewColor(Color.parseColor("#333333"));
        layer = getIntent().getIntExtra("layer", 0);
        metrics = getResources().getDisplayMetrics();
        dataList = new ArrayList<>();
        key = getIntent().getIntExtra(Constants.LOGO_ID, 0);
        imageWith = metrics.widthPixels;
        imageHeight = metrics.heightPixels;
        maxHeight = imageHeight;
        maxWith = imageWith;
        params = (RelativeLayout.LayoutParams) relativeCanvas.getLayoutParams();
        imageWith = (int) (metrics.widthPixels * 0.8);
        imageHeight = (int) (imageWith * 1.9);
        relativeCanvas.setLayoutParams(params);
        querenBtn.setOnClickListener(v -> {
            startEdit();

        });
        backBtn.setOnClickListener(v -> {
            finish();
        });
        newFragment();
        initViewPager();
        relativeCanvas.setOnClickListener(v -> {
            startEdit();
        });
    }

    private void startEdit() {
        if (layer == 0) {
            Intent intent = new Intent(CanvasLargerActivity.this, EditImageActivity.class);
            intent.putExtra("imageHeight", imageHeight);
            intent.putExtra("imageWith", imageWith);
            intent.putExtra("largerCode", code);
            intent.putExtra("percentWith", percentWith);
            intent.putExtra("percentHeight", percentHeight);
            startActivity(intent);
            finish();
        } else {
            EditData data = new EditData();
            data.setType(ConstantLogo.ADD_CANVAS);
            data.setHeight(imageHeight);
            data.setWith(imageWith);
            data.setCode(code);
            EventBus.getDefault().post(data);
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(EditData data) {
        if (data.getType() == ConstantLogo.SELECT_VIEW) {
            switch (data.getPosition()) {
                case 1:
                    //1080x1080
                    imageWith = metrics.widthPixels;
                    imageHeight = metrics.heightPixels;
                    params.width = (int) (metrics.widthPixels * 0.7);
                    params.height = (int) (metrics.widthPixels * 0.7);
                    relativeCanvas.setLayoutParams(params);
                    textPixel.setText("1080x1080像素");
                    code = 1;
                    percentHeight = 100;
                    percentWith = 100;
                    position = 1;
                    break;
                case 2:
                    //1080x1920
                    code = 2;
                    imageWith = (int) (metrics.widthPixels * 0.8);
                    imageHeight = (int) (imageWith * 1.9);
                    params.width = imageWith;
                    params.height = imageHeight;
                    relativeCanvas.setLayoutParams(params);
                    textPixel.setText("1080x1920像素");
                    percentHeight = (int) (((float) imageHeight / (float) maxHeight) * 100);
                    percentWith = (int) (((float) imageWith / (float) maxWith) * 100);
                    code = 0;
                    position = 2;
                    break;
                case 3:
                    //500x500
                    imageWith = metrics.widthPixels / 2;
                    imageHeight = imageWith;
                    params.width = imageWith;
                    params.height = imageHeight;
                    relativeCanvas.setLayoutParams(params);
                    textPixel.setText("500x500像素");
                    code = 0;
                    percentHeight = (int) (((float) imageHeight / (float) maxHeight) * 100);
                    percentWith = (int) (((float) imageWith / (float) maxWith) * 100);
                    position = 3;
                    break;
                case 4:
                    //735x1102
                    imageWith = (int) ((metrics.widthPixels * 0.8) * 0.735f);
                    imageHeight = (int) (imageWith * 1.5);
                    params.width = imageWith;
                    params.height = imageHeight;
                    relativeCanvas.setLayoutParams(params);
                    textPixel.setText("735x1102像素");
                    code = 0;
                    percentHeight = (int) (((float) imageHeight / (float) maxHeight) * 100);
                    percentWith = (int) (((float) imageWith / (float) maxWith) * 100);
                    position = 4;
                    break;
                case 5:
                    //940x788
                    imageWith = (int) ((metrics.widthPixels * 0.8f) * 0.9f);
                    imageHeight = (int) (imageWith * 0.8);
                    params.width = imageWith;
                    params.height = imageHeight;
                    relativeCanvas.setLayoutParams(params);
                    textPixel.setText("940x788像素");
                    code = 0;
                    percentHeight = (int) (((float) imageHeight / (float) maxHeight) * 100);
                    percentWith = (int) (((float) imageWith / (float) maxWith) * 100);
                    //
                    position = 5;
                    break;
            }
        }
    }

    private void newFragment() {
        mFragments.add(ViewPagerFragment.newInstance());
        mFragments.add(ViewPagerFragment.newInstance());
        mFragments.add(ViewPagerFragment.newInstance());
        mFragments.add(ViewPagerFragment.newInstance());
    }


    private void initViewPager() {
        viewpagerCanvas = (ViewPager) findViewById(R.id.viewpager_canvas);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewpagerCanvas.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        EventBus.getDefault().unregister(this);
    }

    class ImageData {
        int height;
        int with;
        String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWith() {
            return with;
        }

        public void setWith(int with) {
            this.with = with;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

}
