package com.jsy.community.qo.payment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 开户回调接参
 * @Date: 2021/5/10 17:21
 * @Version: 1.0
 **/
@Data
@ApiModel("开户回调接参")
public class CredentialNotifyQO implements Serializable {

    @ApiModelProperty("注册号")
    private String registerNo;

    @ApiModelProperty("注册状态")
    private String regStatus;

    @ApiModelProperty("钱包ID")
    private String walletId;

    @ApiModelProperty("钱包名称")
    private String walletName;

    @ApiModelProperty("企业名称")
    private String companyName;

    @ApiModelProperty("营业执照号")
    private String bizLicNo;

    @ApiModelProperty("法人名称")
    private String legalName;

    @ApiModelProperty("注册使用的电话号码")
    private String mobileNo;

    @ApiModelProperty("确认金状态")
    private String confirmAmtStatus;

    @ApiModelProperty("确认金金额")
    private String confirmAmt;

    @ApiModelProperty("取消原因内容")
    private String unpassReasonContent;

    @ApiModelProperty("银行账户类型")
    private String bankAcctType;

    private String msgType;
}
