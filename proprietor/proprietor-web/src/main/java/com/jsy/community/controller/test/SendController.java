package com.jsy.community.controller.test;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.vo.HouseVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author YuLF
 * @since 2021-02-02 09:50
 */
@RestController
@ApiJSYController
public class SendController {

    private final RabbitTemplate rabbitTemplate;

    @Resource()
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public SendController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    @GetMapping("/send")
    public String send(String msg){
        rabbitTemplate.convertAndSend(BusinessConst.ES_TOPIC_EXCHANGE_NAME, "elasticsearch.full.text.search", msg);
        return "发送成功!";
    }

    @GetMapping("/setValue")
    public String setValue(String value){
        stringRedisTemplate.opsForValue().set("OBJ:"+value, value);
        return "success";
    }

    @GetMapping("/setHashValue")
    public String setHashValue(){
        for(int i = 0; i < 15; i++){
            HouseVo vo = new HouseVo();
            vo.setBuilding("楼栋"+i);
            vo.setCheckStatus(i+"");
            vo.setCommunityName("社区"+i);
            redisTemplate.opsForHash().put("TestObj:",""+i, JSONObject.toJSONString(vo));
        }
        return "success";
    }

    @GetMapping("/getValue")
    public String getValue(String value){
        return String.valueOf(stringRedisTemplate.opsForValue().get("Test:"+value));
    }

    @Resource
    ApplicationContext applicationContext;

    @GetMapping("/getBeanList")
    public List<String> getBeanList(){
        return Arrays.asList(applicationContext.getBeanDefinitionNames());
    }

}
