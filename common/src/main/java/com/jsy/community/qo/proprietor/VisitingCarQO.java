package com.jsy.community.qo.proprietor;

import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;

/**
 * @author chq459799974
 * @description 随行车辆QO
 * @since 2020-11-28 16:06
 **/
@Data
public class VisitingCarQO extends BaseEntity {
	
	@ApiModelProperty(value = "来访车辆车牌")
	@Pattern(regexp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$", message = "请输入一个正确的车牌号!")
	private String carPlate;
	
	@ApiModelProperty(value = "来访车辆类型ID")
	@Range(min = BusinessEnum.CarTypeEnum.CAR_TYPE_MIN, max = BusinessEnum.CarTypeEnum.CAR_TYPE_MAX, message = "车辆类型不合规范")
	private Integer carType;
}
