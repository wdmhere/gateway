package com.ha.net.eautoopen.auth.jwt.decorator;

import com.ha.net.eautoopen.auth.jwt.base.JwtBaseAuth;
import com.ha.net.eautoopen.dto.Signature;
import com.ha.net.eautoopen.vo.RequestHead;

public class JwtBaseAuthDecorator extends JwtBaseAuth {

    JwtBaseAuth jwtBaseAuth;

    public JwtBaseAuthDecorator(JwtBaseAuth jwtBaseAuth){
        this.jwtBaseAuth = jwtBaseAuth;
    }

    @Override
    public Boolean check(RequestHead requestHead) {
        return this.jwtBaseAuth.check(requestHead);
    }

    @Override
    public Signature decrypt(RequestHead requestHead) {
        return this.jwtBaseAuth.decrypt(requestHead);
    }

    @Override
    public <T> Boolean verifyToken(T t, String... keys) {
        return this.jwtBaseAuth.verifyToken(t,keys);
    }

    @Override
    public Signature buildToken(RequestHead requestHead) {
        return this.jwtBaseAuth.buildToken(requestHead);
    }

}
