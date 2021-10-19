package com.jsy.community.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author DKS
 * @description 物业公司查询QO
 * @since 2021-10-15 10:47
 **/
@Data
public class PropertyCompanyQO implements Serializable {
	
	@ApiModelProperty(value = "id")
	private Long id;
	
	@ApiModelProperty(value = "查询条件 name")
	private String name;
	
	@ApiModelProperty(value = "省份ID")
	private Integer provinceId;
	
	@ApiModelProperty(value = "城市ID")
	private Integer cityId;
	
	@ApiModelProperty(value = "区ID")
	private Integer areaId;
}
