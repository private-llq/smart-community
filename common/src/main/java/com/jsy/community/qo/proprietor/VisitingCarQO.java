package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chq459799974
 * @description 随行车辆QO
 * @since 2020-11-28 16:06
 **/
@Data
public class VisitingCarQO {
	
	@ApiModelProperty(value = "ID")
	private Long id;
	
	@ApiModelProperty(value = "来访车辆车牌")
	private String carPlate;
	
	@ApiModelProperty(value = "来访车辆类型ID")
	private Integer carType;
}
