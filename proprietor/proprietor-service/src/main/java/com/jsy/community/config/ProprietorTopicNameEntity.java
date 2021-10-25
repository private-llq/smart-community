package com.jsy.community.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Pipi
 * @Description: mq名称配置
 * @Date: 2021/10/22 10:04
 * @Version: 1.0
 **/
@Configuration
public class ProprietorTopicNameEntity {
    //小区相关-topic交换机名称
    public static String exTopicVisitorToCommunity;

    //绑定topic队列名称,小区监听云端队列,服务器用于推送消息
    public static String queueVisitorToCommunity;

    //监听topic队列名称,云端监听小区队列,服务器用于监听消息
    public static String queueVisitorHisFromCommunity;




    //炫优人脸识别一体机 - 交换机名称
    public static String exFaceXu;

    //炫优人脸识别一体机 - topic队列名称,服务器用于推送消息
    public static String topicFaceXuServer;

    //炫优人脸识别一体机 - topic队列名称,服务器用于监听消息
    public static String topicFaceXuClient;


    @Value("${rabbit-mq-name.exTopicVisitorToCommunity}")
    public void setExTopicVisitorToCommunity(String exTopicVisitorToCommunity) {
        ProprietorTopicNameEntity.exTopicVisitorToCommunity = exTopicVisitorToCommunity;
    }

    @Value("${rabbit-mq-name.queueVisitorToCommunity}")
    public void setQueueVisitorToCommunity(String queueVisitorToCommunity) {
        ProprietorTopicNameEntity.queueVisitorToCommunity = queueVisitorToCommunity;
    }

    @Value("${rabbit-mq-name.queueVisitorHisFromCommunity}")
    public void setQueueVisitorHisFromCommunity(String queueVisitorHisFromCommunity) {
        ProprietorTopicNameEntity.queueVisitorHisFromCommunity = queueVisitorHisFromCommunity;
    }

    @Value("${rabbit-mq-name.exFaceXu}")
    public void setExFaceXu(String exFaceXu) {
        ProprietorTopicNameEntity.exFaceXu = exFaceXu;
    }

    @Value("${rabbit-mq-name.topicFaceXuServer}")
    public void setTopicFaceXuServer(String topicFaceXuServer) {
        ProprietorTopicNameEntity.topicFaceXuServer = topicFaceXuServer;
    }

    @Value("${rabbit-mq-name.topicFaceXuClient}")
    public void setTopicFaceXuClient(String topicFaceXuClient) {
        ProprietorTopicNameEntity.topicFaceXuClient = topicFaceXuClient;
    }
}
