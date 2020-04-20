package com.ha.net.eautoopen.auth.jwt.base;

import com.ha.net.eautoopen.dto.Signature;
import com.ha.net.eautoopen.vo.RequestHead;

public abstract class JwtBaseAuth {

    public abstract Boolean check(RequestHead requestHead);

    public abstract Signature decrypt(RequestHead requestHead);

    public abstract <T>Boolean verifyToken(T t,String... keys);

    public abstract Signature buildToken(RequestHead requestHead);

}
