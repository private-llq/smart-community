package com.jsy.community.listener;

import com.jsy.community.api.IWeChatService;
import com.jsy.community.config.PublicConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.WeChatOrderEntity;
import com.rabbitmq.client.Channel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

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
        System.out.println(LocalDateTime.now());
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
    public void receive_wechat_delay (String msg, Message message, Channel channel) throws Exception {
        System.out.println(LocalDateTime.now());
        WeChatOrderEntity one = weChatService.getOrderOne(msg);
        if (one!=null){
            if (one.getOrderStatus()!=2){
                weChatService.deleteByOrder(msg);
                PublicConfig.CloseOrder(msg);
            }
        }
        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }




}
