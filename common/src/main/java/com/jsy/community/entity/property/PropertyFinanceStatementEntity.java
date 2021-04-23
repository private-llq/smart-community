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
 * @Author: Pipi
 * @Description: 物业财务-结算单实体
 * @Date: 2021/4/21 17:57
 * @Version: 1.0
 **/
@Data
@ApiModel("物业财务-结算单")
@TableName("t_property_finance_statement")
public class PropertyFinanceStatementEntity extends BaseEntity {

    @ApiModelProperty("社区id")
    private Long communityId;

    @ApiModelProperty("结算单号")
    private String statementNum;

    @ApiModelProperty("结算时间段-开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDateTime startDate;

    @ApiModelProperty("结算时间段-开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDateTime endDate;

    @ApiModelProperty("结算状态1.待审核 2.结算中 3.已结算 4.驳回")
    private Integer statementStatus;

    @ApiModelProperty("结算金额")
    private BigDecimal totalMoney;

    @ApiModelProperty("收款账户id(物业对公账户)")
    private Long receiptAccount;

    @ApiModelProperty("开户账户名称")
    private String accountName;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("开户行所在城市")
    private String bankCity;

    @ApiModelProperty("开户支行名称")
    private String bankBranchName;

    @ApiModelProperty("银行卡号")
    private String bankNo;

    @ApiModelProperty("驳回原因")
    private String rejectReason;
    
    @ApiModelProperty(value = "查询条件 - 结算单开始日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate createStartDate;
    @ApiModelProperty(value = "查询条件 - 结算单结束日期")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate createEndDate;

}
