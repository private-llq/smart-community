package com.jsy.community.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.AfterFilter;
import com.alibaba.fastjson.serializer.BeforeFilter;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.alibaba.fastjson.serializer.ValueFilter;

/**
 * @author Administrator
 */
public class JsonUtils {

    /**
     * 从对象中序列化指定字段的json串
     * @param object        参数对象
     * @param keys 需要序列化的key值 目的是排除不需要对象展示的字段
     */
    public static JSONObject toJsonObject(Object object, String... keys) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(object.getClass(),keys);
        return convert(object,filter, SerializerFeature.WriteMapNullValue);
    }

    public static  JSONObject convert(Object object, SerializeFilter filter, SerializerFeature... features) {
        try (SerializeWriter out = new SerializeWriter()) {
            JSONSerializer serializer = new JSONSerializer(out);
            for (SerializerFeature feature : features) {
                serializer.config(feature, true);
            }
            serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
            setFilter(serializer, filter);
            serializer.write(object);
            return JSON.parseObject(out.toString());
        }
    }

    private static void setFilter(JSONSerializer serializer, SerializeFilter filter) {
        if (filter == null) {
            return;
        }
        if (filter instanceof PropertyPreFilter) {
            serializer.getPropertyPreFilters().add((PropertyPreFilter) filter);
        }
        if (filter instanceof NameFilter) {
            serializer.getNameFilters().add((NameFilter) filter);
        }
        if (filter instanceof ValueFilter) {
            serializer.getValueFilters().add((ValueFilter) filter);
        }
        if (filter instanceof PropertyFilter) {
            serializer.getPropertyFilters().add((PropertyFilter) filter);
        }
        if (filter instanceof BeforeFilter) {
            serializer.getBeforeFilters().add((BeforeFilter) filter);
        }
        if (filter instanceof AfterFilter) {
            serializer.getAfterFilters().add((AfterFilter) filter);
        }
    }

}
