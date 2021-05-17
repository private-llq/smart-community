package com.jsy.community.qo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 开户回调接参
 * @Date: 2021/5/10 17:21
 * @Version: 1.0
 **/
@Data
public class CredentialNotifyQO implements Serializable {

    // 注册号
    private String registerNo;

    // 注册状态
    private String regStatus;

    // 钱包ID
    private String walletId;

    // 钱包名称
    private String walletName;

    // 企业名称
    private String companyName;

    // 营业执照号
    private String bizLicNo;

    // 法人名称
    private String legalName;

    // 注册使用的电话号码
    private String mobileNo;

    // 确认金状态
    private String confirmAmtStatus;

    // 确认金金额
    private String confirmAmt;

    // 取消原因内容
    private String unpassReasonContent;

    // 银行账户类型
    private String bankAcctType;

    private String msgType;
}
