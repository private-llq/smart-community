package com.jsy.community.vo.unionpay;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 账单明细返参
 * @Date: 2021/5/12 11:24
 * @Version: 1.0
 **/
@Data
public class QueryBillInfoVO implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 商户订单号
     */
    private String mctOrderNo;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 账户外部编号
     */
    private String acctExtNo;

    /**
     * 账户名称
     */
    private String acctName;

    /**
     * 交易代码
     */
    private String transCode;

    /**
     * 交易名称
     */
    private String transName;

    /**
     * 收支类型
     */
    private String loanMark;

    /**
     * 交易金额
     */
    private String transAmt;

    /**
     * 交易后余额
     */
    private String postBal;

    /**
     * 清算日期
     */
    private String settDate;

    /**
     * 交易时间
     */
    private String transTime;

    /**
     * 对方账号
     */
    private String otherAcctNo;

    /**
     * 对方账户外部编号
     */
    private String otherAcctExtNo;

    /**
     * 对方账户名
     */
    private String otherAcctName;

    /**
     * 渠道商户号
     */
    private String chnlMerNo;

    /**
     * 渠道商户名称
     */
    private String chnlMerName;

    /**
     * 渠道终端号
     */
    private String chnlTermNo;

    /**
     * 资金渠道
     */
    private String amtChnl;

    /**
     * 资金渠道名称
     */
    private String amtChnlName;

    /**
     * 业务类型编号
     */
    private String bizType;

    /**
     * 业务类型名称
     */
    private String bizTypeName;

    /**
     * 业务商户号
     */
    private String bizMerNo;

    /**
     * 摘要
     */
    private String abst;

    /**
     * 交易备注
     */
    private String remark;

    /**
     * 备用域1
     */
    private String def1;

    /**
     * 备用域2
     */
    private String def2;

    /**
     * 备用域3
     */
    private String def3;

    /**
     * 备用域4
     */
    private String def4;

}
