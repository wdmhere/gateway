package com.ha.net.eautoopen.auth.service.base;

import java.util.HashMap;
import java.util.Map;

public class BaseAuthService {

    private static Map<String,String> consumerKeys = new HashMap<>();

    protected void init(Map<String,String> consumerKeys){
        this.consumerKeys = consumerKeys;
    }

    protected String getConsumerKey(String consumer){
        return consumerKeys.get(consumer);
    }


}
