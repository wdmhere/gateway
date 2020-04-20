package com.ha.net.eautoopen.auth.handler;

import com.ha.net.eautoopen.auth.jwt.base.JwtBaseAuth;
import com.ha.net.eautoopen.auth.jwt.decorator.JwtBaseAuthDecorator;
import com.ha.net.eautoopen.auth.service.nomarl.NormalBuildTokenService;
import com.ha.net.eautoopen.auth.service.nomarl.NormalLoginService;
import com.ha.net.eautoopen.auth.service.nomarl.NormalVerifyTokenService;
import com.ha.net.eautoopen.dto.ConsumerCache;
import com.ha.net.eautoopen.dto.Signature;
import com.ha.net.eautoopen.vo.RequestHead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * JWT通用处理类，对登录，验签，生成令牌进行增强
 */
@Slf4j
public class JwtAuthStandardHandler extends JwtBaseAuthDecorator {

    @Autowired
    private NormalBuildTokenService normalBuildTokenService;
    @Autowired
    private NormalLoginService normalLoginService;
    @Autowired
    private NormalVerifyTokenService verifyTokenService;

    public JwtAuthStandardHandler(JwtBaseAuth jwtBaseAuth) {
        super(jwtBaseAuth);
    }


    @Override
    public Signature decrypt(RequestHead requestHead) {
        if(requestHead.getIsLogin()){
           return super.decrypt(requestHead);
        }else {
           return firstLogin(requestHead);
        }
    }

    @Override
    public <T> Boolean verifyToken(T t,String... keys) {
        if (t instanceof Signature) {
            int signpost = keys.length;
            switch (signpost) {
                case 1:
                    return super.verifyToken(t, keys);
                case 2:
                    return verifyTokenService.verifyToken2((Signature) t, keys[0], keys[1]);
            }
        }
        return false;
    }

    @Override
    public Signature buildToken(RequestHead requestHead) {
        if(requestHead.getIsLogin()){
            return super.buildToken(requestHead);
        }else {
            return firstBuild(requestHead);
        }
    }

    private Signature firstLogin(RequestHead requestHead) {
        try {
            return normalLoginService.firstLogin(requestHead.getConsumer(),(String)requestHead.getSignInfo());
        } catch (Exception e) {
            log.error("首次登录失败：",e);
        }
        return null;
    }

    private Signature firstBuild(RequestHead requestHead) {
        try {
            return normalBuildTokenService.firstBuild(requestHead.getConsumer(),(ConsumerCache)requestHead.getCacheInfo());
        } catch (Exception e) {
            log.error("首次构建Token失败：",e);
        }
        return null;
    }
}
