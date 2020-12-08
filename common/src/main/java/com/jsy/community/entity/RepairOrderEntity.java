package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 报修订单信息
 * </p>
 *
 * @author jsy
 * @since 2020-12-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_repair_order")
@ApiModel(value="RepairOrder对象", description="报修订单信息")
public class RepairOrderEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "房屋报修id")
    private Long repairId;

    @ApiModelProperty(value = "订单编号")
    private String number;

    @ApiModelProperty(value = "维修人电话")
    private String dealMobile;

    @ApiModelProperty(value = "维修人姓名")
    private String dealName;

    @ApiModelProperty(value = "用户评价")
    private String comment;

    @ApiModelProperty(value = "维修金额")
    private BigDecimal repairMoney;

    @ApiModelProperty(value = "订单状态 0 待处理 1 处理中 2 已处理 3 未通过审核")
    private Integer orderStatus;

    @ApiModelProperty(value = "订单处理状态 0 待处理 1 处理中 2 已处理 3 未通过审核")
    private Integer result;

    @ApiModelProperty(value = "下单时间")
    private Date orderTime;

}
