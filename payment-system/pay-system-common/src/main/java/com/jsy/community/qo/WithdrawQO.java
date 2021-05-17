package com.jsy.community.qo;

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
public class WithdrawQO implements Serializable {

    // 商户订单号-选填
    private String mctOrderNo;

    // 钱包ID-必填
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    // 提现金额-必填
    @NotBlank(message = "提现金额不能为空")
    private String amount;

    // 手续费-选填
    private String feeAmt;

    // 手续费收入钱包ID-选填
    private String feeIntoWalletId;

    // 提现类型-必填;T0：快捷提现；
    @NotBlank(message = "提现类型不能为空")
    private String withdrawType;

    // 支付密码密文-条件必填
    private String encryptPwd;

    // 加密类型-条件必填
    private String encryptType;

    // 控件随机因子-条件必填
    private String plugRandomKey;

    // 证书签名密文-条件必填
    private String certSign;

    // 验证方式-条件必填
    @NotBlank(message = "验证方式不能为空")
    private String tradeWayCode;

    // 验证字段-条件必填
    @NotBlank(message = "验证字段不能为空")
    private String tradeWayFeilds;

    // 提现银行账号-已绑定的银行账号，如果不填，表示提现到绑定的默认银行卡。
    private String bankAcctNo;

    // 备注-选填
    private String remark;

    // 摘要-选填
    private String abst;

    // 附言-选填
    private String postscript;
}
