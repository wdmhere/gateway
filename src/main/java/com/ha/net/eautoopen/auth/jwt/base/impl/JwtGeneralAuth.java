package com.ha.net.eautoopen.auth.jwt.base.impl;

import com.ha.net.eautoopen.auth.jwt.base.JwtBaseAuth;
import com.ha.net.eautoopen.auth.service.nomarl.NormalBuildTokenService;
import com.ha.net.eautoopen.auth.service.nomarl.NormalCheckService;
import com.ha.net.eautoopen.auth.service.nomarl.NormalLoginService;
import com.ha.net.eautoopen.auth.service.nomarl.NormalVerifyTokenService;
import com.ha.net.eautoopen.dto.ConsumerCache;
import com.ha.net.eautoopen.dto.Signature;
import com.ha.net.eautoopen.vo.RequestHead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ha.net.eautoopen.constant.CacheConstant.LOGIN;

@Slf4j
@Service
public class JwtGeneralAuth extends JwtBaseAuth {

    @Autowired
    private NormalBuildTokenService normalBuildTokenService;
    @Autowired
    private NormalCheckService normalCheckService;
    @Autowired
    private NormalLoginService normalLoginService;
    @Autowired
    private NormalVerifyTokenService normalVerifyTokenService;


    @Override
    public Boolean check(RequestHead requestHead) {
        try {
            ConsumerCache consumerCache = normalCheckService.check(requestHead.getConsumer());
            if(consumerCache != null &&  LOGIN.equals(consumerCache.getIsLogin())){
                requestHead.setCacheInfo(consumerCache);
                log.debug("缓存中的数据时："+consumerCache);
                return true;
            }
        } catch (Exception e) {
            log.error("登录检查状态失败：",e);
        }
        return false;
    }

    @Override
    public Signature decrypt(RequestHead requestHead) {
        try {
            return normalLoginService.login(requestHead.getConsumer(),(String)requestHead.getSignInfo(),(ConsumerCache)requestHead.getCacheInfo());
        } catch (Exception e) {
            log.error("鉴权登录失败：",e);
        }
        return null;
    }

    @Override
    public Signature buildToken(RequestHead requestHead) {
        try {
            return normalBuildTokenService.build(requestHead.getConsumer(),(ConsumerCache)requestHead.getCacheInfo());
        } catch (Exception e) {
            log.error("鉴权构建Token失败：",e);
        }
        return null;
    }

    @Override
    public <T> Boolean verifyToken(T t, String... keys) {
        Boolean result = false;
        try {
            if(t instanceof Signature){
                result = normalVerifyTokenService.verifyToken1((Signature)t,keys[0]);
            }
        } catch (Exception e) {
            log.error("验证Token失败：",e);
        }
        return result;
    }
}
