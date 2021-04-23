package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
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
    private String startDate;

    @ApiModelProperty("结算时间段-开始时间")
    private String endDate;

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

}
