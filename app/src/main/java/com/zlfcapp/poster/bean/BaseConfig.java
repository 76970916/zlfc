package com.zlfcapp.poster.bean;

import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.king.base.util.StringUtils;
import com.zlfcapp.poster.App;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.utils.CommonUtils;

/**
 * @Description: 基础配置类
 * @Author: lixh
 * @CreateDate: 2020/4/2 14:36
 * @Version: 1.0
 */
public class BaseConfig {
    private static BaseConfig baseConfig;
    private String goodMessge;
    private boolean needGood;
    private String packgeName;
    private String hpstr;
    private String hprat;
    private String shareurl;

    public String getShareurl() {
        return shareurl;
    }

    public void setShareurl(String shareurl) {
        this.shareurl = shareurl;
    }

    public String getHpstr() {
        return hpstr;
    }

    public void setHpstr(String hpstr) {
        this.hpstr = hpstr;
    }

    public String getHprat() {
        return hprat;
    }

    public void setHprat(String hprat) {
        this.hprat = hprat;
    }

    public String getGoodMessge() {
        return goodMessge;
    }

    public void setGoodMessge(String goodMessge) {
        this.goodMessge = goodMessge;
    }

    public boolean isNeedGood() {
        return needGood;
    }

    public void setNeedGood(boolean needGood) {
        this.needGood = needGood;
    }

    public String getPackgeName() {
        return packgeName;
    }

    public void setPackgeName(String packgeName) {
        this.packgeName = packgeName;
    }


    public static BaseConfig getInstance(boolean reset) {
        if (baseConfig == null || reset) {
            String str = SPUtils.getInstance().getString(Constants.CONFIG_INFO, "");
            if (TextUtils.isEmpty(str)) {
                baseConfig = new BaseConfig();
            } else {
                baseConfig = new Gson().fromJson(str, BaseConfig.class);
            }
        }
        if (StringUtils.isNotBlank(baseConfig.getHprat()) && CommonUtils.generateRandom(baseConfig.getHprat())) {
            baseConfig.setNeedGood(true);
        } else {
            baseConfig.setNeedGood(false);
        }
        if (StringUtils.isEmpty(baseConfig.getPackgeName())) {
            baseConfig.setPackgeName(App.getApp().getPackageName());
        }
        if (StringUtils.isEmpty(baseConfig.getGoodMessge())) {
            baseConfig.setGoodMessge("前往商店评论");
        }
        return baseConfig;
    }

}
