package com.ha.net.eautoopen.vo;

import lombok.Data;

@Data
public class RequestHead<T,K> extends VO {

    private String consumer;
    //false->logout;true->login
    private Boolean isLogin;

    private T signInfo;

    private K cacheInfo;
    //Encryption Algorithm
    private String alg;

}
