package com.ha.net.eautoopen.filter;


import com.alibaba.fastjson.JSON;
import com.ha.net.eautoopen.auth.handler.JwtAuthStandardHandler;
import com.ha.net.eautoopen.auth.jwt.base.JwtBaseAuth;
import com.ha.net.eautoopen.auth.jwt.base.impl.JwtGeneralAuth;
import com.ha.net.eautoopen.config.exception.BussinessException;
import com.ha.net.eautoopen.dto.ConsumerCache;
import com.ha.net.eautoopen.dto.Signature;
import com.ha.net.eautoopen.vo.RequestHead;
import com.ha.net.eautoopen.vo.ErrorBody;
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
//@Component
public class TokenFilter implements GlobalFilter, Ordered {

    @Value("${jwt.wihte.list}")
    private String whiteList;
    @Value("${jwt.black.list}")
    private String blackList;

    @Autowired
    private JwtAuthStandardHandler jwtBaseAuth;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Boolean odd = false;
        ErrorBody errorBody = null;
        Signature rspSigntaure = null;
        try {
            //白名单or黑名单

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

        } catch (BussinessException be){
            odd = true;
            errorBody = new ErrorBody(be.getErrorCode(),be.getErrorMessage());
        } catch (Exception e) {
            log.error("Token 过滤器处理异常：",e);
            odd = true;
            errorBody = new ErrorBody("-1","System abnormality");
        }finally {
            //设置响应头
            setRespHead(exchange,rspSigntaure);
        }
        if(odd){
            ServerHttpResponse originalResponse = exchange.getResponse();
            byte[] response = JSON.toJSONString(errorBody).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
            return originalResponse.writeWith(Flux.just(buffer));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
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
        if(head.getCacheInfo() != null)
            head.getCacheInfo().setIsLogin(isCheck ? LOGIN : LOGOUT);
    }




    private Signature bulidToken(Boolean isAuth, JwtBaseAuth jwtBaseAuth, RequestHead<String, ConsumerCache> head)throws Exception{
        if(isAuth){
            return jwtBaseAuth.buildToken(head);
        }else {
            throw new BussinessException("-1","登录失败，请检查登录用户或密钥!");
        }
    }



    private void setRespHead(ServerWebExchange exchange,Signature rspSigntaure){
        ServerHttpResponse originalResponse = exchange.getResponse();
        originalResponse.setStatusCode(HttpStatus.OK);
        HttpHeaders httpHeaders = originalResponse.getHeaders();
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
        if(rspSigntaure == null) return;
        httpHeaders.add("Authorization",rspSigntaure.getSgn());
        httpHeaders.add("Consumer",rspSigntaure.getPld().getApt());
        httpHeaders.add("Alg",rspSigntaure.getAlg());
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


}

