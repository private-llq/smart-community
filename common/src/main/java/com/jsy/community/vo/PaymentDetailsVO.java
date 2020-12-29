package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description: 缴费成功后返回订单详情
 * @author: Hu
 * @create: 2020-12-28 14:40
 **/
@Data
@ApiModel("缴费成功后返回的数据")
public class PaymentDetailsVO implements Serializable {

    @ApiModelProperty(value = "订单id")
    private Long id;

    @ApiModelProperty(value = "缴费单位名称")
    private String unitName;

    @ApiModelProperty(value = "户号")
    private String familyId;

    @ApiModelProperty(value = "缴费时间")
    private LocalDateTime orderTime;

    @ApiModelProperty(value = "到账时间")
    private LocalDateTime accountingTime;

    @ApiModelProperty(value = "缴费金额")
    private BigDecimal paySum;

    @ApiModelProperty(value = "住址信息")
    private String address;


}
