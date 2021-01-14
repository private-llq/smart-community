package com.jsy.community.config.web;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.List;

/**
 * @author YuLF
 * @since 2021-01-14 14:29
 */
public class MyBeanSerializerModifier extends BeanSerializerModifier {


    private final List<String> field;

    public MyBeanSerializerModifier(List<String> field){
        this.field = field;
    }

    private final JsonSerializer<Object> nullArrayJsonSerializer = new MyNullSerializer();

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        //循环所有的beanPropertyWriter
        for (BeanPropertyWriter writer : beanProperties) {
            //给指定如果值为Null的字段 也返回 writer注册一个自己的nullSerializer
            if (isIgnoreType(writer, field)) {
                writer.assignNullSerializer(this.defaultNullArrayJsonSerializer());
            }
        }
        return beanProperties;
    }

    protected boolean isIgnoreType(BeanPropertyWriter writer, List<String> field) {
        return field.contains(writer.getName());

    }

    protected JsonSerializer<Object> defaultNullArrayJsonSerializer() {
        return nullArrayJsonSerializer;
    }

}
