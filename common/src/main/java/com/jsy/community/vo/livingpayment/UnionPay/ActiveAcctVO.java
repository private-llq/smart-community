package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 激活账户返参
 * @Date: 2021/5/12 17:17
 * @Version: 1.0
 **/
@Data
@ApiModel("激活账户返参")
public class ActiveAcctVO extends UnionPayBaseVO implements Serializable {

    @ApiModelProperty("钱包ID")
    private String walletId;

    @ApiModelProperty("交易订单号")
    private String transOrderNo;
}
