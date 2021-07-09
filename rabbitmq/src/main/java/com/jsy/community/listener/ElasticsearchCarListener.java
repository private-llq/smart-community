package com.jsy.community.listener;

import com.jsy.community.api.IPropertyCarService;
import com.jsy.community.config.RabbitMqConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.property.ElasticsearchCarQO;
import com.jsy.community.utils.es.ElasticsearchCarUtil;
import com.rabbitmq.client.Channel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 车辆队列监听器
 * @author: Hu
 * @create: 2021-03-26 14:21
 **/
@Component
public class ElasticsearchCarListener {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyCarService propertyCarService;

    /**
     * @Description: 新增监听队列
     * @author: Hu
     * @since: 2021/3/26 14:26
     * @Param:
     * @return:
     */
    @RabbitListener(queues = {RabbitMqConfig.QUEUE_CAR_INSERT})
    public void QUEUE_CAR_INSERT(List<ElasticsearchCarQO> cars, Message message, Channel channel)throws IOException {
        ElasticsearchCarUtil.insertData(cars,restHighLevelClient);
        propertyCarService.insertList(cars);
        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        System.out.println("新增车辆信息成功");
    }
    /**
     * @Description: 修改监听队列
     * @author: Hu
     * @since: 2021/3/26 14:26
     * @Param:
     * @return:
     */
    @RabbitListener(queues = {RabbitMqConfig.QUEUE_CAR_UPDATE})
    public void QUEUE_CAR_UPDATE (List<ElasticsearchCarQO> cars, Message message, Channel channel)throws IOException {
        ElasticsearchCarUtil.updateData(cars,restHighLevelClient);
        propertyCarService.updateList(cars);
        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        System.out.println("修改车辆信息成功");
    }
    /**
     * @Description: 删除监听队列
     * @author: Hu
     * @since: 2021/3/26 14:26
     * @Param:
     * @return:
     */
    @RabbitListener(queues = {RabbitMqConfig.QUEUE_CAR_DELETE})
    public void QUEUE_CAR_DELETE (String id, Message message, Channel channel)throws IOException {
        ElasticsearchCarUtil.deleteData(id,restHighLevelClient);
        propertyCarService.deleteById(id);
        //手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        System.out.println("删除车辆信息成功");
    }
}
