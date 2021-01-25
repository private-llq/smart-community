package com.jsy.community.utils;

import lombok.Data;

/**
 * @Description: im组接口传参对象 (from im组 @author liujinrong)
 * @Author: chq459799974
 * @Date: 2021/1/25
 **/
@Data
public class OpenParam {
    /**
     * err_code:
     * 0标识成功
     * 10 无效的open_id
     * 20 签名错误
     * 30 时间戳错误
     * 40 访问ip不在白名单中
     * 50 未知错误
     * 60 秘钥错误
     */
    private Integer err_code = 0;
    /**
     * ok成功提示信息 err错误提示
     */
    private String err_msg = "ok";
    /**
     * 聊天开放平台注册id
     */
    private String open_id = "";
    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 加密之后的数据
     */
    private String data = "";
    /**
     * 签名
     */
    private String signature = "";
}
