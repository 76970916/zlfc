package com.zlfcapp.poster.http;

public class HttpCodeConstant {
    /**
     * Unauthorized/未授权
     */
    public static final String UNAUTHORIZED = "未授权";
    public static final int UNAUTHORIZED_CODE = 401;


    /**
     * Forbidden/禁止
     */
    public static final String FORBIDDEN = "禁止访问";
    public static final int FORBIDDEN_CODE = 403;

    /**
     * Not Found/未找到
     */
    public static final String NOT_FOUND = "访问地址不存在";
    public static final int NOT_FOUND_CODE = 404;

    /**
     * Method Not Allowed/方法未允许
     */
    public static final String METHOD_NOT_ALLOWED = "请求方法错误";
    public static final int METHOD_NOT_ALLOWED_CODE = 405;

    /**
     * Request Timeout/请求超时
     */
    public static final String REQUEST_TIMEOUT = "请求超时";
    public static final int REQUEST_TIMEOUT_CODE = 408;

    /**
     * Stringernal Server Error/内部服务器错误
     */
    public static final String INTERNAL_SERVER_ERROR = "服务器内部出错";
    public static final int INTERNAL_SERVER_ERROR_CODE = 500;
    public static final int BAD_GATEWAY_CODE = 502;

    /**
     * 网络连接错误
     */
    public static final String BAD_NET_WORK = "网络连接错误";
    public static final int SERVER_UNAVAILABLE_CODE = 503;
    public static final int GATEWATTIMEDOUT_CODE = 504;

    /**
     * 数据解析出错
     */
    public static final String PARSE_ERROR = "数据解析出错";
    public static final int PARSE_ERROR_CODE = 4008;


    /**
     * 未知错误
     */
    public static final String UNKNOW_ERROR = "未知错误";

    /**
     * 请求出错
     */
    public static final String BAD_REQUEST = "请求出错";
    public static final int BAD_REQUEST_CODE = 400;

    /**
     * 解析实体类为空
     */
    public static final String EMPTY_ERROR = "数据为空";
    public static final int EMPTY_ERROR_CODE = 4011;
}
