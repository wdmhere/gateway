package com.ha.net.eautoopen.auth.base;

public abstract class BaseAuth {

    public abstract void check();

    public abstract void login();

    public abstract String buildToken();

    public abstract void verifyToken();

}
