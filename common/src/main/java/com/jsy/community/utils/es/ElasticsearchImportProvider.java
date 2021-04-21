package com.jsy.community.utils.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.FullTextSearchEntity;
import com.jsy.community.utils.SpringContextUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.*;
import java.util.Arrays;

/**
 * @author YuLF
 * @since 2021-02-02 14:41
 */
public class ElasticsearchImportProvider {

    private static RabbitTemplate rabbitTemplate = null;

    /**
     * 工具类调用方法
     * 往MQ队列里面生产消息，主要为往ES中导入数据
     * 增删改接口业务最后增加此方法、
     * app首页全文搜索数据 导入搜索数据
     * @author YuLF
     * @Param recordFlag        数据标记：租赁、商铺、趣事、消息...，用于标识这条数据操作是请求对应接口
     * @Param searchTitle       数据标题：用于ES全文检索
     * @Param id                数据唯一ID
     * @Param operation         数据操作：告知ES中该数据是删除还是插入还是修改
     * @Param recordPicture     数据的列表icon：如果该数据存在
     * @since 2021/2/2 15:40
     */
    public static void elasticOperationSingle(@NonNull Long id, @NonNull RecordFlag recordFlag, @NonNull Operation operation, @Nullable String searchTitle, @Nullable String recordPicture, Long communityId) {
        FullTextSearchEntity fullTextSearchEntity = new FullTextSearchEntity();
        fullTextSearchEntity.setId(id);
        fullTextSearchEntity.setFlag(recordFlag);
        fullTextSearchEntity.setOperation(operation);
        fullTextSearchEntity.setTitle(searchTitle);
        fullTextSearchEntity.setPicture(recordPicture);
        fullTextSearchEntity.setCommunityId(communityId);
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(fullTextSearchEntity));
        //为json对象添加 es 搜索字段
        jsonObject.put("searchTitle", searchTitle);
        //标记这条数据的操作 增or改 类型
        jsonObject.put("operation", operation);
        //标记这条数据 是属于 哪一个类型（如 社区消息、房屋租赁、社区趣事..） 用于 后续的数据操作
        jsonObject.put("flag", recordFlag);
        //对象数据业务id 作为 es id 存入 该对象 id 的名称
        jsonObject.put("esId", id);
        //对象数据业务社区
        jsonObject.put("communityId", communityId);
        rabbitTemplate = getRabbitTemplate();
        byte[] bytes = serializationObjectToByte(fullTextSearchEntity);
        rabbitTemplate.convertAndSend(BusinessConst.APP_SEARCH_EXCHANGE_NAME, BusinessConst.APP_SEARCH_ROUTE_KEY, jsonObject.toJSONString().getBytes());
    }

    public static void elasticOperationSingle(@NonNull Long id, @NonNull RecordFlag recordFlag, @NonNull Operation operation, @Nullable String searchTitle, @Nullable String recordPicture) {
        elasticOperationSingle(id,recordFlag,operation,searchTitle,recordPicture,null);
    }


    public static void elasticOperation(@NonNull Long id, @NonNull RecordFlag recordFlag, @NonNull Operation operation, @Nullable String[] searchTitle, @Nullable String recordPicture) {
        JSONObject jsonObject = new JSONObject();
        //为json对象添加 es 搜索字段
        jsonObject.put("searchTitle", Arrays.toString(searchTitle));
        //标记这条数据的操作 增or改 类型
        jsonObject.put("operation", operation);
        //标记这条数据 是属于 哪一个类型（如 社区消息、房屋租赁、社区趣事..） 用于 后续的数据操作
        jsonObject.put("flag", recordFlag);
        //对象数据业务id 作为 es id 存入 该对象 id 的名称
        jsonObject.put("esId", id);
        rabbitTemplate = getRabbitTemplate();
        rabbitTemplate.convertAndSend(BusinessConst.APP_SEARCH_EXCHANGE_NAME, BusinessConst.APP_SEARCH_ROUTE_KEY, jsonObject.toJSONString().getBytes());
    }


    /**
     * 注解调用方法
     * 按JSONObject发送
     * @param jsonObject        jsonObject 对象
     */
    public static void elasticOperation(JSONObject jsonObject) {
        //以字节数组 传输
        rabbitTemplate = getRabbitTemplate();
        rabbitTemplate.convertAndSend(BusinessConst.APP_SEARCH_EXCHANGE_NAME, BusinessConst.APP_SEARCH_ROUTE_KEY, jsonObject.toJSONString().getBytes());
    }


    /**
     * 对象序列化为字节数组
     * @author YuLF
     * @since  2021/2/20 10:05
     */
    public static byte[] serializationObjectToByte(Object obj) {
        try (ByteArrayOutputStream bo = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bo)) {
            oos.writeObject(obj);
            return bo.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static RabbitTemplate getRabbitTemplate() {
        if (rabbitTemplate == null) {
            synchronized (ElasticsearchImportProvider.class) {
                if (rabbitTemplate == null) {
                    rabbitTemplate = (RabbitTemplate) SpringContextUtils.getBean("rabbitTemplate");
                }
            }
        }
        return rabbitTemplate;
    }


}
