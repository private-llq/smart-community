package com.jsy.community.controller;

import com.jsy.community.vo.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-05-31 16:02
 **/
@RestController
public class IOSLoginController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("getKey")
    public CommonResult getKeys(){
//        PublicKey key = IOSUtil.getPublicKey();
//        System.out.println("PublicKey:"+key);
//        return CommonResult.ok(key.toString());
        redisTemplate.opsForValue().set("Login:wodiaonimade","{\n" +
                "  \"avatarUrl\": \"http://222.178.212.29:9000/avatar/8c7fa5cc0b2d47aa9a8de1e7c2e977ec\",\n" +
                "  \"city\": \"重庆市\",\n" +
                "  \"detailAddress\": \"重庆市九龙坡区西彭镇泥壁村15组31号\",\n" +
                "  \"imId\": \"c889034ef4d3424aa8ac9bf7cea909c1\",\n" +
                "  \"isBindMobile\": 1,\n" +
                "  \"isRealAuth\": 2,\n" +
                "  \"nickname\": \"AnswerLiu\",\n" +
                "  \"province\": \"重庆市\",\n" +
                "  \"realName\": \"刘浩\",\n" +
                "  \"sex\": 1,\n" +
                "  \"uid\": \"6d6d2a3e42b14afa88de5e2faf6acfae\",\n" +
                "  \"uroraTags\": \"all\"\n" +
                "}");

        return CommonResult.ok();
    }
}
