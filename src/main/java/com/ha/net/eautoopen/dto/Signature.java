package com.ha.net.eautoopen.dto;

import lombok.Data;

@Data
public class Signature {
    //加密算法  AES
    private String alg;
    //授权信息
    private Payload pld;
    //签名
    private String sgn;
    //约定私钥
    private String pky;
}
