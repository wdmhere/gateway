package com.ha.net.eautoopen.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Slf4j
//@Controller
public class TestController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private void excute(){

        String reuslt = stringRedisTemplate.opsForValue().get("asdfadfadfa");
        System.out.println(reuslt);

    }



}
