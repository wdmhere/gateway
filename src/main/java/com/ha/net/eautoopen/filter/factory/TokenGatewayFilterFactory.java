package com.ha.net.eautoopen.filter.factory;

import com.alibaba.fastjson.JSON;
import com.ha.net.eautoopen.dto.Signature;
import com.ha.net.eautoopen.filter.service.CipherTextFilter;
import com.ha.net.eautoopen.filter.service.RequestAuthTokeFilter;
import com.ha.net.eautoopen.util.IPUtil;
import com.ha.net.eautoopen.vo.ErrorBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static com.ha.net.eautoopen.constant.CacheConstant.CONSUMER_PASS;

@Slf4j
public class TokenGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenGatewayFilterFactory.Config> {

    @Autowired
    private RequestAuthTokeFilter requestAuthTokeFilter;
    @Autowired
    private CipherTextFilter cipherTextFilter;

    public TokenGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

            return ((exchange, chain) -> {
                try {
                    Signature sign = requestAuthTokeFilter.filter(exchange);
                    //鉴权失败，直接返回
                    if(sign == null) throw new Exception("Login failed, please check username or password.");
                    //如果只修改请求头，需要构建新的请求头 （因为请求头是“只读”模式），更多功能请创建装饰器
                    ServerHttpRequest requestHead = exchange.getRequest().mutate().headers(
                            httpHeaders -> {
                                httpHeaders.add("IP", IPUtil.getServerIP());
//                                httpHeaders.add("","");
                            }
                    ).build();
                    //构建请求，重置请求头及请求体，包括解密等自定义动作 (请求体有问题，待解决)
//                    ServerHttpRequest discoverReqeust = buildRequest(exchange,sign);
                    //发布到ServerWebExchange
                    ServerWebExchange newServerWebExchange = exchange.mutate().request(requestHead).build();
                    //发布签名信息
                    newServerWebExchange.getAttributes().put(CONSUMER_PASS, sign);

                    return chain.filter(newServerWebExchange).then(
                            Mono.fromRunnable(()-> {
                                        requestAuthTokeFilter.initResponseHead(exchange);
                                        ServerHttpResponse response = exchange.getResponse();
    //                            response.getHeaders().add("result", URLEncoder.encode("恭喜你，响应头添加成功了~"));
                                        chain.filter(exchange.mutate().response(response).build());
                                    }
                            )
                    );
                } catch (Exception e) {
                    log.error("Failed to TokenGatewayFilterFactory.The detailed reason is as follows :");
                    log.error("Details is :" ,e);
                    ServerHttpResponse originalResponse = exchange.getResponse();
                    requestAuthTokeFilter.initResponseHead(exchange);
                    byte[] response = JSON.toJSONString(new ErrorBody("-1",e.getMessage())).getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
                    return originalResponse.writeWith(Flux.just(buffer));
                }
            });
    }


    private ServerHttpRequest buildRequest(ServerWebExchange exchange,Signature signature)throws Exception{
            return cipherTextFilter.cipherRequest(exchange,signature);
    }


    /**
     * 自定义配置参数
     */
    public static class Config{
        /**
         * 参数1
         */
        private String type;
        /**
         * 参数2
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
