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
public class PropertyTopicNameEntity {
    //小区相关-topic交换机名称
    public static String exTopicToCommunity;

    //绑定topic队列名称,小区监听云端队列,服务器用于推送消息
    public static String queueToCommunity;

    //监听topic队列名称,云端监听小区队列,服务器用于监听消息
    public static String queueFromCommunity;

    //云端监听小区队列
    public static String queueVisitorHisFromCommunity;




    //炫优人脸识别一体机 - 交换机名称
    public static String exFaceXu;

    //炫优人脸识别一体机 - topic队列名称,服务器用于推送消息
    public static String topicFaceXuServer;

    //炫优人脸识别一体机 - topic队列名称,服务器用于监听消息
    public static String topicFaceXuClient;




    //炫优人脸识别一体机 - 延时交换机名称
    public static String delayExFaceXu;

    //炫优人脸识别一体机 - 延时队列名称,服务器用于推送消息
    public static String delayFaceXuServer;


    @Value("${rabbit-mq-name.exTopicToCommunity}")
    public void setExTopicToCommunity(String exTopicToCommunity) {
        PropertyTopicNameEntity.exTopicToCommunity = exTopicToCommunity;
    }

    @Value("${rabbit-mq-name.queueToCommunity}")
    public void setQueueToCommunity(String queueToCommunity) {
        PropertyTopicNameEntity.queueToCommunity = queueToCommunity;
    }

    @Value("${rabbit-mq-name.queueFromCommunity}")
    public void setQueueFromCommunity(String queueFromCommunity) {
        PropertyTopicNameEntity.queueFromCommunity = queueFromCommunity;
    }

    @Value("${rabbit-mq-name.queueVisitorHisFromCommunity}")
    public void setQueueVisitorHisFromCommunity(String queueVisitorHisFromCommunity) {
        PropertyTopicNameEntity.queueVisitorHisFromCommunity = queueVisitorHisFromCommunity;
    }

    @Value("${rabbit-mq-name.exFaceXu}")
    public void setExFaceXu(String exFaceXu) {
        PropertyTopicNameEntity.exFaceXu = exFaceXu;
    }

    @Value("${rabbit-mq-name.topicFaceXuServer}")
    public void setTopicFaceXuServer(String topicFaceXuServer) {
        PropertyTopicNameEntity.topicFaceXuServer = topicFaceXuServer;
    }

    @Value("${rabbit-mq-name.topicFaceXuClient}")
    public void setTopicFaceXuClient(String topicFaceXuClient) {
        PropertyTopicNameEntity.topicFaceXuClient = topicFaceXuClient;
    }

    @Value("${rabbit-mq-name.delayExFaceXu}")
    public void setDelayExFaceXu(String delayExFaceXu) {
        PropertyTopicNameEntity.delayExFaceXu = delayExFaceXu;
    }

    @Value("${rabbit-mq-name.delayFaceXuServer}")
    public void setDelayFaceXuServer(String delayFaceXuServer) {
        PropertyTopicNameEntity.delayFaceXuServer = delayFaceXuServer;
    }
}
