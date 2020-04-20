package com.ha.net.eautoopen.auth.service.nomarl.impl;

import com.alibaba.fastjson.JSON;
import com.ha.net.eautoopen.auth.service.nomarl.AuthCacheService;
import com.ha.net.eautoopen.dto.ConsumerCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

import static com.ha.net.eautoopen.constant.CacheConstant.*;

@Slf4j
@Service
public class AuthCacheServiceImpl implements AuthCacheService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取原始key
     * @param consumer
     * @return
     * @throws Exception
     */
    @Override
    public String getConsumerKey(String consumer) throws Exception {
//        return redisTemplate.opsForValue().get(consumer+CONSUMER_CONFIG_KEY);
        return "wdm-key";
    }


    /**
     * 通过用户标识，获取到缓存中用户登录态
     * 格式：  {"","",""} json字符串，对应对象 ConsumerCache
     * islogin = 登录状态 0 否 1 是；token 每次生成的动态令牌
     * 示例：0:arfgaretagwfadfgagadfadfaewrw
     * @param consumer
     * @return
     */
    @Override
    public ConsumerCache getConsumerCache(String consumer) throws Exception {
        String str = redisTemplate.opsForValue().get(consumer+CONSUMER_CACHE);
        if (StringUtils.hasText(str)) {
            return JSON.parseObject(str,ConsumerCache.class);
        }
        return null;
    }


    @Override
    public void initConsumerCache(String consumer, String infos) throws Exception {
        redisTemplate.opsForValue().set(consumer+CONSUMER_CACHE,infos,Long.valueOf(EXPIRATION_TIME_LONG),TimeUnit.DAYS);
    }

    /**
     * 缓存用户登录信息 对应对象：ConsumerCache
     * @param consumer
     * @param infos
     * @throws Exception
     */
    @Override
    public void setConsumerCache(String consumer, String infos) throws Exception {
        redisTemplate.opsForValue().set(consumer+CONSUMER_CACHE,infos);
    }


}
