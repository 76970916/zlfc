package com.xinlan.imageeditlibrary.editimage.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ImageBean {
    String image_text;
    int itemType;
    Drawable image_drawable;
    Bitmap bitmap;
    boolean isVisable;
    int textColor;
    String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isVisable() {
        return isVisable;
    }

    public void setVisable(boolean visable) {
        isVisable = visable;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Drawable getImage_drawable() {
        return image_drawable;
    }

    public void setImage_drawable(Drawable image_drawable) {
        this.image_drawable = image_drawable;
    }

    public String getImage_text() {
        return image_text;
    }

    public void setImage_text(String image_text) {
        this.image_text = image_text;
    }
}
