/**
 * Copyright 2020 bejson.com
 */
package com.zlfcapp.poster.bean;

import java.util.Date;

public class Results {

    private int aid;
    private String appName;
    private String c_number;
    private String c_name;
    private String device;
    private String ip;
    private String content;
    private Date createTime;
    private String img;
    private int type;

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getAid() {
        return aid;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setC_number(String c_number) {
        this.c_number = c_number;
    }

    public String getC_number() {
        return c_number;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getC_name() {
        return c_name;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDevice() {
        return device;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}