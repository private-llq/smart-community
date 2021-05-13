package com.jsy.community.qo.payment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: B端钱包重置支付密码接参
 * @Date: 2021/5/11 17:40
 * @Version: 1.0
 **/
@Data
@ApiModel("B端钱包重置支付密码接参")
public class ResetBtypeAcctPwdQO implements Serializable {

    @ApiModelProperty("商户订单号-选填")
    private String mctOrderNo;

    @ApiModelProperty("钱包ID-必填")
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    @ApiModelProperty("新密码的密文-必填")
    @NotBlank(message = "新密码的密文不能为空")
    private String encryptNewPwd;

    @ApiModelProperty("加密类型-必填")
    @NotBlank(message = "加密类型不能为空")
    private String encryptType;

    @ApiModelProperty("控件随机因子-必填")
    @NotBlank(message = "控件随机因子不能为空")
    private String plugRandomKey;

    @ApiModelProperty("法人代表姓名-必填")
    @NotBlank(message = "法人代表姓名不能为空")
    private String legalName;

    @ApiModelProperty("法人代表身份证号码-必填")
    @NotBlank(message = "法人代表身份证号码不能为空")
    private String legalIdCard;

    @ApiModelProperty("法人代表手机号码-必填")
    @NotBlank(message = "法人代表手机号码不能为空")
    private String legalPhoneNum;

    @ApiModelProperty("法人代表短信验证码-必填")
    @NotBlank(message = "法人代表短信验证码不能为空")
    private String legalSmsAuthCode;

    @ApiModelProperty("代理人姓名-条件必填")
    private String agentName;

    @ApiModelProperty("代理人身份证号码-条件必填")
    private String agentIdCard;

    @ApiModelProperty("代理人手机号-条件必填")
    private String agentPhoneNum;

    @ApiModelProperty("代理人短信验证码-条件必填")
    private String agentSmsAuthCode;

    @ApiModelProperty("备注-选填")
    private String remark;
}
