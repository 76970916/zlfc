package com.xinlan.imageeditlibrary.editimage.bean;

public class MessageEvent {
    private String message;
    private Object obj;

    public MessageEvent() {
    }

    public MessageEvent(String message, Object obj) {
        this.message = message;
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
