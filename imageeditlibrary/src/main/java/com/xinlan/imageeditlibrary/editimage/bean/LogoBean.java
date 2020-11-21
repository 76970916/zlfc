package com.xinlan.imageeditlibrary.editimage.bean;

import android.graphics.Bitmap;

import org.litepal.crud.LitePalSupport;

import java.util.Date;


/**
 * @Description: 编辑logo 对象
 * @Author: lixh
 * @CreateDate: 2020/10/28 11:16
 * @Version: 1.0
 */

public class LogoBean extends LitePalSupport {

    private int id;
    private int fid;
    private String svgUrl;
    private String path;
    private Bitmap bitmap;
    private int type;
    private Date createtime;
    private Date updatetime;
    private float bitmapWidth;
    private float bitmapHeight;
    //   文字 、文字颜色、文字字体 关联、大小、透明度
    private String text;
    private int textColor;
    private int textfontid;
    private float textSize;
    private int textAlpha;
    private boolean textBold;
    private boolean textEm;
    private float lineSpacing;

    private String bgcolor;
    private int tabWith;
    private int tabHeight;
    //    原始图片坐标
    private int leftRect;
    private int topRect;
    private int rightRect;
    private int bottomRect;

    //    绘制目标坐标
    private float leftRectF;
    private float topRectF;
    private float rightRectF;
    private float bottomRectF;

    private int percentHeight;
    private int percentWith;

    private String svgContent;
    private String pngUrl;
    private String svgName;
    private int mAlpha = -1;
    // 2  是线上数据 不可修改，可创建备份
    // 1 是本地数据可修改
    private int online;


    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getmAlpha() {
        return mAlpha;
    }

    public void setmAlpha(int mAlpha) {
        this.mAlpha = mAlpha;
    }

    public boolean isTextBold() {
        return textBold;
    }

    public void setTextBold(boolean textBold) {
        this.textBold = textBold;
    }

    public boolean isTextEm() {
        return textEm;
    }

    public void setTextEm(boolean textEm) {
        this.textEm = textEm;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public String getSvgName() {
        return svgName;
    }

    public void setSvgName(String svgName) {
        this.svgName = svgName;
    }

    public String getPngUrl() { return pngUrl;
    }

    public void setPngUrl(String pngUrl) { this.pngUrl = pngUrl;
    }

    public String getSvgContent() { return svgContent;
    }

    public void setSvgContent(String svgContent) {
        this.svgContent = svgContent;
    }

    public String getSvgUrl() {
        return svgUrl;
    }

    public void setSvgUrl(String svgUrl) {
        this.svgUrl = svgUrl;
    }

    public int getTabWith() {
        return tabWith;
    }

    public void setTabWith(int tabWith) {
        this.tabWith = tabWith;
    }

    public int getTabHeight() {
        return tabHeight;
    }

    public void setTabHeight(int tabHeight) {
        this.tabHeight = tabHeight;
    }

    public int getPercentHeight() {
        return percentHeight;
    }

    public void setPercentHeight(int percentHeight) {
        this.percentHeight = percentHeight;
    }

    public int getPercentWith() {
        return percentWith;
    }

    public void setPercentWith(int percentWith) {
        this.percentWith = percentWith;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getTextAlpha() {
        return textAlpha;
    }

    public void setTextAlpha(int textAlpha) {
        this.textAlpha = textAlpha;
    }

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

    public int getTextfontid() {
        return textfontid;
    }

    public void setTextfontid(int textfontid) {
        this.textfontid = textfontid;
    }

    public String getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public float getBitmapWidth() {
        return bitmapWidth;
    }

    public void setBitmapWidth(float bitmapWidth) {
        this.bitmapWidth = bitmapWidth;
    }

    public float getBitmapHeight() {
        return bitmapHeight;
    }

    public void setBitmapHeight(float bitmapHeight) {
        this.bitmapHeight = bitmapHeight;
    }

    public int getLeftRect() {
        return leftRect;
    }

    public void setLeftRect(int leftRect) {
        this.leftRect = leftRect;
    }

    public int getTopRect() {
        return topRect;
    }

    public void setTopRect(int topRect) {
        this.topRect = topRect;
    }

    public int getRightRect() {
        return rightRect;
    }

    public void setRightRect(int rightRect) {
        this.rightRect = rightRect;
    }

    public int getBottomRect() {
        return bottomRect;
    }

    public void setBottomRect(int bottomRect) {
        this.bottomRect = bottomRect;
    }

    public float getLeftRectF() {
        return leftRectF;
    }

    public void setLeftRectF(float leftRectF) {
        this.leftRectF = leftRectF;
    }

    public float getTopRectF() {
        return topRectF;
    }

    public void setTopRectF(float topRectF) {
        this.topRectF = topRectF;
    }

    public float getRightRectF() {
        return rightRectF;
    }

    public void setRightRectF(float rightRectF) {
        this.rightRectF = rightRectF;
    }

    public float getBottomRectF() {
        return bottomRectF;
    }

    public void setBottomRectF(float bottomRectF) {
        this.bottomRectF = bottomRectF;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    private byte[] Image;

    public byte[] getImage() {
        return Image;
    }

    public void setImage(byte[] image) {
        Image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "LogoBean{" +
                "id=" + id +
                ", fid=" + fid +
                ", path='" + path + '\'' +
                ", type=" + type +
                ", createtime=" + createtime +
                ", updatetime=" + updatetime +
                '}';
    }
}
