package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-04-20 14:50
 **/
@Data
@ApiModel("物业缴费账单表")
@TableName("t_property_finance_order")
public class PropertyFinanceOrderEntity extends BaseEntity {
    @ApiModelProperty(value = "支付单号")
    private String orderNum;
    @ApiModelProperty(value = "应缴月份")
    private LocalDate orderTime;
    @ApiModelProperty(value = "用户id")
    private String uid;
    @ApiModelProperty(value = "房间id")
    private Long houseId;
    @ApiModelProperty(value = "物业费")
    private BigDecimal propertyFee;
    @ApiModelProperty(value = "违约金")
    private BigDecimal penalSum;
    @ApiModelProperty(value = "总金额")
    private BigDecimal totalMoney;
    @ApiModelProperty(value = "状态0未支付，1已支付")
    private Integer status;

}
