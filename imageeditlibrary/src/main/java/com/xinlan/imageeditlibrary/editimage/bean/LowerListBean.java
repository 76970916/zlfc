package com.xinlan.imageeditlibrary.editimage.bean;

import org.litepal.crud.LitePalSupport;

public class LowerListBean extends LitePalSupport {
    int id;
    int l_id;
    String url;
    String content;
    String createTime;
    int layout_x;
    int layout_y;
    int textsize;
    int LineSpacing;
    int TextColor;
    int TextAlpha;
    String text;
    boolean TextBold;
    boolean TextEm;
    boolean longitudinal;
    int textfontid;
    int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getL_id() {
        return l_id;
    }

    public void setL_id(int l_id) {
        this.l_id = l_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getLayout_x() {
        return layout_x;
    }

    public void setLayout_x(int layout_x) {
        this.layout_x = layout_x;
    }

    public int getLayout_y() {
        return layout_y;
    }

    public void setLayout_y(int layout_y) {
        this.layout_y = layout_y;
    }

    public int getTextsize() {
        return textsize;
    }

    public void setTextsize(int textsize) {
        this.textsize = textsize;
    }

    public int getLineSpacing() {
        return LineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        LineSpacing = lineSpacing;
    }

    public int getTextColor() {
        return TextColor;
    }

    public void setTextColor(int textColor) {
        TextColor = textColor;
    }

    public int getTextAlpha() {
        return TextAlpha;
    }

    public void setTextAlpha(int textAlpha) {
        TextAlpha = textAlpha;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isTextBold() {
        return TextBold;
    }

    public void setTextBold(boolean textBold) {
        TextBold = textBold;
    }

    public boolean isTextEm() {
        return TextEm;
    }

    public void setTextEm(boolean textEm) {
        TextEm = textEm;
    }

    public boolean isLongitudinal() {
        return longitudinal;
    }

    public void setLongitudinal(boolean longitudinal) {
        this.longitudinal = longitudinal;
    }

    public int getTextfontid() {
        return textfontid;
    }

    public void setTextfontid(int textfontid) {
        this.textfontid = textfontid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
