package com.jsy.community.config.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author YuLF
 * @since 2021-01-14 14:30
 */
public class MyNullSerializer extends JsonSerializer<Object> {


    /**
     * 指定 即使 该字段为Null 也把Null当做值序列化
     * @param value             为Null的字段值
     * @param jsonGenerator     成Json格式的内容的
     * @param provider          序列类提供者，可以用它获取指定的json序列类
     * @throws IOException      可能出现IO异常
     */
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeObject(value);
    }
}
