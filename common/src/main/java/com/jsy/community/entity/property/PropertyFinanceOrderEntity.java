package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @ApiModelProperty(value = "账单号")
    private String orderNum;
    @ApiModelProperty(value = "支付单号")
    private Long communityId;
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
    @ApiModelProperty(value = "账单状态 0.待收款 1.已收款  (关联收款单)")
    private Integer orderStatus;
    @ApiModelProperty(value = "收款单号")
    private String receiptNum;
    @ApiModelProperty(value = "结算单号")
    private String statementNum;
    @ApiModelProperty(value = "结算状态 0.待结算 1.待审核 2.结算中 3.已结算 4.驳回 (关联结算单)")
    private Integer statementStatus;
    @ApiModelProperty(value = "支付类型，1微信，2支付宝")
    private Integer payType;
    @ApiModelProperty(value = "三方单号")
    private String tripartiteOrder;

    @ApiModelProperty(value = "房屋全称",hidden = true)
    @TableField(exist = false)
    private String address;
    
    @ApiModelProperty(value = "业主姓名",hidden = true)
    @TableField(exist = false)
    private String realName;
    
    @ApiModelProperty(value = "收款单信息",hidden = true)
    @TableField(exist = false)
    private PropertyFinanceReceiptEntity receiptEntity;
    
    @ApiModelProperty(value = "结算单信息",hidden = true)
    @TableField(exist = false)
    private PropertyFinanceStatementEntity statementEntity;
    
    //查询条件
    @ApiModelProperty(value = "查询条件 - 账单开始日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate orderStartDate;
    @ApiModelProperty(value = "查询条件 - 账单结束日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate orderEndDate;
    
    @ApiModelProperty(value = "查询条件 - 收款单开始日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate receiptStartDate;
    @ApiModelProperty(value = "查询条件 - 收款单结束日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate receiptEndDate;
    
    @ApiModelProperty(value = "查询条件 - 结算单开始日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate statementStartDate;
    @ApiModelProperty(value = "查询条件 - 结算单结束日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate statementEndDate;

    @ApiModelProperty("账单类型-冗余属性,给前端显示")
    @TableField(exist = false)
    private String orderType;
    
}
