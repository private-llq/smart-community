package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 银联余额返参
 * @Date: 2021/4/28 17:19
 * @Version: 1.0
 **/
@Data
@ApiModel("银联余额返参")
public class BalanceVO extends UnionPayBaseVO implements Serializable {

    @ApiModelProperty("钱包ID")
    private String walletId;

    @ApiModelProperty("可用余额")
    private String avlblAmt;

    @ApiModelProperty("冻结余额")
    private String frznAmt;

    @ApiModelProperty("授信额度")
    private String creditBal;
}
