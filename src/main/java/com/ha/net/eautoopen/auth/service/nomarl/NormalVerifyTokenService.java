package com.ha.net.eautoopen.auth.service.nomarl;

import com.ha.net.eautoopen.dto.Signature;

public interface NormalVerifyTokenService {

    public Boolean verifyToken1(Signature signature,String key);

    public Boolean verifyToken2(Signature signature, String jwtid,String name);

}
