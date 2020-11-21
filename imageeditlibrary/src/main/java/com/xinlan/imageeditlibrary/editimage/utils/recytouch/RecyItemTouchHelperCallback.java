package com.xinlan.imageeditlibrary.editimage.utils.recytouch;

import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.bean.ImageBean;
import com.xinlan.imageeditlibrary.editimage.view.StickerItem;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags;

public class RecyItemTouchHelperCallback extends ItemTouchHelper.Callback {
    RecyclerView.Adapter mAdapter;
    boolean isSwipeEnable;
    boolean isFirstDragUnable;
    OnCallBack callBack;
    LinkedHashMap<Integer, StickerItem> bank;
    public void setCallBackListener(OnCallBack callBackListener){
        callBack = callBackListener;
    }
    public  interface OnCallBack{
        void callBackData(LinkedHashMap<Integer, StickerItem> bank,int fromPosition , int toPosition);
    }
    public  void setStickerItem(LinkedHashMap<Integer, StickerItem> bank){
        this.bank = bank;
    }
    public RecyItemTouchHelperCallback(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        isSwipeEnable = true;
        isFirstDragUnable = false;
    }

    public RecyItemTouchHelperCallback(RecyclerView.Adapter adapter, boolean isSwipeEnable, boolean isFirstDragUnable) {
        mAdapter = adapter;
        this.isSwipeEnable = isSwipeEnable;
        this.isFirstDragUnable = isFirstDragUnable;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.d("logd", "getMovementFlags()");
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            // 为0 取消左右滑动
            swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (bank != null ) {
            if (bank.size() != 0) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                StickerItem itemFrom = bank.get(fromPosition);
                StickerItem itemTo = bank.get(toPosition);
                if (isFirstDragUnable && toPosition == 0) {
                    return false;
                }
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(((BaseQuickAdapter) mAdapter).getData(), i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(((BaseQuickAdapter) mAdapter).getData(), i, i - 1);
                    }
                }
                bank.put(fromPosition, itemTo);
                bank.put(toPosition, itemFrom);
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                if (callBack != null) {
                    callBack.callBackData(bank,fromPosition,toPosition);
                }
                return true;
            }

        }
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
    }

}
