package com.xinlan.imageeditlibrary.editimage.bean;


import org.litepal.crud.LitePalSupport;

/**
 * @Description: 颜色抽象类
 * @Author: lixh
 * @CreateDate: 2020/9/25 10:41
 * @Version: 1.0
 */
public class TextColor extends LitePalSupport {
    private int id;

    private int color;

    private String name;

    private String remark;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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
}
