package com.jsy.community.entity.payment;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:  微信提现订单表
 * @author: Hu
 * @create: 2021-01-29 11:36
 **/
@ApiModel("微信提现订单表")
@Data
@TableName("t_withdrawal_order")
public class WithdrawalOrderEntity implements Serializable {
    @ApiModelProperty(value = "订单号")
    private String id;
    @ApiModelProperty(value = "微信用户唯一标识")
    private String openid;
    @ApiModelProperty(value = "提现金额")
    private BigDecimal amount;
    @ApiModelProperty(value = "描述")
    private String desc;
    @ApiModelProperty(value = "发起提现时间")
    private LocalDateTime create_time;
    @ApiModelProperty(value = "提现到账时间")
    private LocalDateTime arrive_time;
    @ApiModelProperty(value = "提现状态,1已到账，2未到账")
    private Integer status;

}
