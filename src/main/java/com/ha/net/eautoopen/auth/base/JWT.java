package com.ha.net.eautoopen.auth.base;

public abstract class JWT extends BaseAuthDecorator{

    public JWT(BaseAuth baseAuth) {
        super(baseAuth);
    }

    public abstract void firstLogin();

}
