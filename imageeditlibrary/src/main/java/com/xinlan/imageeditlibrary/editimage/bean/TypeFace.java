package com.xinlan.imageeditlibrary.editimage.bean;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
 * @Description: 字体抽象类
 * @Author: lixh
 * @CreateDate: 2020/9/24 19:10
 * @Version: 1.0
 */

public class TypeFace extends LitePalSupport {

    private int id;
    private String typeName;
    private boolean isSelect;
    private String onlineUrl;
    private String remark;
    private Date createTime;
    private String localpath;
    private String fonturl;
    private String fontname;
    private String fontsize;
    private String unit;
    // 已存在 101  没有下载 102
    private int fontstate;
    private int onlineid;

    public int getOnlineid() {
        return onlineid;
    }

    public void setOnlineid(int onlineid) {
        this.onlineid = onlineid;
    }

    public int getFontstate() {
        return fontstate;
    }

    public void setFontstate(int fontstate) {
        this.fontstate = fontstate;
    }

    public void setFonturl(String fonturl) {
        this.fonturl = fonturl;
    }

    public String getFonturl() {
        return fonturl;
    }

    public void setFontname(String fontname) {
        this.fontname = fontname;
    }

    public String getFontname() {
        return fontname;
    }

    public void setFontsize(String fontsize) {
        this.fontsize = fontsize;
    }

    public String getFontsize() {
        return fontsize;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public String getLocalpath() {
        return localpath;
    }

    public void setLocalpath(String localpath) {
        this.localpath = localpath;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getOnlineUrl() {
        return onlineUrl;
    }

    public void setOnlineUrl(String onlineUrl) {
        this.onlineUrl = onlineUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "TypeFace{" +
                "id=" + id +
                ", typeName='" + typeName + '\'' +
                ", isSelect=" + isSelect +
                ", onlineUrl='" + onlineUrl + '\'' +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", localpath='" + localpath + '\'' +
                ", fonturl='" + fonturl + '\'' +
                ", fontname='" + fontname + '\'' +
                ", fontsize='" + fontsize + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
