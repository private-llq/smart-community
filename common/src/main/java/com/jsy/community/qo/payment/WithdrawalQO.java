package com.jsy.community.qo.payment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: com.jsy.community
 * @description: 微信提现接收参数类
 * @author: Hu
 * @create: 2021-01-29 14:52
 **/
@Data
public class WithdrawalQO {
    @ApiModelProperty(value = "微信用户openid")
    private String openid;
    @ApiModelProperty(value = "提现金额")
    private BigDecimal amount;
    @ApiModelProperty(value = "描述")
    private String desc;
}
