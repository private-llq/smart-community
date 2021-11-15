package com.jsy.community.qo.unionpay;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 钱包ID接参
 * @Date: 2021/4/14 10:55
 * @Version: 1.0
 **/
@Data
public class WalletIdQO implements Serializable {

    // 钱包ID-必填
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    // 银行账户
    private String bankAcctNo;

    // 查询类型
    private String signBindType;

    // 签约商户号
    private String signMerNo;
}
