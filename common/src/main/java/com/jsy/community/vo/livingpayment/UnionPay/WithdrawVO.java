package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 提现申请返参
 * @Date: 2021/5/10 10:34
 * @Version: 1.0
 **/
@Data
@ApiModel("提现申请返参")
public class WithdrawVO extends UnionPayBaseVO implements Serializable {

    @ApiModelProperty("交易订单号")
    private String transOrderNo;

    @ApiModelProperty("银行账号")
    private String bankAcctNo;
}
