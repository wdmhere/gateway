package com.ha.net.eautoopen.auth.service.nomarl.impl;

import com.alibaba.fastjson.JSON;
import com.ha.net.eautoopen.auth.service.base.BaseAuthService;
import com.ha.net.eautoopen.auth.service.nomarl.AuthCacheService;
import com.ha.net.eautoopen.auth.service.nomarl.NormalLoginService;
import com.ha.net.eautoopen.dto.ConsumerCache;
import com.ha.net.eautoopen.dto.Payload;
import com.ha.net.eautoopen.dto.Signature;
import com.ha.net.eautoopen.util.AesEncryptUtils;
import com.ha.net.eautoopen.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.ha.net.eautoopen.constant.CacheConstant.CONSUMER_CONFIG_KEY;

/**
 * 解密方式由过滤器做判断
 */
@Slf4j
@Service
public class NormalLoginServiceImpl extends BaseAuthService implements NormalLoginService {

    @Autowired
    private AuthCacheService authCacheService;

    private Map<String,String> keyMap;

    /**
     * 首次登录，仅需要根据双方约定的私钥能匹配上签名中的申请方即可
     * @param consumer
     * @param headInfos
     * @return
     * @throws Exception
     */
    @Override
    public Signature firstLogin(String consumer, String headInfos) throws Exception {
        //获取缓存中的约定私钥
        String key = getPrivateKey(consumer);
        //保存约定私钥，后续不再读取缓存
        setKeyMap(consumer,key);
        //解析签名,首次登录签名即用户标识
        Signature signature = parsingToken(key,headInfos);

//        //首次登录根据约定规则“用户标识/私钥”进行MD564位加密生成密钥
//        String param = AesEncryptUtils.decrypt(headInfos,MD5Util.encoderMD5(key));
//        Signature signature = new Signature();
//        Payload payload = new Payload();
//        payload.setApt(param);
//        signature.setPld(payload);

        return signature;
    }


    /**
     * 在签名失效前，非第一次登录，需要通过动态密钥进行解密
     * @param consumer
     * @param headInfos
     * @return
     * @throws Exception
     */
    @Override
    public Signature login(String consumer,String headInfos,ConsumerCache consumerCache) throws Exception {
        //获取动态密钥
        String sign = consumerCache.getSign();
        //解析签名
        Signature signature = parsingToken(sign,headInfos);

        return signature;
    }

    /**
     * 解析token
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    private Signature parsingToken(String key, String value)throws Exception{
        //根据约定规则“用户标识/私钥”进行MD564位加密生成密钥
        String realkey = MD5Util.encoderMD5(key);
        //根据密钥解密
        String param = AesEncryptUtils.decrypt(value,realkey);
        //获得明文
        Signature signature =  JSON.parseObject(param, Signature.class);

        return signature;
    }


    private void setKeyMap(String name, String key)throws Exception{
        if(keyMap == null)
            keyMap = new HashMap<String,String>();
        keyMap.put(name+CONSUMER_CONFIG_KEY,key);
        super.init(keyMap);
    }

    /**
     * 获取后台配置私钥
     * 注意需要捕获异常，如果redis宕机，系统保证从内存中获取私钥，用户需每次通过私钥登录。
     * @param consumer
     * @return
     */
    private String getPrivateKey(String consumer){
        try {
            return  authCacheService.getConsumerKey(consumer);
        } catch (Exception e) {
            log.error("获取私钥异常，请检查redis是否出现异常：",e);
        }
        return null;
    }

}
