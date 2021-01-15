package com.jsy.community.config.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsy.community.utils.JsySerializerModifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;


/**
 * @author YuLF
 * @since 2020-12-26 09:37
 */
@Configuration
public class CommunityWebConfig {

    /**
     * Include.Include.ALWAYS 默认
     * Include.NON_DEFAULT 属性为默认值不序列化
     * Include.NON_EMPTY 属性为 空（“”） 或者为 NULL 都不序列化
     * Include.NON_NULL 属性为NULL 不序列化
     */
    @Bean(name = "mappingJackson2HttpMessageConverter")
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        JsySerializerModifier.setObjectMapperTime(objectMapper);
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
        return mappingJackson2HttpMessageConverter;
    }
}
