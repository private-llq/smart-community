package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
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
    @ApiModelProperty(value = "费用类型")
    private Integer type;

    @ApiModelProperty(value = "1临时，2周期")
    @NotNull(message = "类型不能为空",groups = PropertyFeeRule.class)
    private Integer disposable;

    @ApiModelProperty(value = "社区id")
    private Long communityId;

    @ApiModelProperty(value = "编号")
    private String serialNumber;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "周期1日，2周，3月，4季，5年")
    @NotNull(message = "计费周期不能为空！",groups = PropertyFeeRule.class)
    private Integer period;

    @ApiModelProperty(value = "计费方式1面积，2定额")
    @NotNull(message = "计费方式不能为空！",groups = PropertyFeeRule.class)
    private Integer chargeMode;

    @ApiModelProperty(value = "金额")
    @NotNull(message = "收费金额不能为空！",groups = PropertyFeeRule.class)
    private BigDecimal monetaryUnit;

    @ApiModelProperty(value = "违约金")
    @NotNull(message = "违约金不能为空！",groups = PropertyFeeRule.class)
    private BigDecimal penalSum;

    @ApiModelProperty(value = "违约金多少天开始计费")
    @NotNull(message = "违约天数不能为空！",groups = PropertyFeeRule.class)
    private Integer penalDays;

    @ApiModelProperty(value = "状态0未启用，1启用")
    private Integer status;

    @ApiModelProperty(value = "计算方式")
    private String formula;

    @ApiModelProperty(value = "计价方式")
    private String valuation;

    @ApiModelProperty(value = "报表展示0不展示，1展示")
    private Integer reportStatus;

    @ApiModelProperty(value = "每月生成账单的那天")
    private Integer billDay;

    @ApiModelProperty(value = "每月")
    private Integer billMonth;

    @ApiModelProperty(value = "0不推送缴费通知，1推送缴费通知")
    private Integer pushStatus;

    @ApiModelProperty(value = "每月向用户推送订单信息的那天")
    private Integer pushDay;

    @ApiModelProperty(value = "每月向用户推送订单信息")
    private Integer pushMonth;

    @ApiModelProperty(value = "0不预生成未来订单，1预先生成未来的账单")
    private Integer future;

    @ApiModelProperty(value = "0不生成空置房间账单，1生成空置房间账单")
    private Integer leisure;

    @ApiModelProperty(value = "关联车位id,0表示当前小区全部")
    private String relevance;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "最近修改人")
    private String updateBy;

    @ApiModelProperty(value = "修改人名称")
    @TableField(exist = false)
    private String updateByName;

    public interface PropertyFeeRule{}
}
