package com.jsy.community.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;

import java.time.Duration;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-22 17:06
 **/
@Service
public class SessionGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            WebSession session = exchange.getSession().block();
            session.setMaxIdleTime(Duration.ofDays(7));
            return chain.filter(exchange);
        };
    }

}
