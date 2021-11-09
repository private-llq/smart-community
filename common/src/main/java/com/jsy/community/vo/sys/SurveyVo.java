package com.jsy.community.vo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description: 大后台概况VO
 * @Author DKS
 * @Date 2021/11/09 10:26
 **/
@Data
@ApiModel("大后台概况VO")
public class SurveyVo implements Serializable {
	
	@ApiModelProperty(value = "访客次数")
	private Integer visitorCount;
	
	@ApiModelProperty(value = "开门次数")
	private Integer openDoorCount;
	
	@ApiModelProperty(value = "车辆进入次数(临时停车)")
	private Integer carInfoCount;
	
	@ApiModelProperty(value = "交易额")
	private BigDecimal turnover;
	
	@ApiModelProperty(value = "物业公司数量")
	private Integer companyCount;
	
	@ApiModelProperty(value = "小区数量")
	private Integer communityCount;
	
	@ApiModelProperty(value = "楼栋总数")
	private Integer buildingSum;
	
	@ApiModelProperty(value = "房屋总数")
	private Integer houseSum;
	
	@ApiModelProperty(value = "住户数量")
	private Integer householdCount;
	
	@ApiModelProperty(value = "车辆数量")
	private Integer carCount;
	
	@ApiModelProperty(value = "车位数量")
	private Integer carPositionCount;
	
	@ApiModelProperty(value = "门禁数量")
	private Integer communityHardWareCount;
	
	@ApiModelProperty(value = "集市数量")
	private Integer marketCount;
	
	@ApiModelProperty(value = "房屋租赁数量")
	private Integer houseLeaseCount;
	
	@ApiModelProperty(value = "报修数量")
	private Integer repairCount;
}
