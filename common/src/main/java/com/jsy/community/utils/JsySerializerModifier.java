package com.jsy.community.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jsy.community.config.web.MyBeanSerializerModifier;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * json序列化工具类
 * @author YuLF
 * @since 2021-01-14 14:32
 */
public class JsySerializerModifier {

    /**
     * 指定全局配置 返回vo时 序列化时不忽略指定的字段 即使为Null
     * 需要注意的是：当第一次指定某个返回Vo对象忽略字段时，以后的方法只要是返回这个Vo对象类型 都会忽略掉第一次指定忽略的字段
     * 除非在另一个方法 又调用了 JsySerializerModifier.notIgnore() 会覆盖掉上一次的设置
     * 调用：
     * JsySerializerModifier.notIgnore( House.class, "houseName" );  指定返回House对象的方法 即使 houseName 为Null 也返回 不忽略这个字段
     * JsySerializerModifier.notIgnore( User.class, "gender", "age", "hobby" ); 指定返回User对象的方法 不忽略 gender、ade、hobby属性，即使为Null
     * JsySerializerModifier.notIgnore() 该方法作用域与 返回 Bean对象 和List<Bean> 对象 和 CommonResult.ok(Bean) 或 CommonResult.ok(List<Bean>) 对象
     * @param field     指定返回忽略的字段 可以有多个
     * @param type      class类型
     */
    public static void notIgnore(Class<?> type,String...field){
        if( field == null || type == null){
            return;
        }
        List<String> s = Arrays.asList(field.clone());
        MyBeanSerializerModifier beanSerializerModifier = new MyBeanSerializerModifier(type, s);
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter)SpringContextUtils.getBean("mappingJackson2HttpMessageConverter");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        setObjectMapperTime(objectMapper);
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory().withSerializerModifier(beanSerializerModifier));
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
    }

    public static void setObjectMapperTime(ObjectMapper objectMapper){
        //处理Date
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 初始化JavaTimeModule
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //处理LocalDateTime
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
