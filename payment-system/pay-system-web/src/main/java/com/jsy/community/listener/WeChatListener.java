package com.jsy.community.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-01-23 15:38
 **/
@Component
public class WeChatListener{
    /**
     * 监听wechat队列
     * @param msg  接收的参数，类型自己定义
     * @param message
     * @param channel
     */
    @RabbitListener(queues = {"queue_wechat"})
    public void receive_queue_wechat (String msg, Message message, Channel channel)throws IOException {
        System.out.println(msg);

        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }

    /**
     * 监听wechat延时队列
     * @param msg  接收的参数，类型自己定义
     * @param message
     * @param channel
     */
    @RabbitListener(queues = {"queue_wechat_delay"})
    public void receive_wechat_delay (String msg, Message message, Channel channel)throws IOException {
        System.out.println(new Date());
        System.out.println(msg);
        System.out.println(message.getBody());
        System.out.println(channel);

        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }


}
