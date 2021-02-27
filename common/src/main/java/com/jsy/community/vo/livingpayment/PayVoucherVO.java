package com.jsy.community.vo.livingpayment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description: 缴费凭证
 * @author: Hu
 * @create: 2020-12-28 15:34
 **/
@Data
@ApiModel("缴费凭证")
public class PayVoucherVO implements Serializable {

    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "缴费单位名称")
    private String companyName;

    @ApiModelProperty(value = "流水号")
    private String orderNum;

    @ApiModelProperty(value = "支付方式名称或者账号")
    private String payTypeName;

    @ApiModelProperty(value = "缴费类型Name")
    private String typeName;

    @ApiModelProperty(value = "缴费状态")
    private Integer status;

    @ApiModelProperty(value = "户号")
    private String familyId;

    @ApiModelProperty(value = "缴费金额")
    private BigDecimal paymentBalance;

    @ApiModelProperty(value = "住址信息")
    private String address;

    @ApiModelProperty(value = "缴费时间")
    private LocalDateTime orderTime;


}
