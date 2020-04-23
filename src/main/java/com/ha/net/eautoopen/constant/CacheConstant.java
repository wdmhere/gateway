package com.ha.net.eautoopen.constant;

public class CacheConstant {
    //登录状态
    public static final String LOGIN = "1"; //
    public static final String LOGOUT = "0";

    public static final String ENCRYPTION_AES = "01";  //加密方式
    public static final String HA = "HA";

    public static final String EXPIRATION_TIME_LONG = "30";  //token失效时间 单位：天

    public static final String CONSUMER_TOKEN_LOCK = "CONSUMER_TOKEN_LOCK";
    public static final String CONSUMER_CACHE = "CONSUMERCACHE";//动态缓存KEY
    public static final String CONSUMER_CONFIG_KEY = "CONSUMERCONFIGKEY";//原始key
    public static final String CONSUMER_PASS = "CONSUMERPASS";//认证过的用户信息


}
