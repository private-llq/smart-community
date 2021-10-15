package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.qo.sys.SmsQO;
import com.jsy.community.utils.MyHttpUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 目前仅用于内部测试
 * @author YuLF
 * @since 2021-02-25 16:58
 */
@Api(tags = "短信控制器")
@Slf4j
@RestController
@ConditionalOnProperty( value = "jsy.enable-dev-sms", havingValue = "true")
@RequestMapping("/sms")
@ApiJSYController
public class SmsController {

    private static final String HOST = "http://smsbanling.market.alicloudapi.com";
    private static final String PATH = "/smsapis";
    private static final String APP_CODE = "abfc59f0cdbc4c038a2e804f9e9e37de";

    @Resource(name = "adminRedisTemplate")
    private RedisTemplate<String, Object> adminRedisTemplate;

    /**
     * 内部测试、暂无任何拦截及 短信在一定时间内 不能二次发送
     * @param qo    请求参数
     */
    @PostMapping("/send")
    public CommonResult<Boolean> send(@RequestBody SmsQO qo){

        ValidatorUtils.validateEntity(qo, SmsQO.SendSmsValid.class);
        //指定请求参数
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "APPCODE " + APP_CODE);
        Map<String, String> queryParam = new HashMap<>(3);
        queryParam.put("mobile", qo.getMobile());
        String verifyCode = getSpecifyRandomString(6);
        queryParam.put("msg", "你的验证码是 " + verifyCode + " 有效期" + qo.getExpire() + "秒!");
        queryParam.put("sign", qo.getSign());

        //存入redis
        adminRedisTemplate.opsForValue().set(qo.getRedisPrefix() + qo.getMobile() , verifyCode);
        Boolean expire = adminRedisTemplate.expire(qo.getRedisPrefix() + qo.getMobile(), qo.getExpire(), TimeUnit.SECONDS);

        if( Objects.isNull(expire)  ){
            return CommonResult.error("发送失败!");
        }
        //发送短信
        HttpGet httpGet = MyHttpUtils.httpGet(HOST + PATH, queryParam);
        MyHttpUtils.setHeader(httpGet,headers);
        String result = (String) MyHttpUtils.exec(httpGet, 1);
        //验证结果
        if ( Objects.isNull(result) ){
            return CommonResult.error("发送失败!");
        }
        Integer resultCode = JSON.parseObject(result).getInteger("result");
        if( resultCode != 0 ){
            return CommonResult.error("发送失败!");
        }
        return CommonResult.ok("发送成功!");
    }

    public static String getSpecifyRandomString(int length)
    {
        String charList = "0123456789";
        StringBuilder rev = new StringBuilder();
        Random f = new Random();
        for(int i=0;i<length;i++)
        {
            rev.append(charList.charAt(Math.abs(f.nextInt()) % charList.length()));
        }
        return rev.toString();
    }
}
