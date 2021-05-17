package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: B端钱包重置支付密码返参
 * @Date: 2021/5/11 17:51
 * @Version: 1.0
 **/
@Data
public class ResetBtypeAcctPwdVO extends UnionPayBaseVO implements Serializable {
    /**
     * 交易订单号
     */
    private String transOrderNo;
}
