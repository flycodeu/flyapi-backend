package com.fly.flyapigateway;

import cn.hutool.core.net.URLDecoder;
import com.fly.flyapiclientsdk.utils.SignUtils;
import com.fly.flyapicommon.model.entity.InterfaceInfo;
import com.fly.flyapicommon.model.entity.User;
import com.fly.flyapicommon.service.InnerInterfaceInfoService;
import com.fly.flyapicommon.service.InnerUserInterfaceInfoService;
import com.fly.flyapicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1", "localhost");
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference
    private InnerUserService innerUserService;
    private static final String INTERFACE_HOST = "http://localhost:8123";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //用户发布请求到网关(√)

        //记录日志
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识" + request.getId());
        log.info("请求路径" + path);
        log.info("请求方法" + method);
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址" + request.getLocalAddress().getHostString());
        log.info("请求参数" + request.getQueryParams());

        // 响应对象
        ServerHttpResponse response = exchange.getResponse();
        //黑白名单
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            System.out.println("ok1");
            return response.setComplete();
        }

        //用户鉴权（ak，sk是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        // todo
        // 防止中文乱码
        String body = null;
        try {
            body = URLDecoder.decode(headers.getFirst("body"), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        // todo 从数据库中查询
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null || invokeUser.getId() < 0) {
            return handleNoAuth(response);
        }
        long userId = invokeUser.getId().longValue();
/*
        if (!Objects.equals(accessKey, "fly")) {
            System.out.println("ok2");
            return handleNoAuth(response);
        }
*/

        if (Long.parseLong(nonce) > 100000) {
            System.out.println(nonce);
            return handleNoAuth(response);
        }


        // 时间和当前时间不能超过5分钟
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        final long FIVE_MINUTES = 60 * 5L;
        if ((currentTimeMillis - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            System.out.println("ok4");
            return handleNoAuth(response);
        }


        // 判断密钥
        String secretKey = invokeUser.getSecretKey();
        String validSecret = SignUtils.getSign(body, secretKey);
        if (!Objects.equals(sign, validSecret)) {
            System.out.println("ok5");
            return handleNoAuth(response);
        }

        //请求的模拟接口是否存在
        //todo 从数据库中查取接口存在，以及post,get方法
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null || interfaceInfo.getId() <= 0) {
            return handleNoAuth(response);
        }
        //todo 是否有调用次数
        boolean canInvoke = innerUserInterfaceInfoService.canInvoke(interfaceInfo.getId(), userId);
        if (!canInvoke) {
            return handleNoAuth(response);
        }
        long interfaceInfoId = interfaceInfo.getId().longValue();

        //存在，调用模拟接口（请求转发）
        //  Mono<Void> filter = chain.filter(exchange);
        //响应日志
        return handleResponse(exchange, chain, interfaceInfoId, userId);
        //调用成功接口调用次数+1
/*        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("ok");
        } else {        //调用失败返回规范错误码
            handleInvokeError(response);
        }


        return filter;*/
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            // 从交换机拿到原始response
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓冲区工厂 拿到缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到状态码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        // 对象是响应式的
                        if (body instanceof Flux) {
                            // 我们拿到真正的body
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里面写数据
                            // 拼接字符串
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // todo 7. 调用成功，接口调用次数+1
                                try {
                                    innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                } catch (Exception e) {
                                    log.error("invoke count error", e);
                                }
                                // data从这个content中读取
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);// 释放掉内存
                                // 6.构建日志
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                String data = new String(content, StandardCharsets.UTF_8);// data
                                rspArgs.add(data);
                                log.info("<--- status:{} data:{}"// data
                                        , rspArgs.toArray());// log.info("<-- {} {}", originalResponse.getStatusCode(), data);
                                log.info("响应结果" + data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            // 8.调用失败返回错误状态码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);// 降级处理返回数据
        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }

    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}