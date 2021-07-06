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
public class WeChatPayQO {
    @ApiModelProperty(value = "支付描述 1.充值提现2.商城购物3.水电缴费4.物业管理5.房屋租金6.红包")
    private Integer tradeFrom;
    @ApiModelProperty(value = "支付描述")
    private String descriptionStr;
    @ApiModelProperty("支付金额")
    private BigDecimal amount;
    @ApiModelProperty("商城订单")
    private Map<String,Object> orderData;

    @ApiModelProperty("订单id集合")
    private String ids;


}
