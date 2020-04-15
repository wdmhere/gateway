package com.ha.net.eautoopen.dto;

import lombok.Data;

@Data
public class Payload {
    //申请人
    private String apt;
    //签发人
    private String iss ;
    //过期时间 yyyy-mmm-dd hh:mm:ss
    private String exp ;
    // 受众
    private String aud;
    //(JWT ID)：编号
    private String jti ;
    //动态密钥
    private String dyn ;
}
