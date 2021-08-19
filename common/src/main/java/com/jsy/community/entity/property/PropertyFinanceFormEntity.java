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
 * @description 物业财务报表实体
 * @since 2021/8/17  14:46
 **/
@Data
@ApiModel("物业财务报表实体")
public class PropertyFinanceFormEntity implements Serializable {
	@ApiModelProperty(value = "社区ID")
	private Long communityId;
	
	@ApiModelProperty(value = "押金线上收费")
	private BigDecimal depositOnlineCharging;
	
	@ApiModelProperty(value = "押金线下收费")
	private BigDecimal depositOfflineCharging;
	
	@ApiModelProperty(value = "押金退款")
	private BigDecimal depositRefund;
	
	@ApiModelProperty(value = "押金收款合计")
	private BigDecimal depositTotal;
	
	@ApiModelProperty(value = "小区线上收费")
	private BigDecimal communityOnlineCharging;
	
	@ApiModelProperty(value = "小区线下收费")
	private BigDecimal communityOfflineCharging;
	
	@ApiModelProperty(value = "小区收款合计")
	private BigDecimal communityTotal;
	
	@ApiModelProperty(value = "预存款线上收费")
	private BigDecimal advanceDepositOnlineCharging;
	
	@ApiModelProperty(value = "预存款线下收费")
	private BigDecimal advanceDepositOfflineCharging;
	
	@ApiModelProperty(value = "预存款提现")
	private BigDecimal advanceDepositWithdrawal;
	
	@ApiModelProperty(value = "预存款收款合计")
	private BigDecimal advanceDepositTotal;
	
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
	
}
