package com.xinlan.imageeditlibrary.editimage.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ObjectUtils;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.utils.RectUtil;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouch;


/**
 * @author panyi
 */
public class StickerItem extends ImageViewTouch {
    private static final float MIN_SCALE = 0.15f;
    private static final int HELP_BOX_PAD = 25;
    private static final int BUTTON_WIDTH = Constants.STICKER_BTN_HALF_SIZE;
    private int id;
    public Bitmap bitmap;
    public String path;//图片路径
    public Rect srcRect;// 原始图片坐标
    public RectF dstRect;// 绘制目标坐标
    private Rect helpToolsRect;
    public RectF deleteRect;// 删除按钮位置
    public RectF rotateRect;// 旋转按钮位置

    public RectF helpBox;
    public Matrix matrix;// 变化矩阵
    public float roatetAngle = 0;
    public boolean isDrawHelpTool = false;
    private Paint dstPaint = new Paint();
    private Paint paint = new Paint();
    private Paint helpBoxPaint = new Paint();
    private float initWidth;// 加入屏幕时原始宽度
    private static Bitmap deleteBit;
    private static Bitmap rotateBit;
    private Paint debugPaint = new Paint();
    public RectF detectRotateRect;

    public RectF detectDeleteRect;
    public int type;
    public int percentHeight, percentWith;
    public int mapPosition;//在mapList中的位置
    public int listPosition;//在显示list列表中的位置
    public int mapListPosition;//在map中的stickerItem中的位置
    private String svgContent;
    private boolean isSvg;
    public boolean visAble = true;
    private int mAlpha = -1;


    //文字属性
    public final int TEXT_SIZE_DEFAULT = getResources().getDimensionPixelSize(R.dimen.fontsize_default);
    //    public final int PADDING = getResources().getDimensionPixelSize(R.dimen.font_padding);
    public final int PADDING = 32;
    //    public final int STICKER_BTN_HALF_SIZE = getResources().getDimensionPixelSize(R.dimen.sticker_btn_half_size);
    public final int STICKER_BTN_HALF_SIZE = 30;
    private String mText = getResources().getString(R.string.input_hint); //给贴图文本赋个初始值
    private TextPaint mPaint = new TextPaint();
    private Paint mHelpPaint = new Paint();

    public Rect mTextRect = new Rect();// warp text rect record
    public RectF mHelpBoxRect = new RectF();
    private Rect mDeleteRect = new Rect();//删除按钮位置
    private Rect mRotateRect = new Rect();//旋转按钮位置
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

    public float mRotateAngle = 0;
    public float mScale = 1;
    private boolean isInitLayout = true;
    private boolean isShowHelpBox = true;
    private int phoneHeight;
    private int phoneWith;
    public static int textCount;

    public Typeface getTextType() {
        return textType;
    }

    public void setTextType(Typeface textType) {
        this.textType = textType;
        mPaint.setTypeface(textType);

    }

    //设置字体
    private Typeface textType;

    //设置画笔的透明度
    private int textmAlpha = 255;
    private int fontId;
    private int siteX;
    private int siteY;
    private float lineSpacing;
    private boolean textEm;
    public int itemType;
    public boolean longitudinal = false;

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

    public int getmAlpha() {
        return mAlpha;
    }

    public void setmAlpha(int mAlpha) {
        this.mAlpha = mAlpha;
        mPaint.setAlpha(mAlpha);
    }

    public String getSvgContent() {
        return svgContent;
    }

    public void setSvgContent(String svgContent) {
        this.svgContent = svgContent;
    }

    public boolean isSvg() {
        return isSvg;
    }

    public void setSvg(boolean svg) {
        isSvg = svg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public StickerItem(Context context) {
        super(context);

        helpBoxPaint.setColor(Color.BLACK);
        helpBoxPaint.setStyle(Style.STROKE);
        helpBoxPaint.setAntiAlias(true);
        helpBoxPaint.setStrokeWidth(4);

        dstPaint = new Paint();
        dstPaint.setColor(Color.RED);
        dstPaint.setAlpha(120);

        debugPaint = new Paint();
        debugPaint.setColor(Color.GREEN);
        debugPaint.setAlpha(120);

        // 导入工具按钮位图
        if (deleteBit == null) {
            deleteBit = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sticker_delete);
        }// end if
        if (rotateBit == null) {
            rotateBit = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sticker_rotate);
        }// end if
    }


    /**
     * @Author lixh
     * @Date 2020/10/27 17:50
     * @Description: 本Rect最左侧到屏幕的左侧的距离是 left
     * 本Rect最上面到屏幕上方的距离是 top
     * 本Rect最右侧到屏幕左侧的距离是 right
     * 本Rect最下面到屏幕上方的距离是 bottom
     */
    public void init(Bitmap addBit, View parentView, String path, int percentHeight, int percentWith) {
        this.percentHeight = percentHeight;
        this.percentWith = percentWith;
        this.bitmap = addBit;
        this.path = path;
        this.srcRect = new Rect(0, 0, (addBit.getWidth() * percentHeight) / 100, (addBit.getHeight() * percentWith) * 100);
        if (addBit.getWidth() > addBit.getHeight()) {
            if (percentHeight > percentWith) {
                int a = percentWith;
                percentWith = percentHeight;
                percentHeight = a;
            }
        } else {
            if (percentWith > percentHeight) {
                int a = percentWith;
                percentWith = percentHeight;
                percentHeight = a;
            }
        }
        int bitWidth = (Math.min(addBit.getWidth(), parentView.getWidth() >> 1) * percentWith) / 100;
        int bitHeight = ((int) bitWidth * addBit.getHeight() / addBit.getWidth() * percentHeight) / 100;
        int left = (parentView.getWidth() >> 1) - (bitWidth >> 1);
        int top = (parentView.getHeight() >> 1) - (bitHeight >> 1);
        this.dstRect = new RectF(left, top, left + bitWidth, top + bitHeight);
        this.matrix = new Matrix();
        this.matrix.postTranslate(this.dstRect.left, this.dstRect.top);
        this.matrix.postScale((float) bitWidth / addBit.getWidth(),
                (float) bitHeight / addBit.getHeight(), this.dstRect.left,
                this.dstRect.top);
        initWidth = this.dstRect.width();// 记录原始宽度
        // item.matrix.setScale((float)bitWidth/addBit.getWidth(),
        // (float)bitHeight/addBit.getHeight());
        this.isDrawHelpTool = true;
        this.helpBox = new RectF(this.dstRect);
        updateHelpBoxRect();

        helpToolsRect = new Rect(0, 0, deleteBit.getWidth(),
                deleteBit.getHeight());
        deleteRect = new RectF(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH, helpBox.left + BUTTON_WIDTH, helpBox.top
                + BUTTON_WIDTH);
        rotateRect = new RectF(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH, helpBox.right + BUTTON_WIDTH, helpBox.bottom
                + BUTTON_WIDTH);
        detectRotateRect = new RectF(rotateRect);
        detectDeleteRect = new RectF(deleteRect);
    }

    public void init(Bitmap addBit, View parentView, StickerItem stickerItem, int percentHeight, int percentWith) {
        this.percentHeight = percentHeight;
        this.percentWith = percentWith;
        this.bitmap = addBit;
        this.path = path;
        int bitWidth = 0;
        int bitHeight = 0;
        if (ObjectUtils.isEmpty(stickerItem)) {
            bitWidth = (Math.min(addBit.getWidth(), parentView.getWidth() >> 1) * percentWith) / 100;
            bitHeight = ((int) bitWidth * addBit.getHeight() / addBit.getWidth() * percentHeight) / 100;
            int left = (parentView.getWidth() >> 1) - (bitWidth >> 1);
            int top = (parentView.getHeight() >> 1) - (bitHeight >> 1);
            this.srcRect = new Rect(0, 0, addBit.getWidth(), addBit.getHeight());
            this.dstRect = new RectF(left, top, left + bitWidth, top + bitHeight);
        } else {
            bitWidth = (addBit.getWidth() * percentWith) / 100;
            bitHeight = (addBit.getHeight() * percentHeight) / 100;
            this.srcRect = stickerItem.srcRect;
            this.dstRect = stickerItem.dstRect;
            this.setSvg(stickerItem.isSvg);
        }
        this.matrix = new Matrix();
        this.matrix.postTranslate(this.dstRect.left, this.dstRect.top);
        this.matrix.postScale((float) bitWidth / addBit.getWidth(),
                (float) bitHeight / addBit.getHeight(), this.dstRect.left,
                this.dstRect.top);
        initWidth = this.dstRect.width();// 记录原始宽度
        // item.matrix.setScale((float)bitWidth/addBit.getWidth(),
        // (float)bitHeight/addBit.getHeight());
        this.isDrawHelpTool = true;
        this.helpBox = new RectF(this.dstRect);
        updateHelpBoxRect();

        helpToolsRect = new Rect(0, 0, deleteBit.getWidth(),
                deleteBit.getHeight());

        deleteRect = new RectF(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH, helpBox.left + BUTTON_WIDTH, helpBox.top
                + BUTTON_WIDTH);
        rotateRect = new RectF(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH, helpBox.right + BUTTON_WIDTH, helpBox.bottom
                + BUTTON_WIDTH);

        detectRotateRect = new RectF(rotateRect);
        detectDeleteRect = new RectF(deleteRect);
    }


    public void cropInit(Bitmap addBit, View parentView, StickerItem stickerItem, int percentHeight, int percentWith) {
        this.percentHeight = percentHeight;
        this.percentWith = percentWith;
        this.bitmap = addBit;
        this.path = path;
        int bitWidth = 0;
        int bitHeight = 0;
        bitWidth = (Math.min(addBit.getWidth(), parentView.getWidth() >> 1) * percentWith) / 100;
        bitHeight = ((int) bitWidth * addBit.getHeight() / addBit.getWidth() * percentHeight) / 100;
        if (ObjectUtils.isEmpty(stickerItem)) {
            int left = (parentView.getWidth() >> 1) - (bitWidth >> 1);
            int top = (parentView.getHeight() >> 1) - (bitHeight >> 1);
            this.srcRect = new Rect(0, 0, (addBit.getWidth() * percentHeight) / 100, (addBit.getHeight() * percentWith) * 100);
            this.dstRect = new RectF(left, top, left + bitWidth, top + bitHeight);
        } else {
            this.srcRect = stickerItem.srcRect;
            this.dstRect = stickerItem.dstRect;
            this.setSvg(stickerItem.isSvg);
        }
        this.matrix = new Matrix();
        this.matrix.postTranslate(this.dstRect.left, this.dstRect.top);
        this.matrix.postScale((float) bitWidth / addBit.getWidth(),
                (float) bitHeight / addBit.getHeight(), this.dstRect.left,
                this.dstRect.top);
        initWidth = this.dstRect.width();// 记录原始宽度
        // item.matrix.setScale((float)bitWidth/addBit.getWidth(),
        // (float)bitHeight/addBit.getHeight());
        this.isDrawHelpTool = true;
        this.helpBox = new RectF(this.dstRect);
        updateHelpBoxRect();

        helpToolsRect = new Rect(0, 0, deleteBit.getWidth(),
                deleteBit.getHeight());

        deleteRect = new RectF(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH, helpBox.left + BUTTON_WIDTH, helpBox.top
                + BUTTON_WIDTH);
        rotateRect = new RectF(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH, helpBox.right + BUTTON_WIDTH, helpBox.bottom
                + BUTTON_WIDTH);
        detectRotateRect = new RectF(rotateRect);
        detectDeleteRect = new RectF(deleteRect);
    }

    public void textInit(Context context, Typeface type, int phoneHeight, int phoneWith) {
        this.itemType = ConstantLogo.TEXT;
        this.phoneHeight = phoneHeight;
        this.phoneWith = phoneWith;
        initView(context, type);
    }

    /**
     * @Author lixh
     * @Date 2020/10/27 17:50
     * @Description: 本Rect最左侧到屏幕的左侧的距离是 left
     * 本Rect最上面到屏幕上方的距离是 top
     * 本Rect最右侧到屏幕左侧的距离是 right
     * 本Rect最下面到屏幕上方的距离是 bottom
     */
    public void init(Bitmap addBit, View parentView, String path) {
        this.bitmap = addBit;
        this.path = path;
        this.srcRect = new Rect(0, 0, addBit.getWidth(), addBit.getHeight());
        int bitWidth = Math.min(addBit.getWidth(), parentView.getWidth() >> 1);
        int bitHeight = (int) bitWidth * addBit.getHeight() / addBit.getWidth();
        int left = (parentView.getWidth() >> 1) - (bitWidth >> 1);
        int top = (parentView.getHeight() >> 1) - (bitHeight >> 1);
        this.dstRect = new RectF(left, top, left + bitWidth, top + bitHeight);
        this.matrix = new Matrix();
        this.matrix.postTranslate(this.dstRect.left, this.dstRect.top);
        this.matrix.postScale((float) bitWidth / addBit.getWidth(),
                (float) bitHeight / addBit.getHeight(), this.dstRect.left,
                this.dstRect.top);
        initWidth = this.dstRect.width();// 记录原始宽度
        // item.matrix.setScale((float)bitWidth/addBit.getWidth(),
        // (float)bitHeight/addBit.getHeight());
        this.isDrawHelpTool = true;
        this.helpBox = new RectF(this.dstRect);
        updateHelpBoxRect();

        helpToolsRect = new Rect(0, 0, deleteBit.getWidth(),
                deleteBit.getHeight());

        deleteRect = new RectF(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH, helpBox.left + BUTTON_WIDTH, helpBox.top
                + BUTTON_WIDTH);
        rotateRect = new RectF(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH, helpBox.right + BUTTON_WIDTH, helpBox.bottom
                + BUTTON_WIDTH);

        detectRotateRect = new RectF(rotateRect);
        detectDeleteRect = new RectF(deleteRect);
    }

    private void updateHelpBoxRect() {
        this.helpBox.left -= HELP_BOX_PAD;
        this.helpBox.right += HELP_BOX_PAD;
        this.helpBox.top -= HELP_BOX_PAD;
        this.helpBox.bottom += HELP_BOX_PAD;
    }


    /**
     * 位置更新
     *
     * @param dx
     * @param dy
     */
    public void updatePos(final float dx, final float dy) {
        this.matrix.postTranslate(dx, dy);// 记录到矩阵中
        dstRect.offset(dx, dy);
        // 工具按钮随之移动
        helpBox.offset(dx, dy);
        deleteRect.offset(dx, dy);
        rotateRect.offset(dx, dy);
        this.detectRotateRect.offset(dx, dy);
        this.detectDeleteRect.offset(dx, dy);
    }

    public void updateText(final float dx, final float dy) {
        layout_x += dx;
        layout_y += dy;
    }


    /**
     * 旋转 缩放 更新
     *
     * @param dx
     * @param dy
     */
    public void updateRotateAndScale(final float oldx, final float oldy,
                                     final float dx, final float dy) {
        float c_x = dstRect.centerX();
        float c_y = dstRect.centerY();

        float x = this.detectRotateRect.centerX();
        float y = this.detectRotateRect.centerY();

        // float x = oldx;
        // float y = oldy;

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);
        // System.out.println("srcLen--->" + srcLen + "   curLen---->" +
        // curLen);
        float scale = curLen / srcLen;// 计算缩放比
        float newWidth = dstRect.width() * scale;
        if (newWidth / initWidth < MIN_SCALE) {// 最小缩放值检测
            return;
        }
        this.matrix.postScale(scale, scale, this.dstRect.centerX(),
                this.dstRect.centerY());// 存入scale矩阵
        // this.matrix.postRotate(5, this.dstRect.centerX(),
        // this.dstRect.centerY());
        RectUtil.scaleRect(this.dstRect, scale);// 缩放目标矩形
        // 重新计算工具箱坐标
        helpBox.set(dstRect);
        updateHelpBoxRect();// 重新计算
        rotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);
        deleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);
        detectRotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);
        detectDeleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);
        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));
        // System.out.println("angle--->" + angle);

        // 定理
        float calMatrix = xa * yb - xb * ya;// 行列式计算 确定转动方向

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;
        // System.out.println("angle--- >" + angle);
        roatetAngle += angle;
        this.matrix.postRotate(angle, this.dstRect.centerX(),
                this.dstRect.centerY());
        RectUtil.rotateRect(this.detectRotateRect, this.dstRect.centerX(),
                this.dstRect.centerY(), roatetAngle);
        RectUtil.rotateRect(this.detectDeleteRect, this.dstRect.centerX(),
                this.dstRect.centerY(), roatetAngle);
        // System.out.println("angle----->" + angle + "   " + flag);
        // System.out
        // .println(srcLen + "     " + curLen + "    scale--->" + scale);
    }

    public void setRangle(float scale, float rotation) {
        this.matrix.postScale(scale, scale, this.dstRect.centerX(),
                this.dstRect.centerY());// 存入scale矩阵
        // this.matrix.postRotate(5, this.dstRect.centerX(),
        // this.dstRect.centerY());
        RectUtil.scaleRect(this.dstRect, scale);// 缩放目标矩形
        // 重新计算工具箱坐标
        // 重新计算工具箱坐标
        helpBox.set(dstRect);
        updateHelpBoxRect();// 重新计算
        rotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);
        deleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);
        detectRotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);
        detectDeleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);
        if (rotation != 0) {
            roatetAngle += rotation;
            this.matrix.postRotate(rotation, this.dstRect.centerX(),
                    this.dstRect.centerY());
            RectUtil.rotateRect(this.detectRotateRect, this.dstRect.centerX(),
                    this.dstRect.centerY(), roatetAngle);
            RectUtil.rotateRect(this.detectDeleteRect, this.dstRect.centerX(),
                    this.dstRect.centerY(), roatetAngle);
        }

    }

    public void draw(Canvas canvas, int type) {
        super.draw(canvas);
        if (type == ConstantLogo.IMAGEANDSVG) {
            canvas.drawBitmap(this.bitmap, this.matrix, null);// 贴图元素绘制
            // canvas.drawRect(this.dstRect, dstPaint);
            if (this.isDrawHelpTool) {// 绘制辅助工具线
                canvas.save();
                canvas.rotate(roatetAngle, helpBox.centerX(), helpBox.centerY());
                canvas.drawRoundRect(helpBox, 10, 10, helpBoxPaint);
                // 绘制工具按钮
                canvas.drawBitmap(deleteBit, helpToolsRect, deleteRect, null);
                canvas.drawBitmap(rotateBit, helpToolsRect, rotateRect, null);
                canvas.restore();
            }
        } else if (type == ConstantLogo.TEXT) {
            if (TextUtils.isEmpty(mText))
                return;
            drawContent(canvas);
        }
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
        detectDeleteRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1, STICKER_BTN_HALF_SIZE << 1);
        detectRotateRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1, STICKER_BTN_HALF_SIZE << 1);
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
        resetView();
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
    }

    public void setTextSize(float size) {
        mPaint.setTextSize(size);
    }

    public float getTextSize() {
        return mPaint.getTextSize();
    }


    public int getCurrentTextColor() {
        return mPaint.getColor();
    }


    public int getTextAlpha() {
        return mAlpha;
    }

    public void setTextAlpha(int mAlpha) {
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


    private void drawContent(Canvas canvas) {
        if (longitudinal) {
            drawTextLongitudinal(canvas, textCount);
        } else {
            drawText(canvas);
        }
        //draw x and rotate button
        int offsetValue = ((int) detectDeleteRect.width()) >> 1;
        detectDeleteRect.offsetTo(mHelpBoxRect.left - offsetValue, mHelpBoxRect.top - offsetValue);
        detectRotateRect.offsetTo(mHelpBoxRect.right - offsetValue, mHelpBoxRect.bottom - offsetValue);
        RectUtil.rotateRect(detectDeleteRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);
        RectUtil.rotateRect(detectRotateRect, mHelpBoxRect.centerX(),
                mHelpBoxRect.centerY(), mRotateAngle);

        if (!isDrawHelpTool) {
            return;
        }

        canvas.save();
        canvas.rotate(mRotateAngle, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.drawRoundRect(mHelpBoxRect, 10, 10, mHelpPaint);
        canvas.restore();
        canvas.drawBitmap(mDeleteBitmap, mDeleteRect, detectDeleteRect, null);
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
        int df_with = 100;
        int top_height = phoneHeight / 3 - df_with;
        int left_with = phoneWith / 2 - df_with;
        int bottom_height = phoneHeight / 3 + df_with;
        int right_with = phoneWith / 2 + df_with;
        mHelpBoxRect.set(mTextRect.left - PADDING, mTextRect.top - PADDING
                , mTextRect.right + PADDING, mTextRect.bottom + PADDING);
//        mHelpBoxRect.set(left_with, top_height
//                , right_with, bottom_height);
        RectUtil.scaleRect(mHelpBoxRect, scale);
        canvas.save();
        canvas.scale(scale, scale, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.rotate(rotate, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.drawText(mText, x, y, mPaint);
        canvas.restore();
    }

    private void drawTextLongitudinal(Canvas canvas, int count) {
        drawTextLongitudinal(canvas, layout_x, layout_y, mScale, mRotateAngle, count);
    }

    public void drawTextLongitudinal(Canvas canvas, int _x, int _y, float scale, float rotate, int count) {
        if (TextUtils.isEmpty(mText)) {
            return;
        }
        int x = _x;
        int y = _y;
        mPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        int mWith = (mTextRect.width() >> 1);
        mTextRect.offset(x -mWith, y);
        int df_with = 100;
        int top_height = phoneHeight / 3 - df_with;
        int left_with = phoneWith / 2 - df_with;
        int bottom_height = phoneHeight / 3 + df_with;
        int right_with = phoneWith / 2 + df_with;
        int dy = mTextRect.bottom + PADDING - (mTextRect.top - PADDING);
        int dx = (mTextRect.right - mTextRect.left - PADDING)/2;
        int with = mTextRect.left - PADDING + dx;
        mHelpBoxRect.set(with-10, mTextRect.top - PADDING
                , with+mPaint.getTextSize()+30 , mTextRect.bottom + PADDING+(mPaint.getTextSize()*mText.length()));
//        mHelpBoxRect.set(mTextRect.left - PADDING, mTextRect.top - PADDING
//                , mTextRect.right + PADDING, mTextRect.bottom + PADDING);
//        mHelpBoxRect.set(left_with, top_height
//                , right_with, bottom_height);
        RectUtil.scaleLongRect(mHelpBoxRect, scale);
        canvas.save();
        canvas.scale(scale, scale, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.rotate(rotate, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
//        canvas.drawText(mText, x, y, mPaint);
        y = y - 30;
        x = (int) (x + mPaint.getTextSize())-PADDING;
        for (int i = 0; i < mText.length(); i++) {
            int size = (int) mPaint.getTextSize();
            y = y + size;
            canvas.drawText(mText.substring(i,i+1), x,y , mPaint);
        }
        canvas.restore();
    }


    public void clearTextContent() {
        if (mEditText != null) {
            mEditText.setText(null);
        }
        //setText(null);
    }


    public void resetView() {
        if (siteX != 0 && siteY != 0) {
            layout_x = siteX;
            layout_y = siteY;
        } else {
            layout_x = phoneWith / 2;
            layout_y = phoneHeight / 3;
        }
        mRotateAngle = 0;
        mScale = 1;
    }

    public boolean isShowHelpBox() {
        return isShowHelpBox;
    }

    public void setShowHelpBox(boolean showHelpBox) {
        isDrawHelpTool = showHelpBox;
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
    private TextStickerView.OnEditClickListener mOnItemClickListener;

    public void setOnEditClickListener(TextStickerView.OnEditClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnEditClickListener {
        void onEditClick(View v);
    }


    public interface OnViewClickListener {
        void onClick(View view);
    }

    private TextStickerView.OnViewClickListener mOnClickListener;

    public void setOnViewClickListener(TextStickerView.OnViewClickListener listener) {
        this.mOnClickListener = listener;
    }

    private TextStickerView.OnDeleteClickListener mOnDeleteClickListener;

    public void setOnDeleteClickListener(TextStickerView.OnDeleteClickListener listener) {
        this.mOnDeleteClickListener = listener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(View v, int id);
    }


}
