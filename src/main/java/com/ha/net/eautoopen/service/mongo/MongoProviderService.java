package com.ha.net.eautoopen.service.mongo;

public interface MongoProviderService {

    public <T>void save(T t);

    public <T>void insert(T t);

}
