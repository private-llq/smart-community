package com.jsy.community.vo.unionpay;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 银联余额返参
 * @Date: 2021/4/28 17:19
 * @Version: 1.0
 **/
@Data
public class BalanceVO extends UnionPayBaseVO implements Serializable {

    /**
     * 钱包ID
     */
    private String walletId;

    /**
     * 可用余额
     */
    private String avlblAmt;

    /**
     * 冻结余额
     */
    private String frznAmt;

    /**
     * 授信额度
     */
    private String creditBal;
}
