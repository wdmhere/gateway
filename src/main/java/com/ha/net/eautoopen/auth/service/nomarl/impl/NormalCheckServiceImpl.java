package com.ha.net.eautoopen.auth.service.nomarl.impl;

import com.ha.net.eautoopen.auth.service.nomarl.AuthCacheService;
import com.ha.net.eautoopen.auth.service.nomarl.NormalCheckService;
import com.ha.net.eautoopen.dto.ConsumerCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ha.net.eautoopen.constant.CacheConstant.LOGIN;


@Slf4j
@Service
public class NormalCheckServiceImpl implements NormalCheckService {
    @Autowired
    public AuthCacheService authCacheService;

    /**
     * 查询缓存中的用户状态数据
     * @param consumer
     * @return
     * @throws Exception
     */
    @Override
    public ConsumerCache check(String consumer) throws Exception {
        return authCacheService.getConsumerCache(consumer);
    }

}
