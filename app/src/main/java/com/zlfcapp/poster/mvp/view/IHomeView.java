package com.zlfcapp.poster.mvp.view;

import com.xinlan.imageeditlibrary.editimage.bean.LogoTemplate;
import com.xinlan.imageeditlibrary.editimage.bean.TypeFace;
import com.xinlan.imageeditlibrary.editimage.bean.ImageResult;
import com.zlfcapp.poster.mvp.base.BaseView;

import java.util.List;

/**
 * Created by ZhangBx on 2018/7/25.
 */

public interface IHomeView extends BaseView {
    default void queryFont(List<TypeFace> list) {

    }

    default void queryFontId(String url) {

    }

    default void queryImgList(List<ImageResult> list) {

    }

    default void queryAllTemplate(List<LogoTemplate> list) {

    }

    default void onError(int code, String message) {

    }
}
