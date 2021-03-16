package com.jsy.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.qo.proprietor.PushInformQO;
import com.jsy.community.utils.JsonUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author YuLF
 * @since 2021-03-04 10:40
 */
@RestController
public class TestController {

    @GetMapping("test")
    public JSONObject test(){
        PushInformQO qo = new PushInformQO();
        qo.setId(123123L);
        qo.setPushTarget(312);
        qo.setUid(UUID.randomUUID().toString());
        qo.setAcctName("推送号");
        qo.setAcctId(51231321312L);
        qo.setAcctAvatar("https://www.baidu.com/1.jpg");
        qo.setPushMsg("推送消息");
        return JsonUtils.toJsonObject(qo, "acctName", "pushMsg","acctId","pushTarget");
    }


}
