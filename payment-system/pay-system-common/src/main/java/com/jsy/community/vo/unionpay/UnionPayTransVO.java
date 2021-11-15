package com.jsy.community.vo.unionpay;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 交易明细返参
 * @Date: 2021/5/12 10:04
 * @Version: 1.0
 **/
@Data
public class UnionPayTransVO implements Serializable {

    /**
     * 交易订单号
     */
    private String transOrderNo;

    /**
     * 钱包ID
     */
    private String walletId;

    /**
     * 钱包名称
     */
    private String walletName;

    /**
     * 对方钱包ID
     */
    private String otherWalletId;

    /**
     * 对方钱包名称
     */
    private String otherWalletName;

    /**
     * 共管子账号
     */
    private String coadminAcctNo;

    /**
     * 共管子账号名称
     */
    private String coadminAcctName;

    /**
     * 交易日期
     */
    private String transDate;

    /**
     * 交易时间
     */
    private String transTime;

    /**
     * 交易类型编码
     */
    private String transType;

    /**
     * 交易类型名称
     */
    private String transTypeName;

    /**
     * 商户号
     */
    private String merNo;

    /**
     * 商户简称
     */
    private String merName;

    /**
     * 交易金额
     */
    private String transAmt;

    /**
     * 共管金额
     */
    private String coadminAmt;

    /**
     * 可用金额
     */
    private String avlblAmt;

    /**
     * 清算金额
     */
    private String settAmt;

    /**
     * 清算日期
     */
    private String settDate;

    /**
     * 处理状态
     */
    private String procStatus;

    /**
     * 状态描述
     */
    private String procStatusDscrb;

    /**
     * 处理结果描述
     */
    private String procResultDscrb;

    /**
     * 调整标记
     */
    private String adjustFlag;

    /**
     * 商户订单号
     */
    private String mctOrderNo;

    /**
     * 银行行号
     */
    private String bankCode;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行卡号
     */
    private String bankAcctNo;

    /**
     * 银行户名
     */
    private String bankAcctName;

    /**
     * 批次号
     */
    private String regId;

    /**
     * 批次明细号
     */
    private String regDtlSn;

    /**
     * 摘要
     */
    private String abst;

    /**
     * 备注
     */
    private String remark;

    /**
     * 交易手续费
     */
    private String transFee;

    /**
     * 交易手续费入账钱包id
     */
    private String transFeeWalletId;

    /**
     * 交易手续费入账钱包名称
     */
    private String transFeeWalletName;

    /**
     * 资金符号
     */
    private String amtSymbol;

    /**
     * 资金计划项目ID
     */
    private String cptlPlnPrjctId;

    /**
     * 资金计划项目编号
     */
    private String cptlPlnPrjctCode;

    /**
     * 资金计划项目名称
     */
    private String cptlPlnPrjctName;

    /**
     * 付款类型
     */
    private String payType;

    /**
     * 付款银行行号
     */
    private String payBankCode;

    /**
     * 付款银行名称
     */
    private String payBankName;

    /**
     * 付款银行卡号
     */
    private String payBankAcctNo;

    /**
     * 付款银行账户名称
     */
    private String payBankAcctName;
}
