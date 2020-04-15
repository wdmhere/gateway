package com.ha.net.eautoopen.filter;


import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * TOKEN过滤器
 */
@Component
public class TokenFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //白名单or黑名单

        //获取请求头参数

        //首次登录or查询登录态

        //鉴权

        //创建令牌

        //设置响应头


        return null;
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
