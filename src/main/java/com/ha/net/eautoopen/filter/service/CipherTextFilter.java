package com.ha.net.eautoopen.filter.service;

import com.ha.net.eautoopen.dto.MGLog;
import com.ha.net.eautoopen.dto.Signature;
import com.ha.net.eautoopen.service.mongo.MongoProviderService;
import com.ha.net.eautoopen.util.AesEncryptUtils;
import com.ha.net.eautoopen.util.DateCalcUtil;
import com.ha.net.eautoopen.util.MD5Util;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static com.ha.net.eautoopen.constant.CacheConstant.CONSUMER_PASS;

@Slf4j
@Component
public class CipherTextFilter {

    @Value("${mongodb.expiretime:90}")
    private String MgExpireTime;
    @Autowired
    private MongoProviderService mongoProviderService;

    /**
     * 请求体处理（解密）
     * @param exchange
     * @return
     */
    public ServerHttpRequest cipherRequest(ServerWebExchange exchange,Signature signature)throws Exception{
        ServerHttpRequest request = exchange.getRequest();
        if(!HttpMethod.POST.equals(request.getMethod())) throw new Exception("请求方式不正确，请更改为POST请求。");
        //获取密钥
        String key = getKey(signature);
        //获取请求体内容
        String requestParam = getBodyParams(request);
        //解密
        String outReq = AesEncryptUtils.decrypt(requestParam, MD5Util.encoderMD5(key));
        //保存请求日志
        saveMGLog(new Date(),null,request.getHeaders().getFirst("Consumer"),outReq);
        //创建缓冲区
        DataBuffer bodyDataBuffer = stringBuffer(outReq);
        Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
        ServerHttpRequest newRequest = new ServerHttpRequestDecorator(request) {
            //下面的将请求体再次封装写回到request里，传到下一级，否则，由于请求体已被消费，后续的服务将取不到值
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = request.getHeaders().getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return bodyFlux;
            }
        };
        return newRequest;
    }

    /**
     * 响应体处理（加密）
     * @param exchange
     * @return
     */
    public ServerHttpResponseDecorator cipherResponse(ServerWebExchange exchange,Signature signature)throws Exception{

        ServerHttpResponse response = exchange.getResponse();
        //获取密钥
        String key = getKey(signature);
        //获取请求体内容
//        String requestParam = getBodyParams(response.get());


        return null;
    }


    private String getKey(Signature signature)throws Exception{
        if(signature == null) throw new Exception("用户认证信息丢失！");
        if(StringUtils.hasText(signature.getPky())){
            return signature.getPky();
        }else if(signature.getPld() != null && StringUtils.hasText(signature.getPld().getDyn())){
            return signature.getPld().getDyn();
        }else {
            throw new Exception("用户认证信息缺失，密钥信息缺失。");
        }
    }


    /**
     * 获取请求体内的Fulx，并重新封装成一个新的返回
     * @return
     */
    private String getBodyParams(ServerHttpRequest request){

        return "";
    }

    private DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }

    private void saveMGLog(Date createDate,Date updateDate,String... params)throws Exception{
        String consumer = "";
        String outReq = params[1];
        String outResp = params[2];
        int len = params.length;
        switch (len){
            case 1 : consumer = params[0]; break;
            case 2 : consumer = params[0] ; outReq = params[1]; break;
            case 3 : consumer = params[0] ; outReq = params[1]; outResp = params[2]; break;
            default: throw new Exception("Failed to Save MG_LOG, please check your params.");
        }

        MGLog mgLog = new MGLog();
        mgLog.setConsumer(consumer); //用户
        if(StringUtils.hasText(outReq)) mgLog.setReqOut(outReq);
        if(StringUtils.hasText(outResp)) mgLog.setResOut(outResp);
        if(createDate != null) mgLog.setCreateDate(createDate);
        if(updateDate != null) mgLog.setUpdateDate(updateDate);
        if(createDate != null){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH,Integer.parseInt(MgExpireTime)/30 );
            calendar.set(Calendar.HOUR_OF_DAY, 1);//控制时
            calendar.set(Calendar.MINUTE, 0);//控制分
            calendar.set(Calendar.SECOND, 0);//控制秒
            mgLog.setExpireTime(calendar.getTime());
        }
        mongoProviderService.save(mgLog);
    }


}
