package com.jsy.community.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author DKS
 * @description 小区概况实体
 * @since 2021/8/24  11:21
 **/
@Data
public class CommunitySurveyEntity implements Serializable {
	@ApiModelProperty(value = "开门次数")
	private Integer openDoorCount;
	
	@ApiModelProperty(value = "行人进入次数")
	private Integer peopleInfoCount;
	
	@ApiModelProperty(value = "车辆进入次数")
	private Integer carInfoCount;
	
	@ApiModelProperty(value = "车辆外出次数")
	private Integer carOutCount;
	
	@ApiModelProperty(value = "访客次数")
	private Integer visitorCount;
	
	@ApiModelProperty(value = "住宅数量")
	private Integer residenceCount;
	
	@ApiModelProperty(value = "商铺数量")
	private Integer shopCount;
	
	@ApiModelProperty(value = "房屋总数")
	private Integer houseSum;
	
	@ApiModelProperty(value = "楼栋总数")
	private Integer buildingSum;
	
	@ApiModelProperty(value = "租户数")
	private Integer tenantCount;
	
	@ApiModelProperty(value = "业主数")
	private Integer ownerCount;
	
	@ApiModelProperty(value = "未占用车位")
	private Integer unoccupiedCarPosition;
	
	@ApiModelProperty(value = "已占用车位")
	private Integer occupyCarPosition;
	
	@ApiModelProperty(value = "车位数")
	private Integer carPositionCount;
	
	@ApiModelProperty(value = "车辆数")
	private Integer carCount;
	
	@ApiModelProperty(value = "每天的物业费统计")
	private List<Map<String,BigDecimal>> dateByPropertyFee;
	
	@ApiModelProperty(value = "每月的物业费统计")
	private BigDecimal monthByPropertyFee;
}
