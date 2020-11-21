package com.xinlan.imageeditlibrary.editimage.utils;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

import recycler.coverflow.CoverFlowLayoutManger;

public class CustomLinearLayoutManager extends CoverFlowLayoutManger {

    private boolean isScrollEnabled = true;

    public CustomLinearLayoutManager(boolean isFlat, boolean isGreyItem, boolean isAlphaItem, float cstInterval) {
        super(isFlat, isGreyItem, isAlphaItem, cstInterval);
    }


    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }

}
