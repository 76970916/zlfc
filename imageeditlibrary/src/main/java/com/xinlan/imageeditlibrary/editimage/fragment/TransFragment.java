package com.xinlan.imageeditlibrary.editimage.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pixplicity.sharp.OnSvgElementListener;
import com.pixplicity.sharp.Sharp;
import com.pixplicity.sharp.SharpPicture;
import com.qmuiteam.qmui.alpha.QMUIAlphaFrameLayout;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.R2;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.EditData;
import com.xinlan.imageeditlibrary.editimage.bean.LogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.MessageEvent;
import com.xinlan.imageeditlibrary.editimage.bean.SvgParm;
import com.xinlan.imageeditlibrary.editimage.dialog.ColorSelectDialog;
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;
import com.xinlan.imageeditlibrary.editimage.utils.CommUtils;
import com.xinlan.imageeditlibrary.editimage.view.Constants;
import com.xinlan.imageeditlibrary.editimage.view.StickerItem;
import com.xinlan.imageeditlibrary.editimage.view.StickerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_CROP;
import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_NONE;

/**
 * @Description: 设置透明度 和 svg图标颜色
 * @Author: lixh
 * @CreateDate: 2020/11/10 15:22
 * @Version: 1.0
 */

public class TransFragment extends BaseEditFragment implements SeekBar.OnSeekBarChangeListener {
    @BindView(R2.id.trans_value_bar)
    SeekBar mValueBar;
    @BindView(R2.id.temp_color)
    RecyclerView temp_color;
    @BindView(R2.id.line_template)
    LinearLayout line_template;
    @BindView(R2.id.line_template_color)
    LinearLayout line_template_color;
    @BindView(R2.id.recycle_temps)
    RecyclerView recycle_temps;
    @BindView(R2.id.temp_exit)
    ImageView temp_exit;
    @BindView(R2.id.temp_submit)
    ImageView temp_submit;

    List<Integer> listColors = new ArrayList<>();
    List<Integer> colorList = new ArrayList<>();
    StickerItem selectItem = null;
    Sharp mSharp;
    private int colorPosition = -1;
    private int changecolorPosition = -1;
    private int photoPosition = -1;
    StickerView stickerView;
    LogoBean logoBean;
    String svgContent = null;
    BaseQuickAdapter colorsAdapter = null;

    public static TransFragment newInstance() {
        TransFragment fragment = new TransFragment();
        return fragment;
    }

    @Override
    public void initUI() {
        mValueBar.setProgress(255);
        EventBus.getDefault().register(this);
        inittemplate();
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_edit_image_trans;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (activity.getMainBit() == null) {
            ToastUtils.showShort("请添加图片");
        } else {
            activity.isExit = true;
            if (selectItem.isSvg()) {
                int value = mValueBar.getProgress();
                selectItem.setmAlpha(value);
                reloadSvg(mSharp, true, colorPosition, -1, selectItem);
            } else {
                EditData data = new EditData();
                Bitmap bitmap = BitmapUtils.getTransparentBitmap(activity.getMainBit(), mValueBar.getProgress());
                data.setType(ConstantLogo.TRANS);
                float value = mValueBar.getProgress() / 100f;
                data.setTrans(value);
                data.setBitmap(bitmap);
                EventBus.getDefault().post(data);
            }
        }
    }

    /**
     * @Author lixh
     * @Date 2020/10/29 20:34
     * @Description: 初始化模板数据
     */
    private void inittemplate() {
        if (ObjectUtils.isNotEmpty(selectItem) && selectItem.isSvg()) {
            line_template.setVisibility(View.VISIBLE);
            line_template_color.setVisibility(View.GONE);
            GridLayoutManager layoutManagerColors = new GridLayoutManager(activity, 4);
            layoutManagerColors.setOrientation(RecyclerView.VERTICAL);
            temp_color.setLayoutManager(layoutManagerColors);
            colorsAdapter = new BaseQuickAdapter<Integer, BaseViewHolder>(R.layout.item_image_color, listColors) {
                @SuppressLint("ResourceAsColor")
                @Override
                protected void convert(BaseViewHolder holder, Integer color) {
                    // 选择颜色
                    FloatingActionButton iv_color = holder.getView(R.id.iv_color);
                    ImageView iv_temp_color = holder.getView(R.id.iv_temp_color);
                    iv_temp_color.setVisibility(View.GONE);
                    iv_color.setVisibility(View.GONE);
                    iv_temp_color.setVisibility(View.VISIBLE);
                    iv_temp_color.setBackgroundColor(color);
                    iv_temp_color.setOnClickListener(v -> {
                        line_template_color.setVisibility(View.VISIBLE);
                        line_template.setVisibility(View.GONE);
                        colorPosition = holder.getAdapterPosition();
                    });
                }
            };
            colorsAdapter.bindToRecyclerView(temp_color);
        }
        // 退出选择颜色
        temp_exit.setOnClickListener(v -> {
            line_template_color.setVisibility(View.GONE);
            line_template.setVisibility(View.VISIBLE);
            reloadSvg(mSharp, true, -1, -1, selectItem);
        });
        // 保存颜色
        temp_submit.setOnClickListener(v -> {
            // 之前的颜色
            int intColor = listColors.get(colorPosition);
            String hexColor = String.format("#%06X", (0xFFFFFF & intColor));
            String lowerhexColor = String.format("#%06x", (0xFFFFFF & intColor));
            // 选择的颜色
            int changeColor = colorList.get(changecolorPosition);
            String hexchangeColor = String.format("#%06X", (0xFFFFFF & changeColor));
            String lowerchange = String.format("#%06x", (0xFFFFFF & changeColor));
            svgContent = logoBean.getSvgContent();
            svgContent = svgContent.replace(hexColor, hexchangeColor);
            svgContent = svgContent.replace(lowerhexColor, lowerchange);

            listColors.set(colorPosition, changeColor);
            // 传递svg 修改之后的内容
            EditData data = new EditData();
            data.setSvgContent(svgContent);
            data.setType(ConstantLogo.SVG_CONTENT);
            EventBus.getDefault().post(data);
            logoBean.setSvgContent(svgContent);
            // 隐藏控件
            line_template_color.setVisibility(View.GONE);
            line_template.setVisibility(View.VISIBLE);
            colorsAdapter.setNewData(listColors);
            activity.isExit = true;
        });
        colorList.clear();
        CommUtils.getColors(getActivity(), colorList);
        GridLayoutManager layoutManager = new GridLayoutManager(activity, 6);
        recycle_temps.setLayoutManager(layoutManager);
        BaseQuickAdapter adapterTemp = new BaseQuickAdapter<Integer, BaseViewHolder>(R.layout.item_color_image, colorList) {
            @Override
            protected void convert(BaseViewHolder holder, Integer item) {
                holder.setIsRecyclable(false);
                FrameLayout imageView = holder.getView(R.id.color_frame);
                imageView.setBackgroundColor(item);
                RelativeLayout addLayout = holder.getView(R.id.relative_add);
                QMUIAlphaFrameLayout layout = holder.getView(R.id.color_frame);
                if (holder.getAdapterPosition() == colorList.size() - 1) {
                    layout.setVisibility(View.GONE);
                    addLayout.setVisibility(View.VISIBLE);
                }
                // 自定义颜色选择
                addLayout.setOnClickListener((View v) -> {
                    ColorSelectDialog colorSelectDialog = new ColorSelectDialog(activity, getResources().getColor(R.color.green), "颜色自定义");
                    colorSelectDialog.setAlphaSliderVisible(true);
                    colorSelectDialog.setHexValueEnabled(true);
                    colorSelectDialog.setOnColorListener((int color) -> {
                        changecolorPosition = holder.getAdapterPosition();
                        if (ObjectUtils.isNotEmpty(svgContent)) {
                            Sharp sharp = Sharp.loadString(svgContent);
                            mSharp = sharp;
                        }
                        activity.isExit = true;
                        colorList.add(colorList.size() - 1, color);
                        this.setNewData(colorList);
                        reloadSvg(mSharp, true, colorPosition, color, selectItem);
                    });
                    colorSelectDialog.show();
                });
                imageView.setOnClickListener((View v) -> {
                    changecolorPosition = holder.getAdapterPosition();
                    activity.isExit = true;
                    if (ObjectUtils.isNotEmpty(svgContent)) {
                        Sharp sharp = Sharp.loadString(svgContent);
                        mSharp = sharp;
                    }
                    reloadSvg(mSharp, true, colorPosition, item, selectItem);
                });
            }
        };
        recycle_temps.setAdapter(adapterTemp);
    }

    private void reloadSvg(Sharp sharp, final boolean changeColor, int position, Integer color, StickerItem stickerItem) {
        sharp.setOnElementListener(new OnSvgElementListener() {

            @Override
            public void onSvgStart(@NonNull Canvas canvas,
                                   @Nullable RectF bounds) {
            }

            @Override
            public void onSvgEnd(@NonNull Canvas canvas,
                                 @Nullable RectF bounds) {
            }

            @SuppressLint("ResourceType")
            @Override
            public <T> T onSvgElement(@Nullable String id,
                                      @NonNull T element,
                                      @Nullable RectF elementBounds,
                                      @NonNull Canvas canvas,
                                      @Nullable RectF canvasBounds,
                                      @Nullable Paint paint) {
                if (changeColor && paint != null && paint.getStyle() == Paint.Style.FILL) {
                    if (position != -1) {
                        if (paint.getColor() == listColors.get(position)) {
                            paint.setColor(color);
                            if (element instanceof Path) {
                                canvas.drawPath((Path) element, paint);
                            }
                        }
                    }
                }
                if (paint != null && selectItem.getmAlpha() != -1 && paint.getStyle() == Paint.Style.FILL) {
                    paint.setAlpha(selectItem.getmAlpha());
                    if (element instanceof Path) {
                        canvas.drawPath((Path) element, paint);
                    }
                }
                return element;
            }

            @Override
            public <T> void onSvgElementDrawn(@Nullable String id,
                                              @NonNull T element,
                                              @NonNull Canvas canvas,
                                              @Nullable Paint paint) {
            }

        });
        sharp.getSharpPicture((SharpPicture picture) -> {
                    Drawable drawable = sharp.getDrawable();
                    Bitmap bitmap = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        bitmap = CommUtils.getBitmapFromDrawable(activity, drawable, stickerItem);
                    }
                    // 如果是替换颜色
                    // 就覆盖HashMap 里面的对象并且保持bitmap
                    // 宽高不变化
                    if (changeColor) {
                        stickerView.replaceBitmap(bitmap, 102, stickerItem, stickerItem.percentHeight, stickerItem.percentWith);
                    } else {
                        stickerView.addBitImage(bitmap, 102, null, stickerItem.percentHeight, stickerItem.percentWith);
                    }
                }
        );
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mValueBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onShow() {
        activity.mode = MODE_CROP;
        activity.mainImage.setImageBitmap(activity.getMainBit());
    }

    @Override
    public void backToMain() {
        mValueBar.setProgress(255);
        activity.mode = MODE_NONE;
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void Event(MessageEvent event) {
        String message = event.getMessage();
        if (ObjectUtils.equals(message, Constants.SVG_DATA)) {
            listColors.clear();
            SvgParm parm = (SvgParm) event.getObj();
            if (ObjectUtils.isEmpty(parm)) {
                return;
            }
            mSharp = parm.getmSharp();
            selectItem = parm.getSelectItem();
            listColors = parm.getListColors();
            stickerView = parm.getStickerView();
            logoBean = parm.getLogoBean();
            inittemplate();
        } else if (ObjectUtils.equals(message, Constants.CLEAR_SVG_DATA)) {
            listColors.clear();
            colorsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 解除所有粘性事件
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
//        listColors.clear();
    }
}
