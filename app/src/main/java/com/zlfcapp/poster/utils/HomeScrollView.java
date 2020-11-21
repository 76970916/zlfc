package com.zlfcapp.poster.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class HomeScrollView extends NestedScrollView {
    /**
     * 设置滚动接口
     *
     * @param onScrollListener
     */
    private int lastScrollY;
    private OnScrollMoveListener listener;
    public void setOnScrollListener(OnScrollMoveListener onScrollListener) {
        this.listener = onScrollListener;
    }
    public interface  OnScrollMoveListener{
        void moveListener(int y);

        void touch();
    }

    public HomeScrollView(@NonNull Context context) {
        super(context);
    }

    public HomeScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            lastScrollY = this.getScrollY();
            if (listener != null) {
                listener.moveListener(lastScrollY);
            }
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (listener != null) {
                listener.touch();
            }
        }
        return super.onTouchEvent(ev);
    }
}
