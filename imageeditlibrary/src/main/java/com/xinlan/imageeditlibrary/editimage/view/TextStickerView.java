package com.xinlan.imageeditlibrary.editimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ObjectUtils;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.utils.RectUtil;

/**
 * 文本贴图处理控件
 */
public class TextStickerView extends View {
    public final int TEXT_SIZE_DEFAULT = getResources().getDimensionPixelSize(R.dimen.fontsize_default);
    //    public final int PADDING = getResources().getDimensionPixelSize(R.dimen.font_padding);
    public final int PADDING = 32;
    //    public final int STICKER_BTN_HALF_SIZE = getResources().getDimensionPixelSize(R.dimen.sticker_btn_half_size);
    public final int STICKER_BTN_HALF_SIZE = 30;
    private String mText = getResources().getString(R.string.input_hint); //给贴图文本赋个初始值
    private TextPaint mPaint = new TextPaint();
    private Paint debugPaint = new Paint();
    private Paint mHelpPaint = new Paint();

    public Rect mTextRect = new Rect();// warp text rect record
    private RectF mHelpBoxRect = new RectF();
    private Rect mDeleteRect = new Rect();//删除按钮位置
    private Rect mRotateRect = new Rect();//旋转按钮位置

    private RectF mDeleteDstRect = new RectF();
    private RectF mRotateDstRect = new RectF();

    private Bitmap mDeleteBitmap;
    private Bitmap mRotateBitmap;

    private int mCurrentMode = IDLE_MODE;
    //控件的几种模式
    private static final int IDLE_MODE = 2;//正常
    private static final int MOVE_MODE = 3;//移动模式
    private static final int ROTATE_MODE = 4;//旋转模式
    private static final int DELETE_MODE = 5;//删除模式

    private EditText mEditText;//输入控件

    public int layout_x = 0;
    public int layout_y = 0;

    private float last_x = 0;
    private float last_y = 0;

    public float mRotateAngle;
    public float mScale = 1;
    private boolean isInitLayout = true;

    private boolean isShowHelpBox = true;
    private float dx;
    private float dy;
    private long currentTime;

    //设置字体
    private Typeface type;

    //设置画笔的透明度
    private int mAlpha = 255;

    private int fontId;

    private int id;

    private int siteX;

    private int siteY;

    private float lineSpacing;

    private boolean textEm;

    public int getSiteX() {
        return siteX;
    }

    public void setSiteX(int siteX) {
        this.siteX = siteX;
    }

    public int getSiteY() {
        return siteY;
    }

    public void setSiteY(int siteY) {
        this.siteY = siteY;
    }

    // 取消OnTouchEvent事件
    private boolean TouchEnable;

    public boolean isTouchEnable() {
        return TouchEnable;
    }

    public void setTouchEnable(boolean touchEnable) {
        TouchEnable = touchEnable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFontId() {
        return fontId;
    }

    public void setFontId(int fontId) {
        this.fontId = fontId;
    }

    public TextStickerView(Context context, Typeface type) {
        super(context);
        initView(context, type);
    }

    public TextStickerView(Context context, AttributeSet attrs, Typeface type) {
        super(context, attrs);
        initView(context, type);
    }

    public TextStickerView(Context context, AttributeSet attrs, int defStyleAttr, Typeface type) {
        super(context, attrs, defStyleAttr);
        initView(context, type);
    }

    public void setEditText(EditText textView) {
        this.mEditText = textView;

//        //设置特殊字体
//        mEditText.setTypeface(type);
    }

    private void initView(Context context, Typeface type) {
        debugPaint.setColor(Color.parseColor("#66ff0000"));
        mDeleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sticker_delete);
//        mRotateBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sticker_rotate);
//        new Double(logoBean.getLeftRect() * 0.38).intValue(), new Double(logoBean.getTopRect() * 0.47).intValue(),
        mDeleteRect.set(0, 0, mDeleteBitmap.getWidth(), mDeleteBitmap.getHeight());
//        mRotateRect.set(0, 0, mRotateBitmap.getWidth(), mDeleteBitmap.getHeight());
        mDeleteDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1, STICKER_BTN_HALF_SIZE << 1);
        mRotateDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1, STICKER_BTN_HALF_SIZE << 1);
        mPaint.setColor(Color.BLACK);
        if (null != type) {
            mPaint.setTypeface(type);
        }
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(TEXT_SIZE_DEFAULT);
        mPaint.setAntiAlias(true);
        //设置画笔的透明度
        mPaint.setAlpha(mAlpha);
        mHelpPaint.setColor(Color.RED);
        mHelpPaint.setStyle(Paint.Style.STROKE);
        mHelpPaint.setAntiAlias(true);
        mHelpPaint.setStrokeWidth(4);
    }

    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    public String getmText() {
        return mText;
    }

    public void setTextColor(int newColor) {
        mPaint.setColor(newColor);
        invalidate();
    }

    public void setTextSize(float size) {
        mPaint.setTextSize(size);
        invalidate();
    }

    public float getTextSize() {
        return mPaint.getTextSize();
    }


    public int getCurrentTextColor() {
        return mPaint.getColor();
    }

    public Typeface getType() {
        return type;
    }

    public void setType(Typeface type) {
        this.type = type;
        mPaint.setTypeface(type);
        invalidate();
    }

    public int getmAlpha() {
        return mAlpha;
    }

    public void setmAlpha(int mAlpha) {
        this.mAlpha = mAlpha;
        mPaint.setAlpha(mAlpha);
        invalidate();
    }

    /**
     * @Author lixh
     * @Date 2020/11/7 16:37
     * @Description: 设置字体间距
     */
    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
        mPaint.setLetterSpacing(lineSpacing);
        invalidate();
    }

    public float getLineSpacing() {
        return this.lineSpacing;
    }

    /**
     * @Author lixh
     * @Date 2020/11/7 17:16
     * @Description: 设置粗体
     */
    public void setTextBold(boolean isBoldText) {
        mPaint.setFakeBoldText(isBoldText);
        invalidate();
    }

    public boolean getTextBold() {
        return mPaint.isFakeBoldText();
    }

    /**
     * @Author lixh
     * @Date 2020/11/7 17:18
     * @Description: 设置斜体
     */
    public void setTextEm(boolean isEm) {
        textEm = isEm;
        if (isEm) {
            mPaint.setTextSkewX(0.5f);
        } else {
            mPaint.setTextSkewX(0f);
        }
        invalidate();
    }

    public boolean getTextEm() {
        return this.textEm;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isInitLayout) {
            isInitLayout = false;
            resetView();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(mText))
            return;

        drawContent(canvas);
    }

    private void drawContent(Canvas canvas) {
        drawText(canvas);

        //draw x and rotate button
        int offsetValue = ((int) mDeleteDstRect.width()) >> 1;
        mDeleteDstRect.offsetTo(mHelpBoxRect.left - offsetValue, mHelpBoxRect.top - offsetValue);
        mRotateDstRect.offsetTo(mHelpBoxRect.right - offsetValue, mHelpBoxRect.bottom - offsetValue);

        RectUtil.rotateRect(mDeleteDstRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);
        RectUtil.rotateRect(mRotateDstRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);

        if (!isShowHelpBox) {
            return;
        }

        canvas.save();
        canvas.rotate(mRotateAngle, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.drawRoundRect(mHelpBoxRect, 10, 10, mHelpPaint);
        canvas.restore();


        canvas.drawBitmap(mDeleteBitmap, mDeleteRect, mDeleteDstRect, null);
//        canvas.drawBitmap(mRotateBitmap, mRotateRect, mRotateDstRect, null);
        //canvas.drawRect(mRotateDstRect, debugPaint);
        //canvas.drawRect(mDeleteDstRect, debugPaint);
    }

    private void drawText(Canvas canvas) {
        drawText(canvas, layout_x, layout_y, mScale, mRotateAngle);
    }

    public void drawText(Canvas canvas, int _x, int _y, float scale, float rotate) {
        if (TextUtils.isEmpty(mText)) {
            return;
        }

        int x = _x;
        int y = _y;

        mPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        mTextRect.offset(x - (mTextRect.width() >> 1), y);

        mHelpBoxRect.set(mTextRect.left - PADDING, mTextRect.top - PADDING
                , mTextRect.right + PADDING, mTextRect.bottom + PADDING);
        RectUtil.scaleRect(mHelpBoxRect, scale);

        canvas.save();
        canvas.scale(scale, scale, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.rotate(rotate, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.drawText(mText, x, y, mPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isTouchEnable()) {
            return false;
        }
        boolean ret = super.onTouchEvent(event);// 是否向下传递事件标志 true为消耗

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mDeleteDstRect.contains(x, y)) {// 删除模式
                    isShowHelpBox = true;
                    mCurrentMode = DELETE_MODE;
                } else if (mRotateDstRect.contains(x, y)) {// 旋转按钮
                    isShowHelpBox = true;
                    mCurrentMode = MOVE_MODE;
                    last_x = mRotateDstRect.centerX();
                    last_y = mRotateDstRect.centerY();
                    ret = true;
                } else if (mHelpBoxRect.contains(x, y)) {// 移动模式 //或者编辑模式
                    isShowHelpBox = true;
                    mCurrentMode = MOVE_MODE;
                    last_x = x;
                    last_y = y;
                    ret = true;

                    currentTime = System.currentTimeMillis();

                } else {
                    isShowHelpBox = false;
                    invalidate();
                }// end if

                if (mCurrentMode == DELETE_MODE) {// 删除选定贴图
                    mCurrentMode = IDLE_MODE;// 返回空闲状态
                    clearTextContent();
                    invalidate();
                    mOnDeleteClickListener.onDeleteClick(this, id);
                }// end if
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                if (mCurrentMode == MOVE_MODE) {// 移动贴图
                    mCurrentMode = MOVE_MODE;
                    dx = x - last_x;
                    dy = y - last_y;
                    layout_x += dx;
                    layout_y += dy;
                    invalidate();
                    last_x = x;
                    last_y = y;
                } else if (mCurrentMode == ROTATE_MODE) {// 旋转 缩放文字操作
                    mCurrentMode = ROTATE_MODE;
                    dx = x - last_x;
                    dy = y - last_y;
                    updateRotateAndScale(dx, dy);
                    invalidate();
                    last_x = x;
                    last_y = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (ObjectUtils.isNotEmpty(mOnClickListener)) {
                    mOnClickListener.onClick(this);
                }
                //判断是否单击编辑框
                long moveTime = System.currentTimeMillis() - currentTime;//移动时间

                //判断是否继续传递信号
                if (moveTime <= 200 && (dx <= 20 || dy <= 20)) {
                    //点击事件，自己消费
//                    Toast.makeText(getContext(), "aaaaa",Toast.LENGTH_SHORT).show();
//                    isShowHelpBox = false;
//                    invalidate();
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onEditClick(this);
                    }
                    return true;
                }
                ret = false;
                mCurrentMode = IDLE_MODE;
                break;
        }
        return ret;
    }

    public void clearTextContent() {
        if (mEditText != null) {
            mEditText.setText(null);
        }
        //setText(null);
    }


    /**
     * 旋转 缩放 更新
     *
     * @param dx
     * @param dy
     */
    public void updateRotateAndScale(final float dx, final float dy) {
        float c_x = mHelpBoxRect.centerX();
        float c_y = mHelpBoxRect.centerY();

        float x = mRotateDstRect.centerX();
        float y = mRotateDstRect.centerY();

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        float scale = curLen / srcLen;// 计算缩放比

        mScale *= scale;
        float newWidth = mHelpBoxRect.width() * mScale;

        if (newWidth < 70) {
            mScale /= scale;
            return;
        }

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));
        float calMatrix = xa * yb - xb * ya;// 行列式计算 确定转动方向

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;
        mRotateAngle += angle;
    }

    public void resetView() {
        if (siteX != 0 && siteY != 0) {
            layout_x = siteX;
            layout_y = siteY;
        } else {
            layout_x = getMeasuredWidth() / 2;
            layout_y = getMeasuredHeight() / 3;
        }
    }

    public boolean isShowHelpBox() {
        return isShowHelpBox;
    }

    public void setShowHelpBox(boolean showHelpBox) {
        isShowHelpBox = showHelpBox;
        invalidate();
    }

    public float getScale() {
        return mScale;
    }

    public float getRotateAngle() {
        return mRotateAngle;
    }

    /**
     * 向外部提供监听事件
     */
    private OnEditClickListener mOnItemClickListener;

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnEditClickListener {
        void onEditClick(View v);
    }


    public interface OnViewClickListener {
        void onClick(View view);
    }

    private OnViewClickListener mOnClickListener;

    public void setOnViewClickListener(OnViewClickListener listener) {
        this.mOnClickListener = listener;
    }

    private OnDeleteClickListener mOnDeleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.mOnDeleteClickListener = listener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(View v, int id);
    }

    @Override
    public String toString() {
        return "TextStickerView{" +
                "mText='" + mText + '\'' +
                ", mTextRect=" + mTextRect +
                ", mHelpBoxRect=" + mHelpBoxRect +
                ", mDeleteRect=" + mDeleteRect +
                ", mRotateRect=" + mRotateRect +
                ", mDeleteDstRect=" + mDeleteDstRect +
                ", mRotateDstRect=" + mRotateDstRect +
                ", mCurrentMode=" + mCurrentMode +
                '}';
    }
}