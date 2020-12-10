package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author jsy
 * @since 2020-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_pay_order")
@ApiModel(value="PayOrder对象", description="订单表")
public class PayOrderEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单流水号")
    private String orderNum;

    @ApiModelProperty(value = "缴费单位")
    private String unit;

    @ApiModelProperty(value = "缴费金额")
    private Double paymentAmount;

    @ApiModelProperty(value = "缴费状态 0 未到账 1已到账")
    private Integer status;

    @ApiModelProperty(value = "户号")
    private String familyId;

    @ApiModelProperty(value = "下单时间")
    private Date orderTime;

}
