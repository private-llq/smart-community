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
 * @description 获取收款报表实体
 * @since 2021/8/19  9:41
 **/
@Data
@ApiModel("获取收款报表实体")
public class PropertyCollectionFormEntity implements Serializable {
	@ApiModelProperty(value = "账单号")
	private Long feeRuleId;
	
	@ApiModelProperty(value = "社区ID")
	private Long communityId;
	
	@ApiModelProperty(value = "社区名称")
	private String communityName;
	
	@ApiModelProperty(value = "微信支付")
	private BigDecimal weChatPaySum;
	
	@ApiModelProperty(value = "支付宝支付")
	private BigDecimal aliPaySum;
	
	@ApiModelProperty(value = "余额支付")
	private BigDecimal balancePaySum;
	
	@ApiModelProperty(value = "现金支付")
	private BigDecimal cashPaySum;
	
	@ApiModelProperty(value = "银联刷卡支付")
	private BigDecimal UnionPaySum;
	
	@ApiModelProperty(value = "银行代扣支付")
	private BigDecimal bankPaySum;
	
	@ApiModelProperty(value = "合计支付")
	private BigDecimal totalSum;
	
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
	
	@ApiModelProperty(value = "按日查询")
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private LocalDate dateTime;
	
	@ApiModelProperty(value = "1.按账单生成时间，2.按账单周期时间")
	private Integer type;
	
	@ApiModelProperty(value = "项目名称")
	private String feeRuleName;
	
	@ApiModelProperty(value = "房屋id或者车辆id")
	private Long targetId;
	
	@ApiModelProperty(value = "房屋名称或者车辆编号")
	private String targetIdName;
	
	@ApiModelProperty(value = "收款报表账单统计应收金额")
	private BigDecimal StatementReceivableMoney;
	
	@ApiModelProperty(value = "收款报表账单统计实收金额")
	private BigDecimal StatementCollectMoney;
	
	@ApiModelProperty(value = "收款报表账单统计欠缴金额")
	private BigDecimal StatementArrearsMoney;
}
