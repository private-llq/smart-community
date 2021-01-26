package com.jsy.community.listener;

import com.jsy.community.api.IWeChatService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.WeChatOrderEntity;
import com.rabbitmq.client.Channel;
import org.apache.dubbo.config.annotation.DubboReference;
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
    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private IWeChatService weChatService;
    /**
     * 监听wechat队列
     * @param msg  接收的参数，类型自己定义
     * @param message
     * @param channel
     */
    @RabbitListener(queues = {"queue_wechat"})
    public void receive_queue_wechat (WeChatOrderEntity msg, Message message, Channel channel)throws IOException {
        System.out.println(weChatService);
        System.out.println(msg);
        weChatService.insertOrder(msg);
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
