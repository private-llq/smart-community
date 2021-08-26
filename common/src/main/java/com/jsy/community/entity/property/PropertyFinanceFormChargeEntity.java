package com.jsy.community.entity.property;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author DKS
 * @description 物业财务报表-小区收费报表
 * @since 2021/8/18  10:46
 **/
@Data
@ApiModel("物业财务报表-小区收费报表")
public class PropertyFinanceFormChargeEntity implements Serializable {
	@ApiModelProperty(value = "账单号")
	private Long feeRuleId;
	
	@ApiModelProperty(value = "社区ID")
	private Long communityId;
	
	@ApiModelProperty(value = "合计应收、本月应收=所有账单总金额")
	private BigDecimal totalMoney;
	
	@ApiModelProperty(value = "往月欠收=往月所有账单待支付")
	private BigDecimal arrearsMoney;
	
	@ApiModelProperty(value = "合计欠收=往月欠收+本月欠收")
	private BigDecimal arrearsMoneySum;
	
	@ApiModelProperty(value = "本月欠收=本月所有账单待支付")
	private BigDecimal thisMonthArrearsMoney;
	
	@ApiModelProperty(value = "所有优惠金额")
	private BigDecimal couponMoney;
	
	@ApiModelProperty(value = "所有违约应收金额")
	private BigDecimal receivablePenalMoney;
	
	@ApiModelProperty(value = "已支付违约金=违约实收金额")
	private BigDecimal collectPenalMoney;
	
	@ApiModelProperty(value = "线上收费、合计实收=所有已支付账单")
	private BigDecimal communityOnlineCharging;
	
	@ApiModelProperty(value = "线下收费")
	private BigDecimal communityOfflineCharging;
	
	@ApiModelProperty(value = "所有预存款金额")
	private BigDecimal deductionMoney;
	
	@ApiModelProperty(value = "支付开始时间")
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private LocalDate startTime;
	
	@ApiModelProperty(value = "支付结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private LocalDate endTime;
	
	@ApiModelProperty(value = "按年查询")
	private Integer year;
	
	@ApiModelProperty(value = "按月查询")
	private Integer month;
	
	@ApiModelProperty(value = "1.按账单生成时间，2.按账单周期时间")
	private Integer type;
	
	@ApiModelProperty(value = "项目名称")
	private String feeRuleName;
}
