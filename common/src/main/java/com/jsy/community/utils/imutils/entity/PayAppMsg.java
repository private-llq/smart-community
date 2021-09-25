package com.jsy.community.utils.imutils.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author lxjr
 * @date 2021/8/19 17:34
 */
@Data
@NoArgsConstructor
public class PayAppMsg {
    /**
     * 支付金额
     * 示例：200
     */
    private String amount;
    /**
     * 描述
     */
    private String desc;
    /**
     * 币种
     */
    private String currency;
    /**
     * 支付详情url，也就是点击消息会跳转到该url
     */
    private String detailUrl;
    /**
     * 支付方式
     * 示例：微信支付（字符串）
     */
    private String payType;
    /**
     * 模板（暂为保留字段）
     */
    private String templateId;
    /**
     * 子集详情
     */
    private List<Links> links;
    /**
     * 子集详情
     */
    private Map extraDta;
}
