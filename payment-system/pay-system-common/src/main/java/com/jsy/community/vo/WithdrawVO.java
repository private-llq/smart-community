package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 提现申请返参
 * @Date: 2021/5/10 10:34
 * @Version: 1.0
 **/
@Data
public class WithdrawVO extends UnionPayBaseVO implements Serializable {

    /**
     * 交易订单号
     */
    private String transOrderNo;

    /**
     * 银行账号
     */
    private String bankAcctNo;
}
