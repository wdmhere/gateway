package com.ha.net.eautoopen.constant;

public class CacheConstant {
    //登录状态
    public static final String LOGIN = "1"; //
    public static final String LOGOUT = "0";

    public static final String ENCRYPTION_AES = "01";  //加密方式
    public static final String HA = "HA";

    public static final Long EXPIRATION_TIME = 60*60*1000L;  //token刷新时间 单位：分钟
    public static final String EXPIRATION_TIME_LONG = "30";  //token失效时间 单位：天


    public static final String CONSUMER_CACHE = "CONSUMERCACHE";//原始key
    public static final String CONSUMER_CONFIG_KEY = "CONSUMERCONFIGKEY";
    public static final String CONSUMER_STATUS_INFOS = "CONSUMERSTATUS";
    public static final String CONSUMER_TOKEN = "CONSUMERTOKEN";
}
