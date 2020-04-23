package com.ha.net.eautoopen.auth.service.nomarl;

import com.ha.net.eautoopen.dto.ConsumerCache;

public interface AuthCacheService {

    public String getConsumerKey(String consumer) throws Exception;

    public ConsumerCache getConsumerCache(String consumer) throws Exception;

    public void initConsumerCache(String consumer,String infos) throws Exception;

    public Boolean setConsumerCache(String consumer,String infos) throws Exception;



}
