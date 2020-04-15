package com.ha.net.eautoopen.dto;

import lombok.Data;

@Data
public class ConsumerCache {
    //用户名
    private String name;
    //登录状态 0否 1是
    private String isLogin;
    //动态令牌
    private String sign;
    //编号
    private String jwtid;
    //失效时间
    private String time;
}
