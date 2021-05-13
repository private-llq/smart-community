package com.jsy.community.qo.payment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 提现接参
 * @Date: 2021/5/10 10:09
 * @Version: 1.0
 **/
@Data
@ApiModel("提现接参")
public class WithdrawQO implements Serializable {

    @ApiModelProperty("商户订单号-选填")
    private String mctOrderNo;

    @ApiModelProperty("钱包ID-必填")
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    @ApiModelProperty("提现金额-必填")
    @NotBlank(message = "提现金额不能为空")
    private String amount;

    @ApiModelProperty("手续费-选填")
    private String feeAmt;

    @ApiModelProperty("手续费收入钱包ID-选填")
    private String feeIntoWalletId;

    @ApiModelProperty("提现类型-必填;T0：快捷提现；")
    private String withdrawType;

    @ApiModelProperty("支付密码密文-条件必填")
    private String encryptPwd;

    @ApiModelProperty("加密类型-条件必填")
    private String encryptType;

    @ApiModelProperty("控件随机因子-条件必填")
    private String plugRandomKey;

    @ApiModelProperty("证书签名密文-条件必填")
    private String certSign;

    @ApiModelProperty("验证方式-条件必填")
    private String tradeWayCode;

    @ApiModelProperty("验证字段-条件必填")
    private String tradeWayFeilds;

    @ApiModelProperty("提现银行账号-已绑定的银行账号，如果不填，表示提现到绑定的默认银行卡。")
    private String bankAcctNo;

    @ApiModelProperty("备注-选填")
    private String remark;

    @ApiModelProperty("摘要-选填")
    private String abst;

    @ApiModelProperty("附言-选填")
    private String postscript;
}
