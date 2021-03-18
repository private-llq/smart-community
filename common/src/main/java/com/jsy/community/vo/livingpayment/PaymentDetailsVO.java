package com.jsy.community.vo.livingpayment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description: 缴费详情
 * @author: Hu
 * @create: 2020-12-28 14:40
 **/
@Data
@ApiModel("缴费详情")
public class PaymentDetailsVO implements Serializable {

    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "缴费单位名称")
    private String companyName;

    @ApiModelProperty(value = "户号")
    private String familyId;

    @ApiModelProperty(value = "户名")
    private String familyName;

    @ApiModelProperty(value = "缴费时间")
    private LocalDateTime orderTime;

    @ApiModelProperty(value = "到账时间")
    private LocalDateTime arriveTime;

    @ApiModelProperty(value = "户主余额")
    private BigDecimal accountBalance;

    @ApiModelProperty(value = "缴费金额")
    private BigDecimal paymentBalance;

    @ApiModelProperty(value = "住址信息")
    private String address;

    @ApiModelProperty(value = "缴费状态 0 未到账 1处理中，2已到账")
    private Integer status;



}
