package com.ha.net.eautoopen.auth.service.nomarl.impl;

import com.ha.net.eautoopen.auth.service.nomarl.NormalVerifyTokenService;
import com.ha.net.eautoopen.dto.ConsumerCache;
import com.ha.net.eautoopen.dto.Signature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class NormalVerifyTokenServiceImpl implements NormalVerifyTokenService {


    /**
     * 首次登录，通过约定私钥验签
     * @param signature
     * @param key
     * @return
     * @throws Exception
     */
    @Override
    public Boolean verifyToken1(Signature signature, String key) throws Exception {
        if(!StringUtils.hasText(key) || signature == null) return false;
        if(key.equals(signature.getPld().getAud())) return true;
        return false;
    }

    /**
     * 使用请求令牌中的  jti  与缓存中的jti 进行认证
     * @param signature
     * @param consumerCache
     * @return
     * @throws Exception
     */
    @Override
    public Boolean verifyToken2(Signature signature, ConsumerCache consumerCache) throws Exception {
        if(consumerCache == null || signature == null) return false;
        if(!StringUtils.hasText(consumerCache.getJwtid())){ log.error("token ID 出现缺失，请检查系统是否出现异常！"); return  false;}
        if(consumerCache.getJwtid().equals(signature.getPld().getJti())
                && consumerCache.getName().equals(signature.getPld().getAud())) return true;

        return false;
    }
}
