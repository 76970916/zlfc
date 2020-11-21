package com.xinlan.imageeditlibrary.editimage.bean;

import org.litepal.crud.LitePalSupport;

/**
 * @Description: 素材图片表
 * @Author: lixh
 * @CreateDate: 2020/10/17 15:34
 * @Version: 1.0
 */

public class ImageLogo extends LitePalSupport {

    private int id;
    private String imgname;
    private String url;
    private String imgsize;
    private String type;
    private int onlineid;
    private boolean selected;
    private String thumb_url;

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getOnlineid() {
        return onlineid;
    }

    public void setOnlineid(int onlineid) {
        this.onlineid = onlineid;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setImgname(String imgname) {
        this.imgname = imgname;
    }

    public String getImgname() {
        return imgname;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getImgsize() {
        return imgsize;
    }

    public void setImgsize(String imgsize) {
        this.imgsize = imgsize;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ImageLogo{" +
                "id=" + id +
                ", imgname='" + imgname + '\'' +
                ", url='" + url + '\'' +
                ", imgsize=" + imgsize +
                ", type='" + type + '\'' +
                ", onlineid=" + onlineid +
                '}';
    }
}