package com.jsy.community.websocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Component("CommunityWebSocketConfig")
@EnableWebSocket
public class CommunityWebSocketConfig implements WebSocketConfigurer {
    //注入拦截器
    @Autowired
    private CommunityHandshakeInterceptor communityHandshakeInterceptor;
    //注入处理器
    @Autowired
    private CommunityWebSocketHandler communityWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
    	webSocketHandlerRegistry.addHandler(communityWebSocketHandler,"/wb/test").setAllowedOrigins("*")
               .addInterceptors(communityHandshakeInterceptor);
    }

}
