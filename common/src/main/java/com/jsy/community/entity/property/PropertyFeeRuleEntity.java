package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-04-20 14:01
 **/
@Data
@ApiModel("物业缴费收费标准")
@TableName("t_property_fee_rule")
public class PropertyFeeRuleEntity extends BaseEntity {
    @ApiModelProperty(value = "1物业费，2车位费")
    private Integer type;
    @ApiModelProperty(value = "编号")
    private String serialNumber;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "周期1日，2周，3月，4季，5年")
    private Integer period;
    @ApiModelProperty(value = "计费方式1面积，2定额")
    private Integer chargeMode;
    @ApiModelProperty(value = "金额")
    private BigDecimal monetaryUnit;
    @ApiModelProperty(value = "违约金")
    private BigDecimal penalSum;
    @ApiModelProperty(value = "违约金多少天开始计费")
    private Integer penalDays;
    @ApiModelProperty(value = "状态0未启用，1启用")
    private Integer status;
    @ApiModelProperty(value = "创建人")
    private String createBy;
    @ApiModelProperty(value = "最近修改人")
    private String updateBy;
}
