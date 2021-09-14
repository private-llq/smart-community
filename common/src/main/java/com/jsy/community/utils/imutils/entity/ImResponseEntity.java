package com.jsy.community.utils.imutils.entity;

import lombok.Data;

/**
 * @program: untitled
 * @description:  im聊天响应实体类
 * @author: Hu
 * @create: 2021-09-02 14:17
 **/
@Data
public class ImResponseEntity {
    /**
     * 响应code
     */
    private Integer err_code;
    /**
     * 响应消息
     */
    private String err_msg;
    /**
     * 时间戳
     */
    private String time;
    /**
     * 签名
     */
    private String sign;
    /**
     * 结果
     */
    private String data;

}
