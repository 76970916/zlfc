package com.xinlan.imageeditlibrary.editimage.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xinlan.imageeditlibrary.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeTableView extends LinearLayout {
    //星期
    private String[] weekTitle = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    //最大星期数
    private int weeksNum = weekTitle.length;
    //最大节数
    private int maxSection = 8;

    //圆角半径
    private int radius = 8;
    //线宽
    private int tableLineWidth = 1;
    //数字字体大小
    private int numberSize = 12;
    //标题字体大小
    private int titleSize = 10;
    //课表信息字体大小
    private int courseSize = 12;
    //底部按钮大小
    private int buttonSize = 12;

    //单元格高度
    private int cellHeight = 8;
    private int cellWidth = 10;


    private Context mContext;
    //开学日期
    private Date startDate;
    private long weekNum;

    //周次信息
    private TextView mWeekTitle;
    private LinearLayout mMainLayout;
    private RelativeLayout mTitleLayout;

    private int currentX;
    private OnbtnClickListener clickListener;

    public TimeTableView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public interface OnbtnClickListener {


    }

    public TimeTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public void setOnbtnClickListener(OnbtnClickListener listener) {
        this.clickListener = listener;
    }
    /**
     * 初始化/修改默认参数
     *
     * @param params 参数
     */


    /**
     * 数据预处理
     */
    private void preprocessorParam() {
        tableLineWidth = dip2px(tableLineWidth);
        cellHeight = dip2px(cellHeight);
        cellWidth = dip2px(cellWidth);
    }


    public void loadData() {

        flushView();
    }


    /**
     * 初始化视图
     */
    private void initView() {
        preprocessorParam();
        //课程信息
        flushView();
    }

    /**
     * 刷新课程视图
     */
    private void flushView() {
        //初始化主布局
        if (null != mMainLayout) removeView(mMainLayout);
        mMainLayout = new LinearLayout(mContext);
        mMainLayout.setOrientation(HORIZONTAL);
        mMainLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mMainLayout);


        for (int i = 1; i <= 30; i++) {
            addVerticalTableLine(mMainLayout);
            addDayCourse(mMainLayout, i);
        }

        invalidate();
    }

    /**
     * 周次标题
     *
     * @param pViewGroup
     */
    private void addWeekTitle(ViewGroup pViewGroup) {
        mTitleLayout = new RelativeLayout(mContext);
        mTitleLayout.setPadding(8, 15, 8, 15);
        mTitleLayout.setBackgroundColor(getResources().getColor(R.color.normalView));
        //周次信息
//        mWeekTitle = new TextView(mContext);
//        mWeekTitle.setTextSize(titleSize);
//        mWeekTitle.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        mWeekTitle.setGravity(Gravity.CENTER_HORIZONTAL);
//        mTitleLayout.addView(mWeekTitle);
        //左侧菜单栏

        pViewGroup.addView(mTitleLayout);
        addHorizontalTableLine(pViewGroup);
    }


    /**
     * 添加单天课程
     *
     * @param pViewGroup pViewGroup 父组件
     * @param day        星期
     */
    private void addDayCourse(ViewGroup pViewGroup, int day) {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        linearLayout.setOrientation(VERTICAL);

//        if (null != courses) {
//            for (int i = 0, size = courses.size(); i < size; i++) {
//                Course course = courses.get(i);
//                int section = course.getSection();
//
////                addCourseCell(linearLayout, course);
//              addBlankCell(linearLayout, maxSection+1);
//            }
//        } else {
//
//        }
        addBlankCell(linearLayout, maxSection, day);
        pViewGroup.addView(linearLayout);
    }


    /**
     * 添加空白块
     *
     * @param pViewGroup 父组件
     */
    private void addBlankCell(ViewGroup pViewGroup, int num, int line) {
        for (int i = 0; i < 60; i++) {
            addHorizontalTableLine(pViewGroup);
            TextView blank = new TextView(mContext);
            blank.setGravity(Gravity.CENTER);
            int position;
            if (i == 0) {
                //为0时
                if (line == 0) {
                    blank.setBackgroundColor(mContext.getResources().getColor(R.color.color_dddddd));
                } else {
                    if (line % 2 == 0) {
                        blank.setBackgroundColor(mContext.getResources().getColor(R.color.color_dddddd));
                    } else {
                        blank.setBackgroundColor(Color.WHITE);
                    }
                }
            } else if(i%2==0){
                //偶数
                if (line == 0) {
                    blank.setBackgroundColor(mContext.getResources().getColor(R.color.color_dddddd));
                } else {
                    if (line % 2 == 0) {
                        blank.setBackgroundColor(mContext.getResources().getColor(R.color.color_dddddd));
                    } else {
                        blank.setBackgroundColor(Color.WHITE);
                    }
                }
            } else {
                //奇数
                position = line * i;
                if (position % 2 == 0) {
                    blank.setBackgroundColor(Color.WHITE);
                } else {
                    blank.setBackgroundColor(mContext.getResources().getColor(R.color.color_dddddd));
                }
            }
            blank.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, cellHeight));
            pViewGroup.addView(blank);

        }
    }


    /**
     * 添加垂直线
     *
     * @param pViewGroup 父组件
     */
    private void addVerticalTableLine(ViewGroup pViewGroup) {
        View view = new View(mContext);
        view.setLayoutParams(new ViewGroup.LayoutParams(tableLineWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(getResources().getColor(R.color.viewLine));
        pViewGroup.addView(view);
    }

    /**
     * 添加水平线
     *
     * @param pViewGroup 父组件
     */
    private void addHorizontalTableLine(ViewGroup pViewGroup) {
        View view = new View(mContext);
        view.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, tableLineWidth));
        view.setBackgroundColor(getResources().getColor(R.color.viewLine));
        pViewGroup.addView(view);
    }

    /**
     * 创建TextView
     *
     * @param content 文本内容
     * @param color   字体颜色
     * @param size    字体大小
     * @param width   宽度
     * @param height  高度
     * @param weight  权重
     * @return
     */
    private TextView createTextView(String content, int size, int width, int height, int weight, int color, int bkColor) {
        TextView textView = new TextView(mContext);
        textView.setLayoutParams(new LinearLayout.LayoutParams(width, height, weight));
        if (bkColor != -1) textView.setBackgroundColor(bkColor);
        if (color != 0) {
            textView.setTextColor(color);
        }
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(size);
        textView.setText(content);
        return textView;
    }

    private void toggleWeek(int flag) {
        if (flag < 0) {
            weekNum = weekNum - 1 <= 0 ? weekNum : weekNum - 1;
        } else {
            weekNum = weekNum + 1 > 19 ? weekNum : weekNum + 1;
        }
        flushView();
    }


    private int dip2px(float dpValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                int i = (int) event.getX() - currentX;
                if (i > 30) {
                    toggleWeek(-1);
                } else if (i < -30) {
                    toggleWeek(1);
                }
                break;
        }
        return true;
    }

    public void setMaxSection(int maxSection) {
        this.maxSection = maxSection;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setTableLineWidth(int tableLineWidth) {
        this.tableLineWidth = tableLineWidth;
    }

    public void setNumberSize(int numberSize) {
        this.numberSize = numberSize;
    }

    public void setTitleSize(int titleSize) {
        this.titleSize = titleSize;
    }

    public void setCourseSize(int courseSize) {
        this.courseSize = courseSize;
    }

    public void setButtonSize(int buttonSize) {
        this.buttonSize = buttonSize;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }
}


