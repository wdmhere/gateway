package com.ha.net.eautoopen.filter.factory;

import com.alibaba.fastjson.JSON;
import com.ha.net.eautoopen.auth.handler.JwtAuthStandardHandler;
import com.ha.net.eautoopen.dto.Signature;
import com.ha.net.eautoopen.filter.service.RequestAuthTokeFilter;
import com.ha.net.eautoopen.util.IPUtil;
import com.ha.net.eautoopen.vo.ErrorBody;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.UrlBase64;
import org.bouncycastle.util.encoders.UrlBase64Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class TokenGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenGatewayFilterFactory.Config> {

    @Autowired
    private RequestAuthTokeFilter requestAuthTokeFilter;

    public TokenGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            Signature sign = requestAuthTokeFilter.filter(exchange);
            //鉴权失败，直接返回
            if(null == sign){
                ServerHttpResponse originalResponse = exchange.getResponse();
                byte[] response = JSON.toJSONString(new ErrorBody("-1","Login failed, please check username or password.")).getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
                return originalResponse.writeWith(Flux.just(buffer));
            }
            //如果要修改请求头，需要构建新的请求头 （因为请求头是“只读”模式）
            ServerHttpRequest requestHead = exchange.getRequest().mutate().headers(
                    httpHeaders -> {
                        httpHeaders.add("IP", IPUtil.getServerIP());
//                        httpHeaders.add("","");
                    }
            ).build();
            ServerWebExchange newServerWebExchange = exchange.mutate().request(requestHead).build();
            newServerWebExchange.getAttributes().put("passSign", sign);

            return chain.filter(newServerWebExchange).then(
                Mono.fromRunnable(()-> {
                            requestAuthTokeFilter.initResponseHead(newServerWebExchange);
                            ServerHttpResponse response = newServerWebExchange.getResponse();
//                            response.getHeaders().add("result", URLEncoder.encode("恭喜你，响应头添加成功了~"));
                            chain.filter(exchange.mutate().response(response).build());
                        }
                )
            );
        });
    }




    /**
     * 自定义配置参数
     */
    public static class Config{
        /**
         * 过滤类型
         */
        private String type;
        /**
         * 操作
         */
        private String op;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }
    }

}
