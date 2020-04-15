package com.ha.net.eautoopen.auth.handler;

import com.ha.net.eautoopen.auth.base.BaseAuth;
import com.ha.net.eautoopen.auth.base.JWT;

public class JWTAuthStandardHandler extends JWT {


    public JWTAuthStandardHandler(BaseAuth baseAuth) {
        super(baseAuth);
    }

    @Override
    public void firstLogin() {

    }

    @Override
    public void check() {

    }

    @Override
    public void login() {

    }

    @Override
    public String buildToken() {
        return null;
    }

    @Override
    public void verifyToken() {

    }
}
