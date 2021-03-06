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
import java.time.LocalDateTime;

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
    private Long feeRuleId;
    @ApiModelProperty(value = "账单抬头")
    private String rise;
    @ApiModelProperty(value = "账单类型")
    private Integer type;
    @ApiModelProperty(value = "关联类型1房屋，2车位")
    private Integer associatedType;
    @ApiModelProperty(value = "账单号")
    private String orderNum;
    @ApiModelProperty(value = "生成类型1系统生成，2临时收费")
    private Integer buildType;
    @ApiModelProperty(value = "支付单号")
    private Long communityId;
    @ApiModelProperty(value = "应缴月份")
    private LocalDate orderTime;
    @ApiModelProperty(value = "用户id")
    private String uid;
    @ApiModelProperty(value = "房间id")
    private Long targetId;
    @ApiModelProperty(value = "物业费")
    private BigDecimal propertyFee;
    @ApiModelProperty(value = "违约金")
    private BigDecimal penalSum;
    @ApiModelProperty(value = "优惠金额")
    private BigDecimal coupon;
    @ApiModelProperty(value = "预存款抵扣")
    private BigDecimal deduction;
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
    @ApiModelProperty(value = "支付方式 1 app支付 ，2 物业后台")
    private Integer payType;
    @ApiModelProperty(value = "付款时间")
    private LocalDateTime payTime;
    @ApiModelProperty(value = "三方单号")
    private String tripartiteOrder;

    @ApiModelProperty(value = "账单开始时间")
    private LocalDate beginTime;
    @ApiModelProperty(value = "账单结束时间")
    private LocalDate overTime;
    @ApiModelProperty(value = "状态1显示，2隐藏")
    private Integer hide;


    @ApiModelProperty(value = "房屋全称",hidden = true)
    @TableField(exist = false)
    private String address;

    @ApiModelProperty(value = "缴费项目名称",hidden = true)
    @TableField(exist = false)
    private String feeRuleName;
    
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
    
    @ApiModelProperty(value = "查询条件 - 支付开始日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate payTimeStartDate;
    @ApiModelProperty(value = "查询条件 - 支付结束日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate payTimeEndDate;
    
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
    
    @ApiModelProperty(value = "所有违约应收金额")
    @TableField(exist = false)
    private BigDecimal receivablePenalMoney;
    
    @ApiModelProperty(value = "所有优惠金额")
    @TableField(exist = false)
    private BigDecimal couponMoney;
    
    @ApiModelProperty(value = "所有预存款金额")
    @TableField(exist = false)
    private BigDecimal deductionMoney;
    
    @ApiModelProperty(value = "已支付违约金=违约实收金额")
    @TableField(exist = false)
    private BigDecimal collectPenalMoney;
    
    @ApiModelProperty(value = "线上收费、合计实收=所有已支付账单")
    @TableField(exist = false)
    private BigDecimal communityOnlineCharging;
    
    @ApiModelProperty(value = "往月欠收=往月所有账单待支付")
    @TableField(exist = false)
    private BigDecimal arrearsMoney;
    
    @ApiModelProperty(value = "合计欠收=往月欠收+本月欠收")
    @TableField(exist = false)
    private BigDecimal arrearsMoneySum;
    
    @ApiModelProperty(value = "本月欠收=本月所有账单待支付")
    @TableField(exist = false)
    private BigDecimal thisMonthArrearsMoney;
    
    @ApiModelProperty(value = "微信支付")
    @TableField(exist = false)
    private BigDecimal weChatPaySum;
    
    @ApiModelProperty(value = "支付宝支付")
    @TableField(exist = false)
    private BigDecimal aliPaySum;
    
    @ApiModelProperty(value = "余额支付")
    @TableField(exist = false)
    private BigDecimal balancePaySum;
    
    @ApiModelProperty(value = "现金支付")
    @TableField(exist = false)
    private BigDecimal cashPaySum;
    
    @ApiModelProperty(value = "银联刷卡支付")
    @TableField(exist = false)
    private BigDecimal UnionPaySum;
    
    @ApiModelProperty(value = "银行代扣支付")
    @TableField(exist = false)
    private BigDecimal bankPaySum;
    
    @ApiModelProperty(value = "合计支付")
    @TableField(exist = false)
    private BigDecimal totalSum;
    
    @ApiModelProperty("手机号码")
    @TableField(exist = false)
    private String mobile;
    
    @ApiModelProperty("账单主体")
    @TableField(exist = false)
    private String financeTarget;
}
