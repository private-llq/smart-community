package com.jsy.community.listener;


import com.jsy.community.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @Description: 消息监听器
 * @author: Hu
 * @since: 2020/12/17 14:55
 * @Param:
 * @return:
 */
@Component
public class SMSListener {


    /**
     * 监听SMS队列
     * @param msg  接收的参数，类型自己定义
     * @param message
     * @param channel
     */
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_SMS})
    public void receive_sms (String msg, Message message, Channel channel)throws IOException {

        System.out.println(msg);
        System.out.println(message.getBody());
        System.out.println(channel);

        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }
    /**
     * 监听EMAIL队列
     * @param msg
     * @param message
     * @param channel
     */
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_EMAIL})
    public void receive_email (Map msg, Message message, Channel channel)throws IOException {

        System.out.println(msg);
        message.getBody();
        System.out.println(message.getBody());
        System.out.println(channel);

        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }

}