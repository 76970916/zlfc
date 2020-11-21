package com.xinlan.imageeditlibrary.editimage.bean;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.xinlan.imageeditlibrary.editimage.view.StickerItem;
import com.xinlan.imageeditlibrary.editimage.view.StickerView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TouchEvent {
    List<ImageBean> List;
    Bitmap mainBitmap;//背景图
    LinkedHashMap<Integer, StickerItem> bank;//图层集合
    int eventPosition;
    Paint paint;

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public  List<ImageBean> getList() {
        return List;
    }

    public void setList(List<ImageBean> list) {
        List = list;
    }

    public int getEventPosition() {
        return eventPosition;
    }

    public void setEventPosition(int eventPosition) {
        this.eventPosition = eventPosition;
    }

    public LinkedHashMap<Integer, StickerItem> getBank() {
        return bank;
    }

    public void setBank(LinkedHashMap<Integer, StickerItem> bank) {
        this.bank = bank;
    }

    public Bitmap getMainBitmap() {
        return mainBitmap;
    }

    public void setMainBitmap(Bitmap mainBitmap) {
        this.mainBitmap = mainBitmap;
    }

    public List<ImageBean> getLayerList() {
        return List;
    }

    public void setLayerList(List<ImageBean> layerList) {
        List = layerList;
    }


}
