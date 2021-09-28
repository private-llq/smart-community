package com.jsy.community.qo.payment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 微信支付入参
 * @author: Hu
 * @create: 2021-01-22 14:12
 **/
@Data
public class WeChatH5PayQO {
    @ApiModelProperty(value = "交易类型 1.微信支付 2.支付宝支付")
    private Integer payType;
    @ApiModelProperty(value = "支付描述")
    private String descriptionStr;
    @ApiModelProperty("支付金额")
    private BigDecimal money;
    @ApiModelProperty("订单编号")
    private String orderNum;

    @ApiModelProperty("社区id")
    private Long communityId;

    @ApiModelProperty(value = "交易类型 0.积分支付 1.钱支付")
    private Integer type;

//    @ApiModelProperty("订单id集合")
//    private String ids;
//
//    @ApiModelProperty("其他服务id")
//    private String serviceOrderNo;


}
