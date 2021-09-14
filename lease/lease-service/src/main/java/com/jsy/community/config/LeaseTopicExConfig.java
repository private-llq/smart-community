package com.jsy.community.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 租赁topic交换机配置
 * @Date: 2021/9/13 10:30
 * @Version: 1.0
 **/
@Configuration
public class LeaseTopicExConfig {

    // 租赁签约交换机名称
    public final static String DELAY_EX_TOPIC_TO_LEASE_CONTRACT = "leaseContractTopicExchange";

    // 租赁签约队列名称
    public final static String DELAY_QUEUE_TO_LEASE_CONTRACT = "queue.lease.contract";

    //声明延时交换机
    @Bean(DELAY_EX_TOPIC_TO_LEASE_CONTRACT)
    CustomExchange delayExchangeExTopicToLeaseContract() {
        Map<String, Object> args = new HashMap<>(1);
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAY_EX_TOPIC_TO_LEASE_CONTRACT, "x-delayed-message", true, false, args);
    }

    //声明延时队列
    @Bean(DELAY_QUEUE_TO_LEASE_CONTRACT)
    public Queue delayQueueToLeaseContract() {
        return new Queue(DELAY_QUEUE_TO_LEASE_CONTRACT, true);
    }

    //延时队列绑定延时交换机
    @Bean
    Binding bindingQueueExchange(@Qualifier(DELAY_QUEUE_TO_LEASE_CONTRACT) Queue queue,
                                    @Qualifier(DELAY_EX_TOPIC_TO_LEASE_CONTRACT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DELAY_QUEUE_TO_LEASE_CONTRACT).noargs();
    }
}
