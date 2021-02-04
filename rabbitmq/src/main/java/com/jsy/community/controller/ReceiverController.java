package com.jsy.community.controller;
import com.alibaba.fastjson.JSON;
import com.jsy.community.config.web.ElasticsearchConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.FullTextSearchEntity;
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
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author YuLF
 * @since 2021-02-02 09:48
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiverController {



    private final RestHighLevelClient elasticsearchClient;

    @RabbitListener(queues = {BusinessConst.APP_SEARCH_QUEUE_NAME})
    public void receiverMessage (FullTextSearchEntity entity, Message message, Channel channel) throws IOException {
        System.out.println("【ES全文搜索数据】MqTag标签："+message.getMessageProperties().getDeliveryTag() + ":" + entity);
        boolean isConsumerSuccess = false;
        //向es导入数据
        switch (entity.getOperation()){
            case INSERT:
                isConsumerSuccess = insertData(entity);
                break;
            case UPDATE:
                isConsumerSuccess = updateData(entity);
                break;
            case DELETE:
                isConsumerSuccess = deleteData(entity);
                break;
            default:
                break;
        }
        //如果操作 elasticsearch 成功 回复 mq
        if(isConsumerSuccess){
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }
        //进入死信队列
    }

    private boolean deleteData(FullTextSearchEntity entity) throws IOException {
        //第一个参数为操作索引名称、第二个参数为删除文档的id
        DeleteRequest deleteRequest = new DeleteRequest(
                BusinessConst.FULL_TEXT_SEARCH_INDEX, entity.getId().toString());
        DeleteResponse response = elasticsearchClient.delete(deleteRequest, ElasticsearchConfig.COMMON_OPTIONS);
        DocWriteResponse.Result result = response.getResult();
        //以下两种状态都表示在es中不存在了
        return result == DocWriteResponse.Result.DELETED || result == DocWriteResponse.Result.NOT_FOUND;
    }

    private boolean updateData(FullTextSearchEntity entity) throws IOException {
        //构造函数传入索引名、其他两个构造函数传入type和id的已停止使用，
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(BusinessConst.FULL_TEXT_SEARCH_INDEX);
        //docAsUpsert表示 如果文档不存在则创建
        updateRequest.docAsUpsert(true);
        updateRequest.id(entity.getId() + "");
        //转换为json串发给elasticsearch
        updateRequest.doc(JSON.toJSONString(entity), XContentType.JSON);
        UpdateResponse update = elasticsearchClient.update(updateRequest, ElasticsearchConfig.COMMON_OPTIONS);
        DocWriteResponse.Result result = update.getResult();
        return result == DocWriteResponse.Result.UPDATED || result == DocWriteResponse.Result.CREATED;
    }

    private boolean insertData(FullTextSearchEntity entity) throws IOException {
        //构造函数传入索引名、其他两个构造函数传入type和id的已停止使用，
        IndexRequest indexRequest = new IndexRequest(BusinessConst.FULL_TEXT_SEARCH_INDEX);
        indexRequest.id(String.valueOf(entity.getId()));
        //转换为json串发给elasticsearch
        indexRequest.source(JSON.toJSONString(entity), XContentType.JSON);
        //发送保存 第一个参数是构造的请求，第二个参数是请求头需要的一些操作如验证token
        IndexResponse indexResponse = elasticsearchClient.index(indexRequest, ElasticsearchConfig.COMMON_OPTIONS);
        DocWriteResponse.Result result = indexResponse.getResult();
        return result == DocWriteResponse.Result.CREATED || result == DocWriteResponse.Result.UPDATED;
    }






}
