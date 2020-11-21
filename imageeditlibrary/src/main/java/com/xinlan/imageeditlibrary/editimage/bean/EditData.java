package com.xinlan.imageeditlibrary.editimage.bean;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;

import com.pixplicity.sharp.Sharp;
import com.xinlan.imageeditlibrary.editimage.view.TextStickerView;

import java.util.List;

public class EditData {
    float trans; //透明度
    String imageUrl;//图片地址
    int type;
    Bitmap bitmap;
    List<AlbumInfo> list;//图库集合
    int color;
    Sharp sharp;
    int position;
    Uri uri;
    Paint paint;
    int paintPosition;
    int listPosition;
    List<Paint> paintList;
    int with;
    int height;
    int code;
    String svgContent;
    String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSvgContent() {
        return svgContent;
    }

    public void setSvgContent(String svgContent) {
        this.svgContent = svgContent;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getPaintPosition() {
        return paintPosition;
    }

    public void setPaintPosition(int paintPosition) {
        this.paintPosition = paintPosition;
    }

    public int getWith() {
        return with;
    }

    public void setWith(int with) {
        this.with = with;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private List<TextStickerView> mTextList;

    public List<TextStickerView> getmTextList() {
        return mTextList;
    }

    public void setmTextList(List<TextStickerView> mTextList) {
        this.mTextList = mTextList;
    }

    public List<Paint> getPaintList() {
        return paintList;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Sharp getSharp() {
        return sharp;
    }

    public void setSharp(Sharp sharp) {
        this.sharp = sharp;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<AlbumInfo> getList() {
        return list;
    }

    public void setList(List<AlbumInfo> list) {
        this.list = list;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getTrans() {
        return trans;
    }

    public void setTrans(float trans) {
        this.trans = trans;
    }
}
