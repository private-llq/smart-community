package com.jsy.community.listener;

import com.jsy.community.api.ISmsPurchaseRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.SmsPurchaseRecordEntity;
import com.rabbitmq.client.Channel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @program: com.jsy.community
 * @description:  订单监听队列类
 * @author: Hu
 * @create: 2021-01-23 15:38
 **/
@Component
public class Listener {
    
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ISmsPurchaseRecordService smsPurchaseRecordService;

    /**
     * 监听queue_sms_purchase延时队列
     * @param msg  接收的参数，类型自己定义
     * @param message
     * @param channel
     */
    @RabbitListener(queues = {"queue_sms_purchase"})
    public void receive_queue_sms_purchase (String msg, Message message, Channel channel) throws Exception {
        try {
            SmsPurchaseRecordEntity smsPurchaseRecordEntity = smsPurchaseRecordService.querySmsPurchaseByOrderNum(msg);
            if (smsPurchaseRecordEntity != null) {
                if (smsPurchaseRecordEntity.getStatus() == 0) {
                    smsPurchaseRecordEntity.setStatus(2);
                    smsPurchaseRecordService.updateSmsPurchase(smsPurchaseRecordEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }
        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

}
