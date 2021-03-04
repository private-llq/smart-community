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
 * @description: 微信支付订单
 * @author: Hu
 * @create: 2021-01-25 10:06
 **/
@ApiModel("微信支付订单记录表")
@Data
@TableName("t_wechat_order")
public class WeChatOrderEntity implements Serializable {
    @ApiModelProperty(value = "订单号")
    private String id;
    @ApiModelProperty(value = "微信支付订单号")
    private String transactionId;
    @ApiModelProperty(value = "用户id")
    private String uid;
    @ApiModelProperty(value = "商品描述")
    private String description;
    @ApiModelProperty(value = "用户标识")
    private String openId;
    @ApiModelProperty(value = "支付金额")
    private BigDecimal amount;
    @ApiModelProperty(value = "订单状态，1已付款，2未付款")
    private Integer orderStatus;
    @ApiModelProperty(value = "到账状态，1已到账，2未到账")
    private Integer arriveStatus;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}
