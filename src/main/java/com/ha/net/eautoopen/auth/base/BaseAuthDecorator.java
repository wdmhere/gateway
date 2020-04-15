package com.ha.net.eautoopen.auth.base;

public abstract class BaseAuthDecorator extends BaseAuth {

    BaseAuth baseAuth;

    public BaseAuthDecorator(BaseAuth baseAuth){
        this.baseAuth = baseAuth;
    }

}
