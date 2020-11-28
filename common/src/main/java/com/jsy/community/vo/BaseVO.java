package com.jsy.community.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 前端传参BaseVO
 * @since 2020-11-28 11:10
 **/
@Data
public class BaseVO implements Serializable {
	
	@ApiModelProperty(value = "ID")
	private Long id;
	
}
