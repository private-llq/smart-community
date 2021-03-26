package com.jsy.community.config.web;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author YuLF
 * @since 2021-01-29 15:24
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty( value = "jsy.web.enable", havingValue = "true")
public class ElasticsearchConfig {

    @Value("${jsy.elasticsearch.ip}")
    private String elasticsearchIp;

    @Value("${jsy.elasticsearch.port}")
    private Integer elasticsearchPort;

    @Value("${jsy.elasticsearch.protocol}")
    private String elasticsearchProtocol;

    /**
     * 用于连接Elasticsearch的配置
     */
    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }



    @Bean
    public RestHighLevelClient restHighLevelClient(){
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost( elasticsearchIp, elasticsearchPort, elasticsearchProtocol)));
    }



}
