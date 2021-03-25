package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsy.community.config.web.ElasticsearchConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.utils.es.Operation;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author YuLF
 * @since 2021-02-20 09:30
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchImportConsumer implements ChannelAwareMessageListener {

    private final RestHighLevelClient customElasticsearchClient;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        //传过来的 字节数组 为json字符串 字节数组
        String jsonStr = new String(message.getBody(), StandardCharsets.UTF_8);
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String operation =  jsonObject.getString("operation");
        Operation operation1 = Operation.valueOf(operation);
        log.info("自定义监听【ES全文搜索数据】MqTag标签："+ message.getMessageProperties().getDeliveryTag() + ":" + jsonObject);
        boolean isConsumerSuccess = false;
        //向es导入数据
        switch (operation1){
            case INSERT:
                insertData(jsonObject);
                break;
            case UPDATE:
                updateData(jsonObject);
                break;
            case DELETE:
                deleteData(jsonObject);
                break;
            default:
                break;
        }
        //如果操作 elasticsearch 成功 回复 mq
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }


    /**
     * 字节数组序列化为对象
     * @author YuLF
     * @since  2021/2/20 10:05
     */
    public static <T> T serializationByteToObject(byte[] bytes, Class<T> type) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream)) {
            Object object = ois.readObject();
            if (object.getClass().equals(type)) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.convertValue(object, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void deleteData(JSONObject jsonObject) throws IOException {
        //第一个参数为操作索引名称、第二个参数为删除文档的id
        DeleteRequest deleteRequest = new DeleteRequest(
                BusinessConst.FULL_TEXT_SEARCH_INDEX, jsonObject.getString("esId"));
        DeleteResponse response = customElasticsearchClient.delete(deleteRequest, ElasticsearchConfig.COMMON_OPTIONS);
        DocWriteResponse.Result result = response.getResult();
        //以下两种状态都表示在es中不存在了
    }

    private void updateData(JSONObject jsonObject) throws IOException {
        //构造函数传入索引名、其他两个构造函数传入type和id的已停止使用，
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(BusinessConst.FULL_TEXT_SEARCH_INDEX);
        //docAsUpsert表示 如果文档不存在则创建
        updateRequest.docAsUpsert(true);
        updateRequest.id(jsonObject.getString("esId"));
        //转换为json串发给elasticsearch
        updateRequest.doc(jsonObject.toJSONString(), XContentType.JSON);
        UpdateResponse update = customElasticsearchClient.update(updateRequest, ElasticsearchConfig.COMMON_OPTIONS);
        DocWriteResponse.Result result = update.getResult();
    }

    private void insertData(JSONObject jsonObject) throws IOException {
        //构造函数传入索引名、其他两个构造函数传入type和id的已停止使用，
        IndexRequest indexRequest = new IndexRequest(BusinessConst.FULL_TEXT_SEARCH_INDEX);
        indexRequest.id(jsonObject.getString("esId"));
        //转换为json串发给elasticsearch
        indexRequest.source(jsonObject.toJSONString(), XContentType.JSON);
        //发送保存 第一个参数是构造的请求，第二个参数是请求头需要的一些操作如验证token
        IndexResponse indexResponse = customElasticsearchClient.index(indexRequest, ElasticsearchConfig.COMMON_OPTIONS);
        DocWriteResponse.Result result = indexResponse.getResult();
    }
}
