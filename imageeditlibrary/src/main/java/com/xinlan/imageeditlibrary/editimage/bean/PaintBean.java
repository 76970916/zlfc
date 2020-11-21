package com.xinlan.imageeditlibrary.editimage.bean;

public class PaintBean {
    float eventX;
    float eventY;
    float lastX;
    float lastY;
    int paintColor;

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    public float getEventX() {
        return eventX;
    }

    public void setEventX(float eventX) {
        this.eventX = eventX;
    }

    public float getEventY() {
        return eventY;
    }

    public void setEventY(float eventY) {
        this.eventY = eventY;
    }

    public float getLastX() {
        return lastX;
    }

    public void setLastX(float lastX) {
        this.lastX = lastX;
    }

    public float getLastY() {
        return lastY;
    }

    public void setLastY(float lastY) {
        this.lastY = lastY;
    }
}
