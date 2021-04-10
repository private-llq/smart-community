package com.jsy.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @return
 * @Author lihao
 * @Description websocket配置类
 * @Date 2020/12/29 11:05
 * @Param
 **/
@Configuration
public class WebSocketConfig {
    
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
    
}