package com.ha.net.eautoopen.auth.service.nomarl.impl;

import com.alibaba.fastjson.JSON;
import com.ha.net.eautoopen.auth.service.base.BaseAuthService;
import com.ha.net.eautoopen.auth.service.nomarl.AuthCacheService;
import com.ha.net.eautoopen.auth.service.nomarl.NormalBuildTokenService;
import com.ha.net.eautoopen.dto.ConsumerCache;
import com.ha.net.eautoopen.dto.Payload;
import com.ha.net.eautoopen.dto.Signature;
import com.ha.net.eautoopen.util.AesEncryptUtils;
import com.ha.net.eautoopen.util.DateCalcUtil;
import com.ha.net.eautoopen.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

import static com.ha.net.eautoopen.constant.CacheConstant.*;

@Slf4j
@RefreshScope
@Service
public class NormalBuildTokenServiceImpl extends BaseAuthService implements NormalBuildTokenService {

    @Value("${sign.expiration.time:60}")
    private String EXPIRATION_TIME;  //token刷新时间 单位：分钟

    @Autowired
    private AuthCacheService authCacheService;


    @Override
    public Signature firstBuild(String consumer, ConsumerCache consumerCache) throws Exception {

        String consumerKey = getConsumerKey(consumer);

        ConsumerCache cache = initConsumerCache(consumerCache);

        Signature signature = pakage(consumer,cache);

        String token = produceToken(signature,consumerKey);

        signature.setSgn(token);

        initCache(consumer,cache);

        return signature;
    }


    @Override
    public Signature build(String consumer, ConsumerCache consumerCache) throws Exception {

        Signature signature = pakage2(consumer,consumerCache);

        String token = produceDynToken(signature,consumerCache);

        signature.setSgn(token);

        return signature;
    }


    /**
     *  生成认证信息 ， 针对首次登录
     * @param consumer
     * @param consumerCache
     * @return
     * @throws Exception
     */
    private Signature pakage(String consumer,ConsumerCache consumerCache) throws Exception {
        Signature signature = new Signature();
        String dyn = buildDyn(consumer);
        String jti = String.valueOf(RandomUtils.nextInt());
        log.info("当前动态密钥为：" + dyn + " ; Token id ：" + jti);
        Long nowTime = System.currentTimeMillis();
        signature.setPky(getConsumerKey(consumer));
        signature.setAlg(ENCRYPTION_AES);
        Payload payload = new Payload();
        payload.setApt(consumer);
        payload.setAud(consumer);
        payload.setDyn(dyn);
        payload.setExp(DateCalcUtil.formatDatetime(new Date(nowTime)));
        payload.setIss(HA);
        payload.setJti(jti);
        signature.setPld(payload);

        consumerCache.setSign(dyn);
        consumerCache.setIsLogin(LOGIN);
        consumerCache.setJwtid(jti);
        consumerCache.setName(consumer);
        consumerCache.setTime(String.valueOf(nowTime));

        return signature;
    }


    /**
     *  生成认证信息，刷新缓存
     * @param consumer
     * @param consumerCache
     * @return
     * @throws Exception
     */
    private Signature pakage2(String consumer,ConsumerCache consumerCache) throws Exception {
        Signature signature = new Signature();
        signature.setAlg(ENCRYPTION_AES);
        signature.setPky(consumerCache.getSign());
        Payload payload = new Payload();
        payload.setApt(consumer);
        payload.setAud(consumer);
        payload.setIss(HA);
        //是否过期
        Boolean isExpired = false;
        Long nowTime = System.currentTimeMillis();
        if(StringUtils.hasText(consumerCache.getTime()) && Long.valueOf(consumerCache.getTime()) > 0L &&
                nowTime - Long.valueOf(consumerCache.getTime()) > (Long.valueOf(EXPIRATION_TIME)*60*1000L)){
            isExpired = true;
        }
        //过期需要重新生成新的
        if(isExpired){
            String dyn = buildDyn(consumer);
            String jti = String.valueOf(RandomUtils.nextInt());
            ConsumerCache cache = new ConsumerCache();
            cache.setSign(dyn);
            cache.setIsLogin(LOGIN);
            cache.setJwtid(jti);
            cache.setName(consumer);
            cache.setTime(String.valueOf(nowTime));
            //刷新缓存
            if(refreshCache(consumer,cache)){
                payload.setDyn(dyn);
                payload.setExp(DateCalcUtil.formatDatetime(new Date(nowTime)));
                payload.setJti(jti);
            }
        }else {
            //没有过期则不处理
        }
        signature.setPld(payload);
        return signature;
    }


    /**
     * 创建token
     * @param signature
     * @param consumerKey
     * @return
     * @throws Exception
     */
    private String produceToken(Signature signature,String consumerKey)throws Exception{
        String old = signature.getAlg()+"."+ JSON.toJSONString(signature.getPld());
        return AesEncryptUtils.encrypt(old, MD5Util.encoderMD5(consumerKey));
    }

    private String produceDynToken(Signature signature,ConsumerCache consumerCache)throws Exception{
        if(signature == null || signature.getPld() == null || !StringUtils.hasText(signature.getPld().getDyn())) return "";
        String old = signature.getAlg()+"."+ JSON.toJSONString(signature.getPld());
        log.debug("produceDynToken`key is = " +consumerCache.getSign());
        return AesEncryptUtils.encrypt(old, consumerCache.getSign());
    }

    /**
     * 生成动态密钥
     * @param consumer
     * @return
     * @throws Exception
     */
    private String buildDyn(String consumer)throws Exception{
        String dynKey = MD5Util.encoderMD5(consumer + "-" + getConsumerKey(consumer) + "-" + UUID.randomUUID().toString()).substring(0, 16);
        return dynKey;
    }


    /**
     * 刷新缓存
     * @param consumer
     * @param consumerCache
     */
    private void initCache(String consumer,ConsumerCache consumerCache){
        try {
            authCacheService.initConsumerCache(consumer,consumerCache != null ? JSON.toJSONString(consumerCache) : null);
        } catch (Exception e) {
            log.error("初始化用户状态缓存异常：",e);
        }
    }


    /**
     * 刷新缓存
     * @param consumer
     * @param consumerCache
     */
    private Boolean refreshCache(String consumer,ConsumerCache consumerCache){
        try {
            if(!StringUtils.hasText(consumerCache.getTime()))
                throw new Exception("token缓存中失效时间为空！");
            return authCacheService.setConsumerCache(consumer,consumerCache != null ? JSON.toJSONString(consumerCache) : "");
        } catch (Exception e) {
            log.error("刷新用户状态缓存异常：",e);
        }
        return false;
    }


    private ConsumerCache initConsumerCache(ConsumerCache consumerCache){
        if(consumerCache == null)
            return new ConsumerCache();
        return consumerCache;
    }

}
