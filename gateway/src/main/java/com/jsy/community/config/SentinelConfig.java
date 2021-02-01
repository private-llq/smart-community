package com.jsy.community.config;
//
//import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
//import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
///**
// * @program: com.jsy.community
// * @description: 限流返回的数据
// * @author: Hu
// * @create: 2020-12-15 14:22
// **/
//@Configuration
//public class SentinelConfig {
//    public SentinelConfig(){
//        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
//            @Override
//            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
//                return ServerResponse.ok().body(Mono.just("限流啦,请求太频繁"),String.class);
//            }
//        });
//    }
//}