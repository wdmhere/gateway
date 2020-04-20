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
     * 支持三种认证ID
     * @param signature
     * @param key
     * @return
     * @throws Exception
     */
    @Override
    public Boolean verifyToken1(Signature signature, String key){
        if(!StringUtils.hasText(key) || signature == null) return false;
        if(key.equals(signature.getPld().getJti())
                || key.equals(signature.getPld().getApt())
                || key.equals(signature.getPld().getAud())) return true;
        return false;
    }

    /**
     * 使用请求令牌中的  jti  与缓存中的jti 进行认证，
     * @param signature
     * @return
     * @throws Exception
     */
    @Override
    public Boolean verifyToken2(Signature signature, String name, String jwtid){
        if(signature == null) return false;
        if(!StringUtils.hasText(name)){ log.error("认证渠道 出现缺失，请检查系统是否出现异常！"); return  false;}
        if(!StringUtils.hasText(jwtid)){ log.error("token ID 出现缺失，请检查系统是否出现异常！"); return  false;}

        if(name.equals(signature.getPld().getAud())
                && jwtid.equals(signature.getPld().getJti())) return true;
        return false;
    }
}
