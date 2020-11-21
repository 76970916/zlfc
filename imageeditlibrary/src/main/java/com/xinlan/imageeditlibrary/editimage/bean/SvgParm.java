package com.xinlan.imageeditlibrary.editimage.bean;

import com.pixplicity.sharp.Sharp;
import com.xinlan.imageeditlibrary.editimage.view.StickerItem;
import com.xinlan.imageeditlibrary.editimage.view.StickerView;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: svg 参数传递
 * @Author: lixh
 * @CreateDate: 2020/11/10 16:27
 * @Version: 1.0
 */
public class SvgParm implements Serializable {

    private List<Integer> listColors;

    private StickerItem selectItem;

    private Sharp mSharp;

    private StickerView stickerView;

    private LogoBean logoBean;

    public LogoBean getLogoBean() {
        return logoBean;
    }

    public void setLogoBean(LogoBean logoBean) {
        this.logoBean = logoBean;
    }

    public StickerView getStickerView() {
        return stickerView;
    }

    public void setStickerView(StickerView stickerView) {
        this.stickerView = stickerView;
    }

    public List<Integer> getListColors() {
        return listColors;
    }

    public void setListColors(List<Integer> listColors) {
        this.listColors = listColors;
    }

    public StickerItem getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(StickerItem selectItem) {
        this.selectItem = selectItem;
    }

    public Sharp getmSharp() {
        return mSharp;
    }

    public void setmSharp(Sharp mSharp) {
        this.mSharp = mSharp;
    }
}
