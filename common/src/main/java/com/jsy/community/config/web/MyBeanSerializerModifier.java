package com.jsy.community.config.web;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.List;

/**
 * 自定义Json序列化修改器
 * @author YuLF
 * @since 2021-01-14 14:29
 */
public class MyBeanSerializerModifier extends BeanSerializerModifier {


    private final List<String> field;

    private final Class<?> clazz;

    public MyBeanSerializerModifier(Class<?> clazz,List<String> field){
        this.clazz = clazz;
        this.field = field;
    }

    private final JsonSerializer<Object> nullSerializer = new MyNullSerializer();

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        //循环所有的beanPropertyWriter
        for (BeanPropertyWriter writer : beanProperties) {
            //给指定如果值为Null的字段 也返回 writer注册一个自己的nullSerializer
            String name = beanDesc.getBeanClass().getSimpleName();
            //如果class类型和指定类型一样 并且字段属性也是和不忽略的字段一样
            if ( isIdentical(clazz , name) && isIgnoreType(writer, field)) {
                writer.assignNullSerializer(this.defaultNullJsonSerializer());
            }
        }
        return beanProperties;
    }

    protected boolean isIdentical(Class<?> obj, String fieldName ){
        String oName = obj.getSimpleName();
        return oName.equals(fieldName);
    }

    protected boolean isIgnoreType(BeanPropertyWriter writer, List<String> field) {
        return field.contains(writer.getName());

    }

    protected JsonSerializer<Object> defaultNullJsonSerializer() {
        return nullSerializer;
    }

}
