package com.jsy.community.config;

import com.jsy.community.constant.BusinessConst;
import com.jsy.community.controller.ElasticsearchImportConsumer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: rabbitmq配置文件
 * @author: Hu
 * @since: 2020/12/17 16:17
 * @Param:
 * @return:
 */
@Configuration
public class RabbitMqConfig {


    public static final String QUEUE_EMAIL = "queue_email";
    public static final String QUEUE_SMS = "queue_sms";
    public static final String QUEUE_TEST = "queue_test";
    public static final String QUEUE_WECHAT = "queue_wechat";
    public static final String QUEUE_WECHAT_DELAY = "queue_wechat_delay";

    public static final String EXCHANGE_TOPICS = "exchange_topics";
    public static final String EXCHANGE_DELAY = "exchange_delay";
    public static final String EXCHANGE_TOPICS_WECHAT = "exchange_topics_wechat";
    public static final String EXCHANGE_DELAY_WECHAT = "exchange_delay_wechat";

    //@author YuLF start
    /**
     * app 主页 全文搜索 的 队列与交换机
     * @author YuLF
     * @since  2021/2/4 10:08
     */
    @Bean
    Queue appSearchQueue() {
        return new Queue(BusinessConst.APP_SEARCH_QUEUE_NAME, true);
    }




    @Bean
    TopicExchange appSearchTopicExchange() {
        Exchange build = ExchangeBuilder.topicExchange(BusinessConst.APP_SEARCH_EXCHANGE_NAME).durable(true).build();
        return (TopicExchange) build;
    }


    @Bean
    Binding appSearchBinding(Queue appSearchQueue, TopicExchange appSearchTopicExchange) {
        return BindingBuilder.bind(appSearchQueue).to(appSearchTopicExchange).with(BusinessConst.APP_SEARCH_ROUTE_KEY);
    }

    /**
     *  监听队列(多个队列)、自动启动、自动声明功能
     * 设置事务特性、事务管理器、事务属性、事务容量(并发)、是否开启事务、回滚消息等
     * 设置消费者数量、最小最大数量、批量消费
     * 设置消息确认和自动确认模式、是否重回队列、异常捕获
     */
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory, ElasticsearchImportConsumer elasticsearchImportConsumer){
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        //监听全文搜索队列
        simpleMessageListenerContainer.setQueueNames(BusinessConst.APP_SEARCH_QUEUE_NAME);
        //手动签收
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //自定义消费者
        simpleMessageListenerContainer.setMessageListener(elasticsearchImportConsumer);
        simpleMessageListenerContainer.setDefaultRequeueRejected(false);
        return simpleMessageListenerContainer;
    }
    //@author YuLF end



    /**
     * 交换机配置
     * ExchangeBuilder提供了fanout、direct、topic、header交换机类型的配置
     *
     * @return the exchange
     */
    @Bean(EXCHANGE_TOPICS)
    public Exchange exchangeTopicInform() {
        //durable(true)持久化，消息队列重启后交换机仍然存在
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS).durable(true).build();
    }
    /**
     * @Description: 微信普通队列
     * @author: Hu
     * @since: 2021/1/23 16:46
     * @Param:
     * @return:
     */
    @Bean(EXCHANGE_TOPICS_WECHAT)
    public Exchange exchangeTopicInformWechat() {
        //durable(true)持久化，消息队列重启后交换机仍然存在
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_WECHAT).durable(true).build();
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
        Map<String, Object> args = new HashMap<>(1);
        args.put("x-delayed-type", "direct");
        return new CustomExchange(EXCHANGE_DELAY, "x-delayed-message", true, false, args);
    }

    /**
     * @Description: 微信延迟队列
     * @author: Hu
     * @since: 2021/1/23 16:46
     * @Param:
     * @return:
     */
    @Bean(EXCHANGE_DELAY_WECHAT)
    public CustomExchange delayExchangeWeChat() {
        Map<String, Object> args = new HashMap<>(1);
        args.put("x-delayed-type", "direct");
        return new CustomExchange(EXCHANGE_DELAY_WECHAT, "x-delayed-message", true, false, args);
    }




    /**
     * 声明队列
     */
    @Bean(QUEUE_TEST)
    public Queue queueInformTest() {
        return new Queue(QUEUE_TEST);
    }

    /**
     * 声明队列weChat普通队列
     */
    @Bean(QUEUE_WECHAT)
    public Queue queueWeChat() {
        return new Queue(QUEUE_WECHAT);
    }

    /**
     * 声明队列weChat延时队列
     */
    @Bean(QUEUE_WECHAT_DELAY)
    public Queue queueWeChatDelay() {
        return new Queue(QUEUE_WECHAT_DELAY);
    }


    /**
     * 声明队列
     */
    @Bean(QUEUE_SMS)
    public Queue queueInformSms() {
        return new Queue(QUEUE_SMS);
    }

    /**
     * 声明队列
     */
    @Bean(QUEUE_EMAIL)
    public Queue queueInformEmail() {
        return new Queue(QUEUE_EMAIL);
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
    public Binding bindingQueueInformSms(@Qualifier(QUEUE_SMS) Queue queue,
                                            @Qualifier(EXCHANGE_TOPICS) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("queue.sms").noargs();
    }



    @Bean
    public Binding bindingQueueInformEmail(@Qualifier(QUEUE_EMAIL) Queue queue,
                                            @Qualifier(EXCHANGE_TOPICS) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("queue.email").noargs();
    }
    /**
     * @Description: 微信普通队列
     * @author: Hu
     * @since: 2021/1/23 15:45
     * @Param:
     * @return:
     */
    @Bean
    public Binding bindingQueueInformWeChat(@Qualifier(QUEUE_WECHAT) Queue queue,
                                            @Qualifier(EXCHANGE_TOPICS_WECHAT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("queue.wechat").noargs();
    }

    /**
     * @Description: 延时队列
     * @author: Hu
     * @since: 2020/12/29 14:30
     * @Param:
     * @return:
     */
    @Bean
    public Binding bindingQueueInformTest(@Qualifier(QUEUE_TEST) Queue queue,
                                              @Qualifier(EXCHANGE_DELAY) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("queue.test").noargs();
    }
    /**
     * @Description: 微信延时队列
     * @author: Hu
     * @since: 2020/12/29 14:30
     * @Param:
     * @return:
     */
    @Bean
    public Binding bindingQueueInformWeChatDelay(@Qualifier(QUEUE_WECHAT_DELAY) Queue queue,
                                              @Qualifier(EXCHANGE_DELAY_WECHAT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("queue.wechat.delay").noargs();
    }
}
