package com.jsy.community.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsy.community.config.web.MyBeanSerializerModifier;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;
import java.util.List;

/**
 * json序列化工具类
 * @author YuLF
 * @since 2021-01-14 14:32
 */
public class JsySerializerModifier {

    /**
     * 指定全局配置 返回vo时 不忽略指定的字段
     * @param field     指定的字段
     */
    public static void notIgnore(String...field){
        if( field == null ){
            return;
        }
        List<String> s = Arrays.asList(field.clone());
        MyBeanSerializerModifier beanSerializerModifier = new MyBeanSerializerModifier(s);
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter)SpringContextUtils.getBean("mappingJackson2HttpMessageConverter");
        ObjectMapper objectMapper = mappingJackson2HttpMessageConverter.getObjectMapper();
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory().withSerializerModifier(beanSerializerModifier));
    }
}
