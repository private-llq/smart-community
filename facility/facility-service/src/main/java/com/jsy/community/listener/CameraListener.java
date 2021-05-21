package com.jsy.community.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-05-12 13:52
 **/
@Component
public class CameraListener {


    @RabbitListener(queues = {"queue_camera_face"})
    public void QUEUE_CAR_INSERT(String msg, Message message, Channel channel)throws IOException {
        System.out.println("摄像头监听队列");
        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
