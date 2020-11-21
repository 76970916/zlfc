package com.zlfcapp.poster.mvp.view;

import com.zlfcapp.poster.bean.FeedBacks;
import com.zlfcapp.poster.mvp.base.BaseView;

public interface IFeedBack extends BaseView {
    void OnFeedBackResut(String result);

    void getFeedbackList(FeedBacks feedBacks);
}
