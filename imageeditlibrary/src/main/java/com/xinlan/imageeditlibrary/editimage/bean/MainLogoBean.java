package com.xinlan.imageeditlibrary.editimage.bean;


import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Date;
import java.util.List;

/**
 * @Description: 主logo表
 * @Author: lixh
 * @CreateDate: 2020/10/28 15:07
 * @Version: 1.0
 */
public class MainLogoBean extends LitePalSupport {

    private int id;

    private String name;

    private String remark;

    private Date createtime;

    private int state;


    // 类型 1 - 5
    private int type;
    // 2  是线上数据 不可修改，可创建备份
    // 1 是本地数据可修改
    private int online;

    private String preViewPath;

    private String itemBgColor;
    private byte[] image;


    @Column(ignore = true)
    public boolean exit;

    @Column(ignore = true)
    public List<LogoBean> list;


    public String getItemBgColor() {
        return itemBgColor;
    }

    public void setItemBgColor(String itemBgColor) {
        this.itemBgColor = itemBgColor;
    }

    public String getPreViewPath() {
        return preViewPath;
    }

    public void setPreViewPath(String preViewPath) {
        this.preViewPath = preViewPath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "MainLogoBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", remark='" + remark + '\'' +
                ", createtime=" + createtime +
                ", state=" + state +
                ", type=" + type +
                ", online=" + online +
                ", preViewPath='" + preViewPath + '\'' +
                '}';
    }
}
