package com.jsy.community.listener;

import com.jsy.community.constant.BusinessConst;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author YuLF
 * @since 2021-02-02 13:44
 */
@Component
public class ElasticSearchListener {

    @RabbitListener(queues = {BusinessConst.ES_QUEUE_NAME})
    public void receiverMessage (String msg, Message message, Channel channel) throws IOException {
        System.out.println(msg);
        String consumerTag = message.getMessageProperties().getConsumerTag();
        System.out.println("tag标签："+message.getMessageProperties().getDeliveryTag());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }



}
