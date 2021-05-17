package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 银联支付返参
 * @Date: 2021/4/26 16:48
 * @Version: 1.0
 **/
@Data
public class UnionPayOrderVO extends UnionPayBaseVO implements Serializable {

    /**
     * 商户订单号
     */
    private String mctOrderNo;

    /**
     * 消费类支付（H5）url
     */
    private String payH5Url;

    /**
     * 优惠描述
     */
    private String disctDesc;

    /**
     * 优惠金额
     */
    private String disctAmt;

    /**
     * 实付金额
     */
    private String actPayAmt;
}
