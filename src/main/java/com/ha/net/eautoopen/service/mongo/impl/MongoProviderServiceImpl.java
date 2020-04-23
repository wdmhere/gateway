package com.ha.net.eautoopen.service.mongo.impl;

import com.ha.net.eautoopen.service.mongo.MongoProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * 其他操作待后续添加
 */
@Slf4j
@Service
public class MongoProviderServiceImpl implements MongoProviderService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public <T> void save(T t) {
        mongoTemplate.save(t);
    }

    @Override
    public <T> void insert(T t) {
        mongoTemplate.insert(t);
    }
}
