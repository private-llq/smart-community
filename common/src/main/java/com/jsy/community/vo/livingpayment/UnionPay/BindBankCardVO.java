package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("银行账户")
    private String bankAcctNo;

    @ApiModelProperty("银行账户名称")
    private String bankAcctName;

    @ApiModelProperty("是否默认卡")
    private String isDefault;

    @ApiModelProperty("开户行号")
    private String bankNo;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("电子联行号")
    private String elecBankNo;

    @ApiModelProperty("银行账户类型")
    private String bankAcctType;

    @ApiModelProperty("是否信用卡")
    private String creditMark;

    @ApiModelProperty("电子协议编号")
    private String protocolNo;

    @ApiModelProperty("签约商户号")
    private String signMerNo;

    @ApiModelProperty("业务类型")
    private String businessCode;

    @ApiModelProperty("是否签约类型")
    private String signBindType;
}
