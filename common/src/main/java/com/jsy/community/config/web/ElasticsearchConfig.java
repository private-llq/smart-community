package com.jsy.community.config.web;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author YuLF
 * @since 2021-01-29 15:24
 */
@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig {


    /**
     * 用于连接Elasticsearch的配置
     */
    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }



    @Bean
    public RestHighLevelClient elasticsearchClient(){
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("106.13.84.80", 9201, "http")));
    }



}
