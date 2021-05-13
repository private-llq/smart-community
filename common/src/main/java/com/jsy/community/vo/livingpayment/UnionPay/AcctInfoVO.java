package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("钱包ID")
    private String walletId;

    @ApiModelProperty("钱包名称")
    private String walletName;

    @ApiModelProperty("账户状态码")
    private String acctStatus;

    @ApiModelProperty("账户等级")
    private String acctLevel;

    @ApiModelProperty("账户状态描述")
    private String acctStatusDscrb;

    @ApiModelProperty("账户属性")
    private String acctAttribute;

    @ApiModelProperty("可充值状态,0:不可,1:可以")
    private String rechargeCode;

    @ApiModelProperty("可消费状态,0:不可,1:可以")
    private String consumeCode;

    @ApiModelProperty("可转账状态,0:不可,1:可以")
    private String transferCode;

    @ApiModelProperty("可提现状态,0:不可,1:可以")
    private String withdrawCode;

    @ApiModelProperty("可汇款状态,0:不可,1:可以")
    private String remittanceCode;

    @ApiModelProperty("可冻结状态,0:不可,1:可以")
    private String freezeCode;

    @ApiModelProperty("可解冻状态,0:不可,1:可以")
    private String thawCode;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("手机号")
    private String mobileNo;

    @ApiModelProperty("用户中心userUuid")
    private String userUuid;

    @ApiModelProperty("客商ID")
    private String custId;

    @ApiModelProperty("客商名称")
    private String custName;
}
