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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

import static com.ha.net.eautoopen.constant.CacheConstant.*;

@Slf4j
@Service
public class NormalBuildTokenServiceImpl extends BaseAuthService implements NormalBuildTokenService {

    @Autowired
    private AuthCacheService authCacheService;


    @Override
    public Signature firstBuild(String consumer, ConsumerCache consumerCache) throws Exception {

        String consumerKey = getConsumerKey(consumer);

        Signature signature = pakage(consumer,consumerCache);

        String token = produceToken(signature,consumerKey);

        signature.setSgn(token);

        initCache(consumer,consumerCache);

        return signature;
    }


    @Override
    public Signature build(String consumer, ConsumerCache consumerCache) throws Exception {

        Signature signature = pakage2(consumer,consumerCache);

        String token = produceToken(signature,consumerCache.getSign());

        signature.setSgn(token);

        refreshCache(consumer,consumerCache);

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
        Long nowTime = System.currentTimeMillis();
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
     *  生成认证信息
     * @param consumer
     * @param consumerCache
     * @return
     * @throws Exception
     */
    private Signature pakage2(String consumer,ConsumerCache consumerCache) throws Exception {
        Signature signature = new Signature();
        signature.setAlg(ENCRYPTION_AES);

        String dyn = "";
        String jti = "";
        Long nowTime = System.currentTimeMillis();

        //是否过期
        Boolean isExpired = false;

        if(StringUtils.hasText(consumerCache.getTime()) &&
                System.currentTimeMillis() - Long.valueOf(consumerCache.getTime()) > EXPIRATION_TIME ){
            isExpired = true;
        }

        //过期需要重新生成新的
        if(isExpired){
            dyn = buildDyn(consumer);
            jti = String.valueOf(RandomUtils.nextInt());
            consumerCache.setSign(dyn);
            consumerCache.setIsLogin(LOGIN);
            consumerCache.setJwtid(jti);
            consumerCache.setName(consumer);
            consumerCache.setTime(String.valueOf(nowTime));
        }else {
            //没有过期则不处理
        }

        Payload payload = new Payload();
        payload.setApt(consumer);
        payload.setAud(consumer);
        payload.setDyn(isExpired ? dyn : payload.getDyn());
        payload.setExp(isExpired ? DateCalcUtil.formatDatetime(new Date(nowTime)) : payload.getExp());
        payload.setIss(HA);
        payload.setJti(isExpired ? jti : payload.getJti());
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
        return AesEncryptUtils.encrypt(old, consumerKey);
    }

    private String produceDynToken(Signature signature)throws Exception{
        String old = signature.getAlg()+"."+ JSON.toJSONString(signature.getPld());
        return AesEncryptUtils.encrypt(old, signature.getPld().getDyn());
    }

    /**
     * 生成动态密钥
     * @param consumer
     * @return
     * @throws Exception
     */
    private String buildDyn(String consumer)throws Exception{
        return MD5Util.encoderMD5(consumer+"-"+getConsumerKey(consumer)+"-"+UUID.randomUUID().toString()).substring(0,16);
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
            log.error("刷新用户状态缓存异常：",e);
        }
    }


    /**
     * 刷新缓存
     * @param consumer
     * @param consumerCache
     */
    private void refreshCache(String consumer,ConsumerCache consumerCache){
        try {
            if(!StringUtils.hasText(consumerCache.getTime()))
                throw new Exception("token缓存中失效时间为空！");
            if(StringUtils.hasText(consumerCache.getTime()) &&
                    System.currentTimeMillis() - Long.valueOf(consumerCache.getTime()) > EXPIRATION_TIME ){
                authCacheService.setConsumerCache(consumer,consumerCache != null ? JSON.toJSONString(consumerCache) : null);
            }
        } catch (Exception e) {
            log.error("刷新用户状态缓存异常：",e);
        }
    }
}
