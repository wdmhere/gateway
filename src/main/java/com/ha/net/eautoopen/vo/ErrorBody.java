package com.ha.net.eautoopen.vo;

import lombok.Data;

@Data
public class ErrorBody<T> {

    private String responseCode;

    private String responseMsg;

    private T t;

    public ErrorBody(String responseCode, String responseMsg){
        this.responseCode = responseCode;
        this.responseMsg = responseMsg;
    }

}
