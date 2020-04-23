package com.ha.net.eautoopen.filter.service;


import com.alibaba.fastjson.JSON;
import com.ha.net.eautoopen.auth.handler.JwtAuthStandardHandler;
import com.ha.net.eautoopen.auth.jwt.base.JwtBaseAuth;
import com.ha.net.eautoopen.config.exception.BussinessException;
import com.ha.net.eautoopen.dto.ConsumerCache;
import com.ha.net.eautoopen.dto.Signature;
import com.ha.net.eautoopen.vo.ErrorBody;
import com.ha.net.eautoopen.vo.RequestHead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static com.ha.net.eautoopen.constant.CacheConstant.*;

/**
 * TOKEN全局过滤器
 */
@Slf4j
@RefreshScope
@Component
public class RequestAuthTokeFilter {

    @Value("${jwt.wihte.list}")
    private String whiteList;
    @Value("${jwt.black.list}")
    private String blackList;

    @Autowired
    private JwtAuthStandardHandler jwtBaseAuth;

    public Signature filter(ServerWebExchange exchange) {
        Signature rspSigntaure = null;
        try {
            //白名单or黑名单
            whiteList(exchange);
            //获取请求头参数
            RequestHead<String, ConsumerCache> head = getHeadInfos(exchange);
            //初始化调度器
//            JwtBaseAuth jwtBaseAuth = new JwtAuthStandardHandler(new JwtGeneralAuth());
            //查询登录态
            check(jwtBaseAuth, head);
            //解密
            Signature signature = decrypt(head);
            //鉴权
            Boolean isAuth = verify(signature, head);
            //创建令牌
            rspSigntaure = bulidToken(isAuth, jwtBaseAuth, head);

        } catch (Exception e) {
            log.error("Token auth fail ：",e);
        }
        return rspSigntaure;
    }


    private void whiteList(ServerWebExchange exchange){
        String url = exchange.getRequest().getURI().getPath();
    }


    private RequestHead<String, ConsumerCache> getHeadInfos(ServerWebExchange exchange)throws Exception{
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");//token
        String consumer = exchange.getRequest().getHeaders().getFirst("Consumer"); //用户
        String alg = exchange.getRequest().getHeaders().getFirst("Alg");    //算法
        RequestHead<String, ConsumerCache> head = new RequestHead();
        head.setSignInfo(token);
        head.setConsumer(consumer);
        head.setAlg(alg);
        return head;
    }



    private void check(JwtBaseAuth jwtBaseAuth,RequestHead<String, ConsumerCache> head)throws Exception {
        Boolean isCheck = jwtBaseAuth.check(head);
        log.info("当前用户"+(isCheck ? "已" : "没有") + "保持登录");
        head.setIsLogin(isCheck);
        if(head.getCacheInfo() != null) {
            head.getCacheInfo().setIsLogin(isCheck ? LOGIN : LOGOUT);
        }
    }



    private Signature bulidToken(Boolean isAuth, JwtBaseAuth jwtBaseAuth, RequestHead<String, ConsumerCache> head)throws Exception{
        if(isAuth){
            return jwtBaseAuth.buildToken(head);
        }else {
            throw new BussinessException("-1","登录失败，请检查登录用户或密钥!");
        }
    }


    private Signature decrypt(RequestHead<String, ConsumerCache> head)throws Exception{
        if(!ENCRYPTION_AES.equals(head.getAlg())){
            throw new BussinessException("-1","加密方式不匹配，请重新选择。");
        }
        //解密
        return jwtBaseAuth.decrypt(head);
    }


    private Boolean verify(Signature signature,RequestHead<String, ConsumerCache> head){
        if(head.getCacheInfo() != null){
            return jwtBaseAuth.verifyToken(signature, head.getConsumer(),head.getCacheInfo().getJwtid());
        }else {
            return jwtBaseAuth.verifyToken(signature, head.getConsumer());
        }
    }


    public ServerWebExchange initResponseHead(ServerWebExchange exchange ){
        Signature rspSigntaure = exchange.getAttribute(CONSUMER_PASS);
        ServerHttpResponse originalResponse = exchange.getResponse();
        HttpHeaders respHeaders = originalResponse.getHeaders();
        originalResponse.setStatusCode(HttpStatus.OK);
        respHeaders.clear();
        respHeaders.add("Content-Type", "application/json;charset=UTF-8");
        respHeaders.add("Authorization",rspSigntaure.getSgn());
        respHeaders.add("Consumer",rspSigntaure.getPld().getAud());
        respHeaders.add("Alg",rspSigntaure.getAlg());

        return exchange;
    }



}

