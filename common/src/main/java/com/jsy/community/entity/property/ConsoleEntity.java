package com.jsy.community.entity.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author DKS
 * @description 控制台实体
 * @since 2021/8/25  10:56
 **/
@Data
public class ConsoleEntity implements Serializable {
//	@ApiModelProperty(value = "短信到期时间")
//	private LocalDate expirationTime;
	
	@ApiModelProperty(value = "短信剩余数量")
	private Integer messageQuantity;
	
	@ApiModelProperty(value = "小区数量")
	private Integer communityNumber;
	
	@ApiModelProperty(value = "住宅数量")
	private Integer residenceCount;
	
	@ApiModelProperty(value = "商铺数量")
	private Integer shopCount;
	
	@ApiModelProperty(value = "小区房屋总数")
	private Integer houseSum;
	
	@ApiModelProperty(value = "业主人数")
	private Integer ownerCount;
	
	@ApiModelProperty(value = "租户人数")
	private Integer tenantCount;
	
	@ApiModelProperty(value = "居住人数")
	private Integer liveSum;
	
	@ApiModelProperty(value = "未占用车位")
	private Integer unoccupiedCarPosition;
	
	@ApiModelProperty(value = "已占用车位")
	private Integer occupyCarPosition;
	
	@ApiModelProperty(value = "车位总数")
	private Integer carPositionSum;
	
	@ApiModelProperty(value = "物业每月的物业费统计")
	private List<Map<String, BigDecimal>> monthByPropertyFee;
	
	@ApiModelProperty(value = "物业每年的物业费统计")
	private BigDecimal yearByPropertyFee;
	
	@ApiModelProperty(value = "物业每月的车位费统计")
	private List<Map<String, BigDecimal>> monthByCarPositionFee;
	
	@ApiModelProperty(value = "物业每年的车位费统计")
	private BigDecimal yearByCarPositionFee;
	
	@ApiModelProperty(value = "物业年总收入")
	private BigDecimal yearByTotalFee;
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "月份")
	private String month;
	
	@ApiModelProperty(value = "与月份相对应的物业费")
	private BigDecimal propertyFee;
}
