package com.jsy.community.listener;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.AssetLeaseRecordService;
import com.jsy.community.config.LeaseTopicExConfig;
import com.jsy.community.constant.Const;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @Author: Pipi
 * @Description: 租赁签约交换机监听队列
 * @Date: 2021/9/13 13:56
 * @Version: 1.0
 **/
@Slf4j
@Component
public class LeaseTopicListener {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private AssetLeaseRecordService assetLeaseRecordService;

    /**
     * @author: Pipi
     * @description: 租赁签约倒计时监听
     * @param msg:
     * @param message:
     * @param channel:
     * @return: void
     * @date: 2021/9/13 14:27
     **/
    @RabbitListener(queues = LeaseTopicExConfig.DELAY_QUEUE_TO_LEASE_CONTRACT)
    public void contractListener(String msg, Message message, Channel channel) throws IOException {
        log.info("签约倒计时结束:{}", msg);
        try {
            JSONObject jsonObject = JSONObject.parseObject(msg);
            jsonObject.getInteger("operation");
            LocalDateTime operationTime = jsonObject.getObject("operationTime", LocalDateTime.class);
            assetLeaseRecordService.countdownOpration(jsonObject.getLong("id"), jsonObject.getInteger("operation"), operationTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
