package com.jsy.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: babbitmq配置文件
 * @author: Hu
 * @since: 2020/12/17 16:17
 * @Param:
 * @return:
 */
@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_EMAIL = "queue_email";
    public static final String QUEUE_SMS = "queue_sms";
    public static final String QUEUE_TEST = "queue_test";
    public static final String EXCHANGE_TOPICS = "exchange_topics";
    public static final String EXCHANGE_DELAY = "exchange_delay";


    /**
     * 交换机配置
     * ExchangeBuilder提供了fanout、direct、topic、header交换机类型的配置
     *
     * @return the exchange
     */
    @Bean(EXCHANGE_TOPICS)
    public Exchange EXCHANGE_TOPICS_INFORM() {
        //durable(true)持久化，消息队列重启后交换机仍然存在
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS).durable(true).build();
    }

    /**
     * @Description: 延时队列
     * @author: Hu
     * @since: 2020/12/29 14:30
     * @Param:
     * @return:
     */
    @Bean(EXCHANGE_DELAY)
    public CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(EXCHANGE_DELAY, "x-delayed-message", true, false, args);
    }



    //声明队列
    @Bean(QUEUE_TEST)
    public Queue QUEUE_INFORM_TEST() {
        Queue queue = new Queue(QUEUE_TEST);
        return queue;
    }


    //声明队列
    @Bean(QUEUE_SMS)
    public Queue QUEUE_INFORM_SMS() {
        Queue queue = new Queue(QUEUE_SMS);
        return queue;
    }

    //声明队列
    @Bean(QUEUE_EMAIL)
    public Queue QUEUE_INFORM_EMAIL() {
        Queue queue = new Queue(QUEUE_EMAIL);
        return queue;
    }

    /**
     * channel.queueBind(INFORM_QUEUE_SMS,"inform_exchange_topic","inform.#.sms.#");
     * 绑定队列到交换机 .
     *
     * 在配置@Bean的时候，想获取spring容器中的bean来使用
     *  通过参数注入
     *  @Qualifier 指定bean的名称
     *
     * @param queue    the queue
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier(QUEUE_SMS) Queue queue,
                                            @Qualifier(EXCHANGE_TOPICS) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("queue.sms").noargs();
    }



    @Bean
    public Binding BINDING_QUEUE_INFORM_EMAIL(@Qualifier(QUEUE_EMAIL) Queue queue,
                                            @Qualifier(EXCHANGE_TOPICS) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("queue.email").noargs();
    }

    /**
     * @Description: 延时队列
     * @author: Hu
     * @since: 2020/12/29 14:30
     * @Param:
     * @return:
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_TEST(@Qualifier(QUEUE_TEST) Queue queue,
                                              @Qualifier(EXCHANGE_DELAY) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("queue.test").noargs();
    }







}
