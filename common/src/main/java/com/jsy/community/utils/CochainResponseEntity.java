package com.jsy.community.utils;

import lombok.Data;

/**
 * @program: com.jsy.community
 * @description: 支付上链响应类
 * @author: Hu
 * @create: 2021-10-28 16:42
 **/
@Data
public class CochainResponseEntity {
    /**
     * 响应code
     */
    private Integer code;
    /**
     *
     */
    private Boolean fail;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 签名
     */
    private String sign;
    /**
     * 响应
     */
    private Boolean success;
    /**
     * 时间搓
     */
    private String time;
    /**
     * 结果
     */
    private String data;
}
