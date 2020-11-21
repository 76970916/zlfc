package com.xinlan.imageeditlibrary.editimage.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.xinlan.imageeditlibrary.editimage.bean.ConstantLogo;
import com.xinlan.imageeditlibrary.editimage.bean.ImageBean;
import com.xinlan.imageeditlibrary.editimage.bean.LogoBean;
import com.xinlan.imageeditlibrary.editimage.bean.MessageEvent;
import com.xinlan.imageeditlibrary.editimage.utils.RectUtil;
import com.xinlan.imageeditlibrary.editimage.utils.WeakHandler;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouch;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 贴图操作控件
 *
 * @author panyi
 */
public class StickerView extends ImageViewTouch {
    private static int STATUS_IDLE = 0;
    private static int STATUS_MOVE = 1;// 移动状态
    private static int STATUS_DELETE = 2;// 删除状态
    private static int STATUS_ROTATE = 3;// 图片旋转状态
    private int imageCount;// 已加入照片的数量
    private Context mContext;
    private int currentStatus;// 当前状态
    private StickerItem currentItem;// 当前操作的贴图数据
    private float oldx, oldy;
    boolean ret;
    private Paint rectPaint = new Paint();
    private Paint boxPaint = new Paint();
    private LinkedHashMap<Integer, StickerItem> bank = new LinkedHashMap<Integer, StickerItem>();// 存贮每层贴图数据
    private LinkedHashMap<Integer, StickerItem> oldMap = new LinkedHashMap<Integer, StickerItem>();// 存贮每层贴图数据
    private List<Integer> hideItem = new ArrayList<>();//被影藏的选项
    private List<LinkedHashMap<Integer, StickerItem>> mapList = new ArrayList<>(); //多个画布存储的数据
    private Point mPoint = new Point(0, 0);
    WeakHandler mHandler;
    OnStickerClickListener clickListener;
    OnTemplateClickListener templateClickListener;
    int stickerPosition;
    int currentPosition = 0;//
    int mapCount = 0;//bank在map中位置
    int count = 0;
    List<ImageBean> layList;
    protected int lastX;
    protected int lastY;
    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;
    //初始的旋转角度
    private float oriRotation = 0;
    private static String TAG = "sxlwof";
    int startX;
    int endX;
    int startY;
    int endY;
    int bgPosition = 0;//背景图下面控件添加在位置背景图

    // 取消OnTouchEvent事件
    private boolean TouchEnable;
    // 属性变量
    private float translationX; // 移动X
    private float translationY; // 移动Y
    private float scale = 1; // 伸缩比例
    private float rotation; // 旋转角度

    // 移动过程中临时变量
    private float actionX;
    private float actionY;
    private float spacing;
    private float degree;
    private int moveType; // 0=未选择，1=拖动，2=缩放
    private float scale_old;
    private float rotation_old;
    private int createType;//图片或者文字
    private long currentTime;

    public int getCreateType() {
        return createType;
    }

    public void setCreateType(int createType) {
        this.createType = createType;
    }

    public boolean isTouchEnable() {
        return TouchEnable;
    }

    public int getMapCount() {
        return mapCount;
    }

    public void setMapCount(int mapCount) {
        this.mapCount = mapCount;
    }

    public void setTouchEnable(boolean touchEnable) {
        TouchEnable = touchEnable;
    }


    public List<LinkedHashMap<Integer, StickerItem>> getMapList() {
        return mapList;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setVisibMapPosition(int position) {
        for (Map.Entry<Integer, StickerItem> entryAll : oldMap.entrySet()) {
            if (entryAll.getKey() == position) {
                bank.put(entryAll.getKey(), entryAll.getValue());
            }
        }
        if (hideItem.size() > 0) {
            for (int i = 0; i < hideItem.size(); i++) {
                if (position == hideItem.get(i)) {
                    hideItem.remove(i);
                }
            }
        }
        this.invalidate();
    }


    public void setGoneMapPosition(int position) {
        bank.remove(position);
        hideItem.add(position);
        this.invalidate();
    }

    public void setItem(StickerItem stickerItem, int position) {
        bank.put(position, stickerItem);
        invalidate();
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setLocation(int left, int right, int top, int bottom) {
        startX = left;
        endX = right;
        startY = top;
        endY = bottom;
    }

    public int getMaxPosition() {
        List<Integer> list = new ArrayList<>();
        for (Map.Entry<Integer, StickerItem> entryAll : bank.entrySet()) {
            list.add(entryAll.getKey());
        }
        Collections.sort(list, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
        return list.get(0);
    }

    private TextStickerView.OnViewClickListener mOnClickListener;

    public void setOnViewClickListener(TextStickerView.OnViewClickListener listener) {
        this.mOnClickListener = listener;
    }

    public void setStickerListener(OnStickerClickListener listener) {
        this.clickListener = listener;
    }

    private TextStickerView.OnDeleteClickListener mOnDeleteClickListener;

    public void setOnDeleteClickListener(TextStickerView.OnDeleteClickListener listener) {
        this.mOnDeleteClickListener = listener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(View v, int id);
    }

    public void setTextType(Typeface textType) {
        currentItem.setTextType(textType);
        invalidate();
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


    public interface OnStickerClickListener {
        default void delete(StickerItem item) {

        }//当前item

        default void deleteListPosition(int position) {

        }

        default void getEventItemList(LinkedHashMap<Integer, StickerItem> bank) {

        }

        default void getTouchSticker(int position, Bitmap bitmap) {

        }

        default void getTouchEvent() {

        }
    }

    public int getStickerPosition() {
        return stickerPosition;
    }

    public StickerView(Context context) {
        super(context);
        init(context);
    }

    public void setTemplateClickListener(OnTemplateClickListener templateClickListener) {
        this.templateClickListener = templateClickListener;
    }

    public interface OnTemplateClickListener {
        void onclick(StickerItem item);
    }

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setmAlpha(int mAlpha) {
        currentItem.setmAlpha(mAlpha);
        this.invalidate();
    }

    public void setTextSize(float size) {
        currentItem.setTextSize(size);
        this.invalidate();
    }

    public void setTextEm(boolean isEm) {
        currentItem.setTextEm(isEm);
        this.invalidate();
    }

    public LinkedHashMap<Integer, StickerItem> getStickerItem() {
        return bank;
    }

    public void setTextBold(boolean isBoldText) {
        currentItem.setTextBold(isBoldText);
    }

    public void setLineSpacing(float lineSpacing) {
        currentItem.setLineSpacing(lineSpacing);
        invalidate();
    }

    private void init(Context context) {
        this.mContext = context;
        currentStatus = STATUS_IDLE;
        rectPaint.setColor(Color.RED);
        rectPaint.setAlpha(100);
    }

    public void setStickerFrame(int framePosition) {
        if (bank.size() > 0) {
            for (Map.Entry<Integer, StickerItem> entryAll : bank.entrySet()) {
                StickerItem stickerItem = entryAll.getValue();
                if (stickerItem.mapListPosition == framePosition) {
                    entryAll.getValue().isDrawHelpTool = true;
                    currentItem = entryAll.getValue();
                } else {
                    entryAll.getValue().isDrawHelpTool = false;
                }
            }
        }
        this.invalidate();
    }

    /**
     * @Author lixh
     * @Date 2020/10/28 20:47
     * @Description: 添加 Bitmap
     */
    public void addBitImage(final Bitmap addBit, int type, StickerItem items, int percentHeight, int percentWith) {
        StickerItem item = new StickerItem(this.getContext());
        item.itemType = ConstantLogo.IMAGEANDSVG;
        item.init(addBit, this, items, percentHeight, percentWith);
        if (ObjectUtils.isNotEmpty(items)) {
            item.setId(items.getId());
        }
        item.setType(type);
        if (currentItem != null) {
            currentItem.isDrawHelpTool = false;
        }
        if (items != null && ObjectUtils.isNotEmpty(items.getSvgContent())) {
            item.setSvgContent(items.getSvgContent());
        }
        currentPosition++;
        item.listPosition = currentPosition;
        //大于新加的item 位置加1
        bank.put(bank.size(), item);
        oldMap.put(oldMap.size(), item);
        clickListener.getEventItemList(bank);
        currentItem = item;
        this.invalidate();// 重绘视图
    }

    public void addBitImage(final Bitmap addBit, String path, int percentHeight, int percentWith) {
        StickerItem item = new StickerItem(this.getContext());
        item.itemType = ConstantLogo.IMAGEANDSVG;
        item.init(addBit, this, path, percentHeight, percentWith);
        if (currentItem != null) {
            currentItem.isDrawHelpTool = false;
        }
        stickerPosition = ++imageCount;
        currentPosition++;
        item.listPosition = currentPosition;
        //大于新加的item 位置加1
        bank.put(bank.size(), item);
        oldMap.put(oldMap.size(), item);
        //添加到maplist当中
        currentItem = item;
        this.invalidate();// 重绘视图
    }

    public void addText(Typeface type, int phoneHeight, int phoneWith) {
        StickerItem item = new StickerItem(this.getContext());
        item.itemType = ConstantLogo.TEXT;
        item.listPosition = currentPosition;
        item.isDrawHelpTool = true;
        item.textInit(this.getContext(), type, phoneHeight, phoneWith);
        bank.put(bank.size(), item);
        oldMap.put(oldMap.size(), item);
        currentItem = item;
        currentPosition++;
        this.invalidate();// 重绘视图
    }

    public void setTextStickerContent(StickerItem stickerItem, String Context) {
        currentItem.setText(Context);
        invalidate();
    }

    /**
     * @Author lixh
     * @Date 2020/10/28 20:47
     * @Description: 添加 Bitmap
     */
    public void addBitImage(final Bitmap addBit, int type, StickerItem items) {
        StickerItem item = new StickerItem(this.getContext());
        item.init(addBit, this, item.path);
        if (ObjectUtils.isNotEmpty(items)) {
            item.setId(items.getId());
        }
        item.setType(type);
        if (currentItem != null) {
            currentItem.isDrawHelpTool = false;
        }
        bank.put(++imageCount, item);
        clickListener.getEventItemList(bank);
        currentItem = item;
        this.invalidate();// 重绘视图
    }

    public LinkedHashMap<Integer, StickerItem> getMap() {
        return oldMap;
    }

    /**
     * @Author lixh
     * @Date 2020/10/28 20:47
     * @Description: 替换 bitmap
     */
    public void replaceBitmap(final Bitmap repBit, int type, StickerItem stickerItem, int percentHeight, int percentWith) {
        StickerItem item = new StickerItem(this.getContext());
        item.itemType = ConstantLogo.IMAGEANDSVG;
        item.init(repBit, this, stickerItem, percentHeight, percentWith);
        item.setType(type);
        if (stickerItem != null && stickerItem.getId() != 0) {
            item.setId(stickerItem.getId());
        }
        if (currentItem != null) {
            currentItem.isDrawHelpTool = false;
        }
        if (stickerItem != null && ObjectUtils.isNotEmpty(stickerItem.getSvgContent())) {
            item.setSvgContent(stickerItem.getSvgContent());
            item.setmAlpha(stickerItem.getmAlpha());
        }
        bank.put(currentPosition, item);
        currentItem = item;
        this.invalidate();// 重绘视图
    }


    public void setImageBitmap(Bitmap bitMap) {
        if (currentItem != null) {
            currentItem.bitmap = bitMap;
            for (Map.Entry<Integer, StickerItem> entry : bank.entrySet()) {
                if (entry.getKey() == currentPosition) {
                    StickerItem stickerItem = entry.getValue();
                    stickerItem.bitmap = bitMap;
                    bank.put(currentPosition, stickerItem);
                }
            }
            this.invalidate();
        }
    }

    public void setImageCropBitmap(int cropPosition, Bitmap cropBitmap, String path, int percentHeight, int percentWith) {
        for (Map.Entry<Integer, StickerItem> entry : bank.entrySet()) {
            //修改裁剪之后的图片
            int position = entry.getKey();
            if (position == cropPosition) {
                StickerItem item = new StickerItem(this.getContext());
                item.itemType = ConstantLogo.IMAGEANDSVG;
                item.cropInit(cropBitmap, this, currentItem, percentHeight, percentWith);
//                item.init(cropBitmap, this, path, percentHeight, percentWith);
                bank.put(position, item);
            }
        }
        this.invalidate();
    }


    //修改图层
    public void setStickerItem(LinkedHashMap<Integer, StickerItem> map, int fromPosition, int toPosition) {
        oldMap = map;
        bank.clear();
        for (Map.Entry<Integer, StickerItem> entry : oldMap.entrySet()) {
            bank.put(entry.getKey(), entry.getValue());
        }
        for (int i = 0; i < hideItem.size(); i++) {
            int position = hideItem.get(i);
            if (position == fromPosition) {
                bank.remove(toPosition);
            } else if (position == toPosition) {
                bank.remove(fromPosition);
            }
        }
        this.invalidate();
    }

    //前进后退事件修改图层
    public void setStickerItemEvent(LinkedHashMap<Integer, StickerItem> map) {
        this.invalidate();
        bank = map;
    }

    public void setImageUrl(String url) {
        Bitmap bitmap = BitmapFactory.decodeFile(url);
        currentItem.bitmap = bitmap;
        this.invalidate();
    }

    public void setTextColor(StickerItem stickerItem, int newColor) {
        stickerItem.setTextColor(newColor);
//        bank.put(stickerItem.listPosition, stickerItem);
        this.invalidate();
    }

    public StickerItem getItem() {
        return currentItem;
    }

    public Bitmap getBitmap() {
        return currentItem.bitmap;
    }

    /**
     * 绘制客户页面
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // System.out.println("on draw!!~");
        //map排序
        List<Integer> listSort = new ArrayList<>();
        for (Map.Entry<Integer, StickerItem> entry : bank.entrySet()) {
            listSort.add(entry.getKey());
        }
        Collections.sort(listSort, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        List<StickerItem> itemList = new ArrayList<>();
        for (int i = 0; i < listSort.size(); i++) {
            itemList.add(bank.get(listSort.get(i)));
        }
        for (StickerItem item : itemList) {
            item.draw(canvas, item.itemType);
        }
//        for (Integer id : bank.keySet()) {
//            StickerItem item = bank.get(id);
//            item.draw(canvas);
//        }// end for each
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // System.out.println(w + "   " + h + "    " + oldw + "   " + oldh);
    }

    private int type = 0;
    private int deleteMapPosition;
    private int deleteListposition;
    private int deleteMapListPosition;
    private StickerItem touchItem;
    private float dx;
    private float dy;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isTouchEnable()) {
            return false;
        }
        ret = super.onTouchEvent(event);// 是否向下传递事件标志 true为消耗
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                moveType = 2;
                spacing = getSpacing(event);
                degree = getDegree(event);
                break;
            case MotionEvent.ACTION_DOWN:
                moveType = 1;
                actionX = event.getRawX();
                actionY = event.getRawY();
                int deleteId = -1;
                for (Integer id : bank.keySet()) {
                    StickerItem item = bank.get(id);
                    deleteMapPosition = item.mapPosition;
                    deleteListposition = item.listPosition;
                    deleteMapListPosition = item.mapListPosition;
                    if (item.detectDeleteRect.contains(x, y)) {// 删除模式
                        // ret = true;
                        deleteId = id;
                        currentStatus = STATUS_DELETE;
                        // 删除该对象在数据库中的数据
                        if (item.isSvg()) {
                            MessageEvent event1 = new MessageEvent();
                            event1.setMessage(Constants.CLEAR_SVG_DATA);
                            EventBus.getDefault().postSticky(event);
                        }
                        LitePal.delete(LogoBean.class, item.getId());
                        LogUtils.e(item.getId());
                    } else if (item.detectRotateRect.contains(x, y)) {// 点击了旋转按钮
                        moveType = 3;
                        ret = true;
                        if (currentItem != null) {
                            currentItem.isDrawHelpTool = false;
                        }
                        currentItem = item;
                        currentItem.isDrawHelpTool = true;
                        currentStatus = STATUS_ROTATE;
                        oldx = x;
                        oldy = y;
                    } else if (detectInItemContent(item, x, y)) {// 移动模式
                        // 被选中一张贴图
                        ret = true;
                        currentTime = System.currentTimeMillis();
                        stickerPosition = id;
                        if (currentItem != null) {
                            currentItem.isDrawHelpTool = false;
                        }
                        currentItem = item;
                        currentItem.isDrawHelpTool = true;
                        currentStatus = STATUS_MOVE;
                        oldx = x;
                        oldy = y;
                        if (clickListener != null) {
                            clickListener.getTouchSticker(stickerPosition, currentItem.bitmap);
                        }
                        if (item.getType() == 101) {
                            type = item.getType();
                        }
                    }
                }// end for each
                if (!ret && currentItem != null && currentStatus == STATUS_IDLE) {// 没有贴图被选择
                    currentItem.isDrawHelpTool = false;
                    currentItem = null;
                    invalidate();
                }

                // 删除选定贴图
                if (deleteId >= 0 && currentStatus == STATUS_DELETE) {
                    if (clickListener != null) {
                        clickListener.deleteListPosition(deleteId);
                    }
                    bank.remove(deleteId);
                    oldMap.remove(deleteId);
                    //删除当前position修改
                    if (currentPosition > deleteListposition) {
                        currentPosition = currentPosition - 1;
                    }
                    // 返回空闲状态
                    currentStatus = STATUS_IDLE;
                    List<Integer> countList = new ArrayList<>();
                    for (Map.Entry<Integer, StickerItem> entry : bank.entrySet()) {
                        countList.add(entry.getKey());
                    }
                    Collections.sort(countList, new Comparator<Integer>() {
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return o2 - o1;
                        }
                    });
                    for (Map.Entry<Integer, StickerItem> entry : bank.entrySet()) {
                        if (countList.get(0) == entry.getKey()) {
                            currentItem = entry.getValue();
                            if (clickListener != null) {
                                clickListener.delete(currentItem);
                            }
                        }
                    }
                    deletSortMap(deleteId);
                    //删除文字回调
                    if (ObjectUtils.isNotEmpty(currentItem)) {
                        if (currentItem.itemType == ConstantLogo.TEXT) {
                            mOnDeleteClickListener.onDeleteClick(this, currentItem.getId());
                        }
                    }
                    invalidate();
                }// end if

                if (type == 101) {
                    templateClickListener.onclick(currentItem);
                }
                clickListener.getTouchEvent();
                break;
            case MotionEvent.ACTION_MOVE:
                if (ObjectUtils.isEmpty(currentItem)) {
                    break;
                }
                ret = true;
                if (currentItem.itemType == ConstantLogo.TEXT) {
                    if (moveType == 1) {// 移动贴图
                        if (currentStatus == STATUS_MOVE) {
                            float dx = x - oldx;
                            float dy = y - oldy;
                            if (currentItem != null) {
                                currentItem.updateText(dx, dy);
                                invalidate();
                            }// end if
                            oldx = x;
                            oldy = y;
                        }

                    }
                } else {
                    if (moveType == 2) {
                        scale = scale * getSpacing(event) / spacing;
                        rotation = getDegree(event) - degree;
                        Log.d("spacing", String.valueOf(getSpacing(event)));
                        Log.d("degree", String.valueOf(getDegree(event)));
                        if (scale_old == scale) {
                            scale = 1;
                        }
                        if (rotation_old == rotation) {
                            rotation = 0;
                        }
                        scale_old = scale;
                        rotation_old = rotation;
                        Log.d("scale", String.valueOf(scale));
                        Log.d("rotation", String.valueOf(rotation));
                        if (scale > 1.2) {
                            scale = 1.05f;
                        } else if (scale < 0.8) {
                            scale = 0.95f;
                        } else {
                            scale = 1;
                        }
                        if (rotation > 10) {
                            rotation = 5;
                        } else if (rotation < -10) {
                            rotation = -5;
                        } else {
                            rotation = 0;
                        }
                        float dx = x - oldx;
                        float dy = y - oldy;
                        if (currentItem != null) {
                            currentItem.setRangle(scale, rotation);
                            invalidate();
                        }
                        oldx = x;
                        oldy = y;
                    } else {
                        if (moveType == 1) {// 移动贴图
                            if (currentStatus == STATUS_MOVE) {
                                float dx = x - oldx;
                                float dy = y - oldy;
                                if (currentItem != null) {
                                    currentItem.updatePos(dx, dy);
                                    invalidate();
                                }// end if
                                oldx = x;
                                oldy = y;
                            }

                        } else if (currentStatus == STATUS_ROTATE) {// 旋转 缩放图片操作
                            // System.out.println("旋转");
                            float dx = x - oldx;
                            float dy = y - oldy;
                            if (currentItem != null) {
                                currentItem.updateRotateAndScale(oldx, oldy, dx, dy);// 旋转
                                invalidate();
                            }// end if
                            oldx = x;
                            oldy = y;
                        }
                    }

                }
                clickListener.getTouchEvent();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (ObjectUtils.isEmpty(currentItem)) {
                    break;
                }
                if (currentItem.itemType == ConstantLogo.TEXT) {
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
                }
                ret = false;
                currentStatus = STATUS_IDLE;
                moveType = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                moveType = 0;
        }// end switch
        return ret;
    }

    private void deletSortMap(int deleteId) {
        LinkedHashMap<Integer, StickerItem> sortMap = new LinkedHashMap<Integer, StickerItem>();// 存贮每层贴图数据
        for (Map.Entry<Integer, StickerItem> entryAll : bank.entrySet()) {
            if (entryAll.getKey() < deleteId) {
                sortMap.put(entryAll.getKey(), entryAll.getValue());
            } else {
                sortMap.put(entryAll.getKey() - 1, entryAll.getValue());
            }
        }
        bank = sortMap;
        LinkedHashMap<Integer, StickerItem> sortOld = new LinkedHashMap<Integer, StickerItem>();// 存贮每层贴图数据
        for (Map.Entry<Integer, StickerItem> entryAll : oldMap.entrySet()) {
            if (entryAll.getKey() < deleteId) {
                sortOld.put(entryAll.getKey(), entryAll.getValue());
            } else {
                sortOld.put(entryAll.getKey() - 1, entryAll.getValue());
            }
        }
        oldMap = sortOld;
    }


    public void deleteFrame() {
        if (bank.size() > 0) {
            for (Map.Entry<Integer, StickerItem> entryAll : bank.entrySet()) {
                entryAll.getValue().isDrawHelpTool = false;
            }
        }
        this.invalidate();
    }

    public void setFrame() {
        if (bank.size() > 0) {
            for (Map.Entry<Integer, StickerItem> entryAll : bank.entrySet()) {
                if (entryAll.getKey() == bank.size() - 1) {
                    entryAll.getValue().isDrawHelpTool = true;
                }
            }
        }
        this.invalidate();
    }


    public void loadMap(int bgPosition) {
        this.bgPosition = bgPosition;
        bank = mapList.get(bgPosition);
        mapCount = bgPosition;
        this.invalidate();
    }

    /**
     * 判定点击点是否在内容范围之内  需考虑旋转
     *
     * @param item
     * @param x
     * @param y
     * @return
     */
    private boolean detectInItemContent(StickerItem item, float x, float y) {
        //reset
        mPoint.set((int) x, (int) y);
        //旋转点击点
        if (item.itemType == ConstantLogo.TEXT) {
            RectUtil.rotatePoint(mPoint, item.mHelpBoxRect.centerX(), item.mHelpBoxRect.centerY(), -item.roatetAngle);
            return item.mHelpBoxRect.contains(mPoint.x, mPoint.y);
        } else {
            RectUtil.rotatePoint(mPoint, item.helpBox.centerX(), item.helpBox.centerY(), -item.roatetAngle);
            return item.helpBox.contains(mPoint.x, mPoint.y);
        }
    }

    public LinkedHashMap<Integer, StickerItem> getBank() {
        return bank;
    }

    public void setBank(LinkedHashMap<Integer, StickerItem> bank) {
        this.bank = bank;
    }

    public void clear() {
        bank.clear();
        clickListener.getEventItemList(bank);
        this.invalidate();
    }

    // 触碰两点间距离
    private float getSpacing(MotionEvent event) {
        //通过三角函数得到两点间的距离
        float x = 0;
        float y;
        x = event.getX(0) - event.getX(1);
        y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取旋转角度
    private float getDegree(MotionEvent event) {
        //得到两个手指间的旋转角度
        double delta_x = event.getX(0) - event.getX(1);
        double delta_y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

}