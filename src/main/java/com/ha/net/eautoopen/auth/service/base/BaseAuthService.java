package com.ha.net.eautoopen.auth.service.base;

import java.util.Map;

public class BaseAuthService {

    private Map<String,String> consumerKeys;

    protected void init(Map<String,String> consumerKeys){
        this.consumerKeys = consumerKeys;
    }

    protected String getConsumerKey(String consumer){
        return consumerKeys.get(consumer);
    }


}
