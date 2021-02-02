package com.jsy.community.controller.test;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.ApiOutController;
import com.jsy.community.constant.BusinessConst;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YuLF
 * @since 2021-02-02 09:50
 */
@RestController
@ApiJSYController
public class SendController {

    private final RabbitTemplate rabbitTemplate;

    public SendController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/send")
    public String send(String msg){
        rabbitTemplate.convertAndSend(BusinessConst.ES_TOPIC_EXCHANGE_NAME, "elasticsearch.full.text.search", msg);
        return "发送成功!";
    }

}
