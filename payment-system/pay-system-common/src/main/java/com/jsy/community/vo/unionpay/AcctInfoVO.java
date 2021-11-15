package com.jsy.community.vo.unionpay;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 银联钱包账户信息返参
 * @Date: 2021/4/14 11:02
 * @Version: 1.0
 **/
@Data
public class AcctInfoVO extends UnionPayBaseVO implements Serializable {


    /**
     * "钱包ID
     */
    private String walletId;

    /**
     * "钱包名称
     */
    private String walletName;

    /**
     * "账户状态码
     */
    private String acctStatus;

    /**
     * "账户等级
     */
    private String acctLevel;

    /**
     * "账户状态描述
     */
    private String acctStatusDscrb;

    /**
     * "账户属性
     */
    private String acctAttribute;

    /**
     * "可充值状态,0:不可,1:可以
     */
    private String rechargeCode;

    /**
     * "可消费状态,0:不可,1:可以
     */
    private String consumeCode;

    /**
     * "可转账状态,0:不可,1:可以
     */
    private String transferCode;

    /**
     * "可提现状态,0:不可,1:可以
     */
    private String withdrawCode;

    /**
     * "可汇款状态,0:不可,1:可以
     */
    private String remittanceCode;

    /**
     * "可冻结状态,0:不可,1:可以
     */
    private String freezeCode;

    /**
     * "可解冻状态,0:不可,1:可以
     */
    private String thawCode;

    /**
     * "用户姓名
     */
    private String userName;

    /**
     * "手机号
     */
    private String mobileNo;

    /**
     * "用户中心userUuid
     */
    private String userUuid;

    /**
     * "客商ID
     */
    private String custId;

    /**
     * "客商名称
     */
    private String custName;
}
