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
	
	//兼容H5，使用字符串格式， 针对js long型长度不够的问题
	public String getIdStr(){
		return String.valueOf(id);
	}
	
}
