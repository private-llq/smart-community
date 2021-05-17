package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 绑定的银行卡返参
 * @Date: 2021/4/14 17:26
 * @Version: 1.0
 **/
@Data
public class BindBankCardVO implements Serializable {

    /**
     * 银行账户
     */
    private String bankAcctNo;

    /**
     * 银行账户名称
     */
    private String bankAcctName;

    /**
     * 是否默认卡
     */
    private String isDefault;

    /**
     * 开户行号
     */
    private String bankNo;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 电子联行号
     */
    private String elecBankNo;

    /**
     * 银行账户类型
     */
    private String bankAcctType;

    /**
     * 是否信用卡
     */
    private String creditMark;

    /**
     * 电子协议编号
     */
    private String protocolNo;

    /**
     * 签约商户号
     */
    private String signMerNo;

    /**
     * 业务类型
     */
    private String businessCode;

    /**
     * 是否签约类型
     */
    private String signBindType;
}
