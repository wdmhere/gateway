package com.ha.net.eautoopen.filter;

import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RequestBodyFilter2  {


    private ServerHttpResponseDecorator responseDecorator(ServerWebExchange exchange) {
        return new ServerHttpResponseDecorator(exchange.getResponse()) {
            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = serverHttpResponse.bufferFactory();

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                return super.writeWith(DataBufferUtils.join(Flux.from(body))
                        .map(dataBuffer -> {

                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            DataBufferUtils.release(dataBuffer);
                            return content;

                        }).flatMap(bytes -> {

//                            MediaType mediaType = serverHttpResponse.getHeaders().getContentType();
//                            if (null == mediaType || (!mediaType.includes(MediaType.APPLICATION_JSON) && !mediaType.includes(MediaType.APPLICATION_JSON_UTF8))) {
//
//                            } else {
//                                String bodyString = "";
//                                int length = bytes.length;
//
//                                if (!ObjectUtils.isEmpty(exchange.getResponse().getHeaders().get(HttpHeaders.CONTENT_ENCODING))
//                                        && exchange.getResponse().getHeaders().get(HttpHeaders.CONTENT_ENCODING).contains("gzip")) {
//                                    GZIPInputStream gzipInputStream = null;
//                                    try {
//                                        gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes), length);
//                                        StringWriter writer = new StringWriter();
//                                        IOUtils.copy(gzipInputStream, writer, StandardCharsets.UTF_8);
//                                        bodyString = writer.toString();
//
//                                    } catch (IOException e) {
//                                        log.error("====Gzip IO error", e);
//                                    } finally {
//                                        if (gzipInputStream != null) {
//                                            try {
//                                                gzipInputStream.close();
//                                            } catch (IOException e) {
//                                                log.error("===Gzip IO close error", e);
//                                            }
//                                        }
//                                    }
//                                } else {
                            String bodyString = new String(bytes, StandardCharsets.UTF_8);
//                                }

                                log.info("bodyString: {}", bodyString);
//                            }


                            return Mono.just(bufferFactory.wrap(bytes));
                        }));
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };
    }

//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        if (HttpMethod.POST.equals(exchange.getRequest().getMethod())) {
//            String a = DataBufferUtils.join(exchange.getRequest().getBody()).map(dataBuffer -> {
//                byte[] bytes = new byte[dataBuffer.readableByteCount()];
//                dataBuffer.read(bytes);
//                DataBufferUtils.release(dataBuffer);
//                return bytes;
//            }).toString();
////                return chain.filter(exchange.mutate().request(generateNewRequest(exchange.getRequest(), bodyBytes)).build());
//        }
//        return null;
//    }
//
//    private ServerHttpRequest generateNewRequest(ServerHttpRequest request, byte[] bytes) {
//        URI ex = UriComponentsBuilder.fromUri(request.getURI()).build(true).toUri();
//        ServerHttpRequest newRequest = request.mutate().uri(ex).build();
//        DataBuffer dataBuffer = stringBuffer(bytes);
//        Flux<DataBuffer> flux = Flux.just(dataBuffer);
//        newRequest = new ServerHttpRequestDecorator(newRequest) {
//            @Override
//            public Flux<DataBuffer> getBody() {
//                return flux;
//            }
//        };
//        return newRequest;
//    }
//
//    private DataBuffer stringBuffer(byte[] bytes) {
//        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
//        return nettyDataBufferFactory.wrap(bytes);
//    }



}
