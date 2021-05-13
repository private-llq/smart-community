package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 银联支付返参
 * @Date: 2021/4/26 16:48
 * @Version: 1.0
 **/
@Data
@ApiModel(value = "银联支付返参")
public class UnionPayOrderVO extends UnionPayBaseVO implements Serializable {

    @ApiModelProperty("商户订单号")
    private String mctOrderNo;

    @ApiModelProperty("消费类支付（H5）url")
    private String payH5Url;

    @ApiModelProperty("优惠描述")
    private String disctDesc;

    @ApiModelProperty("优惠金额")
    private String disctAmt;

    @ApiModelProperty("实付金额")
    private String actPayAmt;
}
