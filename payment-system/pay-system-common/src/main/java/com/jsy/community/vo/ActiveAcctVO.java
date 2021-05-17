package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 激活账户返参
 * @Date: 2021/5/12 17:17
 * @Version: 1.0
 **/
@Data
public class ActiveAcctVO extends UnionPayBaseVO implements Serializable {

    /**
     * 钱包ID
     */
    private String walletId;

    /**
     * 交易订单号
     */
    private String transOrderNo;
}
