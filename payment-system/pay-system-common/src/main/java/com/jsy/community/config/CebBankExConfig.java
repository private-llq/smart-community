package com.jsy.community.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/12/6 18:53
 * @Version: 1.0
 **/
@Configuration
public class CebBankExConfig {

    /**
     * 光大云缴费延时交换机名称
     */
    public final static String CEB_BANK_DELAYED_EXCHANGE = "cebBankDelayedExchange";

    /**
     * 光大云缴费延时队列队列
     */
    public final static String CEB_BANK_DELAYED_QUEUE = "cebBankDelayedQueue";

    /**
     * @author: Pipi
     * @description: 声明延迟交换机
     * @param :
     * @return: {@link CustomExchange}
     * @date: 2021/12/6 19:02
     **/
    @Bean(CEB_BANK_DELAYED_EXCHANGE)
    CustomExchange delayExchangeToCebBankBill() {
        HashMap<String, Object> args = new HashMap<>(1);
        args.put("x-delayed-type", "direct");
        return new CustomExchange(CEB_BANK_DELAYED_EXCHANGE, "x-delayed-message", true, false, args);
    }

    /**
     * @author: Pipi
     * @description: 声明延迟队列
     * @param :
     * @return: {@link Queue}
     * @date: 2021/12/6 19:05
     **/
    @Bean(CEB_BANK_DELAYED_QUEUE)
    public Queue delayQueueToCebBank() {
        return new Queue(CEB_BANK_DELAYED_QUEUE, true);
    }

    Binding bindingQueueExchange(@Qualifier(CEB_BANK_DELAYED_QUEUE) Queue queue,
                                 @Qualifier(CEB_BANK_DELAYED_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CEB_BANK_DELAYED_QUEUE).noargs();
    }
}
