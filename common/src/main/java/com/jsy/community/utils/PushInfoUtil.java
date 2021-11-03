package com.jsy.community.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.jsy.community.utils.imutils.entity.*;
import com.jsy.community.utils.imutils.open.EncryptHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 推送消息工具类
 * @author: Hu
 * @create: 2021-09-10 09:22
 **/
public class PushInfoUtil {
    /**
     * @Description: 注册im聊天
     * @author: Hu
     * @since: 2021/9/2 13:45
     * @Param: [imId,nickName,password,avatarUrl]
     * @return:
     */
    public static ImResponseEntity registerUser(String imId,String password,String nickName,String avatarUrl) {
        String str = IdUtil.fastUUID();

        RegisterDto registerDto = new RegisterDto();
        registerDto.setImId(imId);
        registerDto.setNickName(nickName);
        registerDto.setPassword(MD5Util.getPassword(password));
        registerDto.setIdentifier("1");
        registerDto.setHeadImgMaxUrl(avatarUrl);
        registerDto.setHeadImgSmallUrl(avatarUrl);
//        HttpResponse response = HttpUtil.createPost("http://222.178.213.183:8090/zhsj/im/auth/login/register")
        HttpResponse response = HttpUtil.createPost("https://im.zhsj.co:8090/zhsj/im/auth/login/register")
                .header(EncryptHelper.HEAD_OPEN_ID, EncryptHelper.OPEN_ID)
                .header(EncryptHelper.HEAD_ONLY_REQ, str)
                .header(EncryptHelper.HEAD_DEVICE, "mobile")
                .body(EncryptHelper.doPost(JSON.toJSONString(registerDto), str,"mobile"))
                .execute();

        String body = response.body();
        return JSON.parseObject(body, ImResponseEntity.class);
    }

    /**
     * @Description: 推送一般消息
     * @author: Hu
     * @since: 2021/9/10 9:45
     * @Param:
     * imId：用户聊天账号,
     * title：标题,
     * desc：描述,
     * url：查看详情url,
     * content：类容
     * @return: ImResponseEntity
     */
    public static ImResponseEntity PushPublicTextMsg(String imId, String title, String desc, String url, String content, Map map, String fromImId) {
        String str = IdUtil.fastUUID();

        PushAppMsg pushAppMsg = new PushAppMsg();
        pushAppMsg.setType(1);

        TextAppMsg textAppMsg = new TextAppMsg();
        textAppMsg.setTitle(title);
        textAppMsg.setDesc(desc);
        textAppMsg.setUrl(url);
        textAppMsg.setTemplateId("暂无模板");
        textAppMsg.setContent(content);
//        textAppMsg.setLinks(Collections.singletonList(new Links(url, "查看详情")));
        textAppMsg.setExtraDta(map);
        pushAppMsg.setAppMsg(JSON.toJSONString(textAppMsg));
        SendInfo sendInfo = new SendInfo();
        sendInfo.setFromImId(fromImId);
        sendInfo.setReceiveType(1);
        sendInfo.setTo(imId);
        pushAppMsg.setSendInfo(sendInfo);
//        HttpResponse response = HttpUtil.createPost("http://222.178.213.183:8090/zhsj/im/open/public/pushAppMsg")
        HttpResponse response = HttpUtil.createPost("https://im.zhsj.co:8090/zhsj/im/open/public/pushAppMsg")
                .header(EncryptHelper.HEAD_OPEN_ID, EncryptHelper.OPEN_ID)
                .header(EncryptHelper.HEAD_ONLY_REQ, str)
                .header(EncryptHelper.HEAD_DEVICE, "mobile")
                .body(EncryptHelper.doPost(JSON.toJSONString(pushAppMsg), str, "mobile"))
                .execute();

        String body = response.body();
        return JSON.parseObject(body, ImResponseEntity.class);
    }

    /**
     * @Description: 推送一般消息
     * @author: Hu
     * @since: 2021/9/10 9:45
     * @Param:
     * imId：用户聊天账号,
     * title：标题,
     * desc：描述,
     * url：查看详情url,
     * content：类容
     * @return: ImResponseEntity
     */
    public static ImResponseEntity PushPublicMsg(String imId, String title, String desc, String url, String content, Map map, String fromImId) {
        String str = IdUtil.fastUUID();

        PushAppMsg pushAppMsg = new PushAppMsg();
        pushAppMsg.setType(1);

        TextAppMsg textAppMsg = new TextAppMsg();
        textAppMsg.setTitle(title);
        textAppMsg.setDesc(desc);
        textAppMsg.setUrl(url);
        textAppMsg.setTemplateId("暂无模板");
        textAppMsg.setContent(content);
        textAppMsg.setLinks(Collections.singletonList(new Links(url, "查看详情")));
        textAppMsg.setExtraDta(map);
        pushAppMsg.setAppMsg(JSON.toJSONString(textAppMsg));
        SendInfo sendInfo = new SendInfo();
        sendInfo.setFromImId(fromImId);
        sendInfo.setReceiveType(1);
        sendInfo.setTo(imId);
        pushAppMsg.setSendInfo(sendInfo);
//        HttpResponse response = HttpUtil.createPost("http://222.178.213.183:8090/zhsj/im/open/public/pushAppMsg")
        HttpResponse response = HttpUtil.createPost("https://im.zhsj.co:8090/zhsj/im/open/public/pushAppMsg")
                .header(EncryptHelper.HEAD_OPEN_ID, EncryptHelper.OPEN_ID)
                .header(EncryptHelper.HEAD_ONLY_REQ, str)
                .header(EncryptHelper.HEAD_DEVICE, "mobile")
                .body(EncryptHelper.doPost(JSON.toJSONString(pushAppMsg), str, "mobile"))
                .execute();

        String body = response.body();
        return JSON.parseObject(body, ImResponseEntity.class);
    }

    /**
     * @Description: 推送支付消息
     * @author: Hu
     * @since: 2021/9/10 9:46
     * @Param:
     * imId：用户聊天账号,
     * type：支付类型，1微信，2支付宝,
     * amount：支付金额,
     * detailUrl：查看详情url,
     * desc：描述
     * @return: ImResponseEntity
     */
    public static ImResponseEntity pushPayAppMsg(String imId,Integer type,String amount,String detailUrl,String desc,Map map,String fromImId){
        String str = IdUtil.fastUUID();

        PushAppMsg pushAppMsg = new PushAppMsg();
        pushAppMsg.setType(2);

        PayAppMsg payAppMsg = new PayAppMsg();
        payAppMsg.setAmount(amount);
        payAppMsg.setDesc(desc);
        payAppMsg.setCurrency("RMB");
        payAppMsg.setDetailUrl(detailUrl);
        if (type==1){
            payAppMsg.setPayType("微信支付");
        } else {
            if (type==2){
                payAppMsg.setPayType("支付宝支付");
            }
        }
        payAppMsg.setTemplateId("");
        payAppMsg.setLinks(Collections.singletonList(new Links(detailUrl, "查看账单详情")));

        payAppMsg.setLinks(Arrays.asList(
                new Links("www.baidu.com","查看百度")
                ,new Links("www.baidu.com","查看百度")
                ,new Links("www.baidu.com","查看百度")
                ,new Links("www.baidu.com","查看百度")
                ,new Links("www.baidu.com","查看百度")
                ,new Links("www.baidu.com","查看百度")
        ));
        payAppMsg.setExtraDta(map);

        pushAppMsg.setAppMsg(JSON.toJSONString(payAppMsg));
        SendInfo sendInfo = new SendInfo();
        sendInfo.setFromImId(fromImId);
        sendInfo.setReceiveType(1);
        sendInfo.setTo(imId);

        pushAppMsg.setSendInfo(sendInfo);

//        HttpResponse response = HttpUtil.createPost("http://222.178.213.183:8090/zhsj/im/open/public/pushAppMsg")
        HttpResponse response = HttpUtil.createPost("https://im.zhsj.co:8090/zhsj/im/open/public/pushAppMsg")
                .header(EncryptHelper.HEAD_OPEN_ID, EncryptHelper.OPEN_ID)
                .header(EncryptHelper.HEAD_ONLY_REQ, str)
                .header(EncryptHelper.HEAD_DEVICE, "mobile")
                .body(EncryptHelper.doPost(JSON.toJSONString(pushAppMsg), str, "mobile"))
                .execute();

        String body = response.body();
        return JSON.parseObject(body, ImResponseEntity.class);
    }
}
