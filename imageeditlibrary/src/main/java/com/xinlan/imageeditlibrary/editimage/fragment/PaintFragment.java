package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.R2;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.adapter.ColorListAdapter;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.EditData;
import com.xinlan.imageeditlibrary.editimage.task.StickerTask;
import com.xinlan.imageeditlibrary.editimage.ui.ColorPicker;
import com.xinlan.imageeditlibrary.editimage.view.CustomPaintView;
import com.xinlan.imageeditlibrary.editimage.view.PaintModeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.xinlan.imageeditlibrary.editimage.view.Constants.MODE_PAINT;


/**
 * 用户自由绘制模式 操作面板
 * 可设置画笔粗细 画笔颜色
 * custom draw mode panel
 *
 * @author panyi
 */
public class PaintFragment extends BaseEditFragment implements View.OnClickListener, ColorListAdapter.IColorListAction {
    public static final int INDEX = ModuleConfig.INDEX_PAINT;
    public static final String TAG = PaintFragment.class.getName();
    @BindView(R2.id.paint_thumb)
    PaintModeView mPaintModeView;
    @BindView(R2.id.paint_color_list)
    RecyclerView mColorListView;
    @BindView(R2.id.paint_eraser)
    ImageView mEraserView;
    @BindView(R2.id.paint_value_bar)
    SeekBar paintValueBar;
    @BindView(R2.id.paint_cancel)
    TextView paintCancel;
    private ColorListAdapter mColorAdapter;
    private View popView;
    public CustomPaintView mPaintView;
    private ColorPicker mColorPicker;//颜色选择器
    private PopupWindow setStokenWidthWindow;
    private SeekBar mStokenWidthSeekBar;
    public boolean isEraser = false;//是否是擦除模式
    private SaveCustomPaintTask mSavePaintImageTask;
    private List<Bitmap> listBitmap;
    private int paintPosition;
    public int[] mPaintColors = {
            Color.DKGRAY, Color.GRAY, Color.LTGRAY, Color.WHITE,
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};

    public static PaintFragment newInstance() {
        PaintFragment fragment = new PaintFragment();
        return fragment;
    }

    @Override
    public void initUI() {
        EventBus.getDefault().register(this);
        listBitmap = new ArrayList<>();
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_edit_paint;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mColorPicker = new ColorPicker(getActivity(), 255, 0, 0);
        initColorListView();
        mPaintModeView.setOnClickListener(this);
        initStokeWidthPopWindow();
        mEraserView.setOnClickListener(this);
        updateEraserView();
        mPaintView.setWidth(40f);
        paintValueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float value = (float) seekBar.getProgress();
                mPaintView.setWidth(value);
            }
        });
        mPaintView.setOnTouchListener(new CustomPaintView.OnTouchListener() {
            @Override
            public void TouchUpClick() {

            }

            @Override
            public void paintClick(Bitmap bitmap) {
//                View view = (View) activity.imagePaint.getParent();
//                Bitmap paintBitmap = BitmapUtils.createBitmapFromView(view);
//                mPaintView.setVisibility(View.VISIBLE);
//                activity.imageSpace.setVisibility(View.GONE);
//                listBitmap.add(paintBitmap);
//                LinkedHashMap<Integer, StickerItem> bank = activity.mainImage.getBank();
//                for (Map.Entry<Integer, StickerItem> entryAll : bank.entrySet()){
//                    if (paintPosition == entryAll.getKey()) {
//                        StickerItem stickerItem = entryAll.getValue();
//                    }
//
//                }
//
//                mPaintView.setVisibility(View.GONE);
            }
        });
        paintCancel.setOnClickListener(v -> {
            CustomPaintView.isCanvas = false;
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(EditData data) {
        switch (data.getType()) {
            case ConstantLogo.PAINT:
                CustomPaintView.isCanvas = true;
                mPaintView.setVisibility(View.VISIBLE);
                paintPosition = data.getPosition();
//                if (activity.getMainBit() != null) {
//                    activity.imagePaint.setImageBitmap(activity.getMainBit());
////                    activity.mPaintView.getBitmap(activity.getMainBit());
//                }
//                activity.lastBtn.setOnClickListener(v -> {
//
//                });
//                activity.qianjinBtn.setOnClickListener(v -> {
//                });
                break;
            case ConstantLogo.UNPAINT:
                CustomPaintView.isCanvas = false;
//                savePaintImage();
                break;
            case ConstantLogo.PAINTRETURN:

                break;
        }
    }

    /**
     * 初始化颜色列表
     */
    private void initColorListView() {
        mColorListView.setHasFixedSize(false);
        GridLayoutManager layoutManager = new GridLayoutManager(activity, 4);
        mColorListView.setLayoutManager(layoutManager);
        mColorAdapter = new ColorListAdapter(getActivity(), this, mPaintColors, this);
        mColorListView.setAdapter(mColorAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v == mPaintModeView) {//设置绘制画笔粗细
            setStokeWidth();
        } else if (v == mEraserView) {
            toggleEraserView();
        }//end if
    }

    /**
     * 返回主菜单
     */
    @Override
    public void backToMain() {

    }

    public void onShow() {
        activity.mode = MODE_PAINT;
        activity.imageSpace.setVisibility(View.VISIBLE);
        Glide.with(getActivity()).load(activity.getMainBit()).into(activity.imageSpace);
        this.mPaintView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onColorSelected(int position, int color) {
        setPaintColor(color);
        mPaintView.setWidth(paintValueBar.getProgress());
        CustomPaintView.isCanvas = true;
    }

    @Override
    public void onMoreSelected(int position) {
        mColorPicker.show();
        Button okColor = (Button) mColorPicker.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPaintColor(mColorPicker.getColor());
                mColorPicker.dismiss();
                CustomPaintView.isCanvas = true;
            }
        });
    }

    @Override
    public void onColorFree(int Color) {
        setPaintColor(Color);
        mPaintView.setWidth(paintValueBar.getProgress());
    }

    /**
     * 设置画笔颜色
     *
     * @param paintColor
     */
    protected void setPaintColor(final int paintColor) {
        mPaintModeView.setPaintStrokeColor(paintColor);
        updatePaintView();
    }

    private void updatePaintView() {
        isEraser = false;
        updateEraserView();
        this.mPaintView.setColor(mPaintModeView.getStokenColor());
        this.mPaintView.setWidth(mPaintModeView.getStokenWidth());
    }

    /**
     * 设置画笔粗细
     * show popwidnow to set paint width
     */
    protected void setStokeWidth() {
        if (popView.getMeasuredHeight() == 0) {
            popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }

        mStokenWidthSeekBar.setMax(mPaintModeView.getMeasuredHeight());

        mStokenWidthSeekBar.setProgress((int) mPaintModeView.getStokenWidth());

        mStokenWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPaintModeView.setPaintStrokeWidth(progress);
                updatePaintView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        int[] locations = new int[2];
    }

    private void initStokeWidthPopWindow() {
        popView = LayoutInflater.from(activity).
                inflate(R.layout.view_set_stoke_width, null);
        setStokenWidthWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        mStokenWidthSeekBar = (SeekBar) popView.findViewById(R.id.stoke_width_seekbar);
        setStokenWidthWindow.setFocusable(true);
        setStokenWidthWindow.setOutsideTouchable(true);
        setStokenWidthWindow.setBackgroundDrawable(new BitmapDrawable());
        setStokenWidthWindow.setAnimationStyle(R.style.popwin_anim_style);
        mPaintModeView.setPaintStrokeColor(Color.RED);
        mPaintModeView.setPaintStrokeWidth(10);
        updatePaintView();
    }

    private void toggleEraserView() {
        isEraser = !isEraser;
        updateEraserView();
    }

    private void updateEraserView() {
        mEraserView.setImageResource(isEraser ? R.drawable.eraser_seleced : R.drawable.eraser_normal);
        mPaintView.setEraser(isEraser);
    }

    /**
     * 保存涂鸦
     */
    public void savePaintImage() {
        if (mSavePaintImageTask != null && !mSavePaintImageTask.isCancelled()) {
            mSavePaintImageTask.cancel(true);
        }
        mSavePaintImageTask = new SaveCustomPaintTask(activity);
        mSavePaintImageTask.execute(activity.getMainBit());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSavePaintImageTask != null && !mSavePaintImageTask.isCancelled()) {
            mSavePaintImageTask.cancel(true);
        }
        EventBus.getDefault().unregister(this);
    }

    /**
     * 文字合成任务
     * 合成最终图片
     */
    private final class SaveCustomPaintTask extends StickerTask {

        public SaveCustomPaintTask(EditImageActivity activity) {
            super(activity);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {
            float[] f = new float[9];
            m.getValues(f);
            int dx = (int) f[Matrix.MTRANS_X];
            int dy = (int) f[Matrix.MTRANS_Y];
            float scale_x = f[Matrix.MSCALE_X];
            float scale_y = f[Matrix.MSCALE_Y];
            canvas.save();
            canvas.translate(dx, dy);
            canvas.scale(scale_x, scale_y);
            if (mPaintView.getPaintBit() != null) {
                canvas.drawBitmap(mPaintView.getPaintBit(), 0, 0, null);
            }
            canvas.restore();
        }

        @Override
        public void onPostResult(Bitmap result) {
            mPaintView.reset();
            activity.changeMainBitmap(result, true);
//            backToMain();
        }
    }//end inner class


}// end class
