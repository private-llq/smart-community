package com.jsy.community.listener;

import org.springframework.stereotype.Component;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-29 10:49
 **/
@Component
public class TestListener {

//    /**
//     * 监听test队列
//     * @param msg  接收的参数，类型自己定义
//     * @param message
//     * @param channel
//     */
//    @RabbitListener(queues = {"queue_test"})
//    public void receive_sms (String msg, Message message, Channel channel)throws IOException {
//        System.out.println(new Date());
//        System.out.println(msg);
//        System.out.println(message.getBody());
//        System.out.println(channel);
//
//        //手动确认
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//
//    }
}
