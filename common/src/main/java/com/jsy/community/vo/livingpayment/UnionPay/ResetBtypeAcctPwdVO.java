package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: B端钱包重置支付密码返参
 * @Date: 2021/5/11 17:51
 * @Version: 1.0
 **/
@Data
@ApiModel("B端钱包重置支付密码返参")
public class ResetBtypeAcctPwdVO extends UnionPayBaseVO implements Serializable {
    @ApiModelProperty("交易订单号")
    private String transOrderNo;
}
