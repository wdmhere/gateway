package com.ha.net.eautoopen.auth.service.base;

import com.ha.net.eautoopen.auth.service.nomarl.AuthCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.ha.net.eautoopen.constant.CacheConstant.CONSUMER_CONFIG_KEY;

@Slf4j
public class BaseAuthService {

    @Autowired
    private AuthCacheService authCacheService;

    private static Map<String,String> consumerKeys = new HashMap<>();

    protected void init(Map<String,String> consumerKeys){
        this.consumerKeys = consumerKeys;
    }

    protected String getConsumerKey(String consumer){
        String key = consumerKeys.get(consumer);
        try {
            if(!StringUtils.hasText(key)){
                log.debug("********** failed to get config key from ram ***********");
                key = authCacheService.getConsumerKey(consumer);
                consumerKeys.put(consumer+CONSUMER_CONFIG_KEY,key);
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return key;
    }


}
