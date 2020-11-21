package com.xinlan.imageeditlibrary.editimage.bean;

import org.litepal.crud.LitePalSupport;

public class ImageResult extends LitePalSupport {
    int id;
    String url;
    String type;
    String createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
