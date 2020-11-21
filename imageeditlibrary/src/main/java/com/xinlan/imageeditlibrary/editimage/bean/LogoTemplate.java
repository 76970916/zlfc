package com.xinlan.imageeditlibrary.editimage.bean;

import org.litepal.crud.LitePalSupport;

/**
 * @Description: logo模板表
 * @Author: lixh
 * @CreateDate: 2020/10/26 14:20
 * @Version: 1.0
 */
public class LogoTemplate extends LitePalSupport {

    private int id;

    private String svgname;

    private String svgurl;

    private int width;

    private int height;

    private int coordx;

    private int coordy;

    private String text;

    private String textColor;

    private int textfontid;

    private String bgcolor;

    private int type;

    private String remark;

    private String remark1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSvgname() {
        return svgname;
    }

    public void setSvgname(String svgname) {
        this.svgname = svgname;
    }

    public String getSvgurl() {
        return svgurl;
    }

    public void setSvgurl(String svgurl) {
        this.svgurl = svgurl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCoordx() {
        return coordx;
    }

    public void setCoordx(int coordx) {
        this.coordx = coordx;
    }

    public int getCoordy() {
        return coordy;
    }

    public void setCoordy(int coordy) {
        this.coordy = coordy;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", svgname='" + svgname + '\'' +
                ", svgurl='" + svgurl + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", coordx=" + coordx +
                ", coordy=" + coordy +
                ", text='" + text + '\'' +
                ", textColor='" + textColor + '\'' +
                ", textfontid=" + textfontid +
                ", bgcolor='" + bgcolor + '\'' +
                ", type=" + type +
                ", remark='" + remark + '\'' +
                ", remark1='" + remark1 + '\'' +
                '}';
    }
}
