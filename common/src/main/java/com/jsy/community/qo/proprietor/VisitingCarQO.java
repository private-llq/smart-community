package com.jsy.community.qo.proprietor;

import com.jsy.community.constant.BusinessEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 随行车辆QO
 * @since 2020-11-28 16:06
 **/
@Data
public class VisitingCarQO implements Serializable {
	
	@ApiModelProperty(value = "ID")
	private Long id;
	
	@ApiModelProperty(value = "来访车辆车牌")
	private String carPlate;
	
	@ApiModelProperty(value = "来访车辆类型ID")
	@Range(min = BusinessEnum.CarTypeEnum.CARTYPE_MIN, max = BusinessEnum.CarTypeEnum.CARTYPE_MAX, message = "车辆类型不合规范")
	private Integer carType;
}
