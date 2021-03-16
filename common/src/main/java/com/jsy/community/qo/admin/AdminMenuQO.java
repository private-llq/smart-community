package com.jsy.community.qo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 系统菜单
 * @since 2020-12-14 10:01
 **/
@Data
public class AdminMenuQO implements Serializable {
	
	@ApiModelProperty(value = "ID")
	private Long id;
	
	@ApiModelProperty(value = "菜单图标")
	private String icon;
	
	@ApiModelProperty(value = "菜单名")
	private String name;
	
	@ApiModelProperty(value = "菜单url")
	private String url;
	
	@ApiModelProperty(value = "排序")
	private Integer sort;
	
	@ApiModelProperty(value = "父级id")
	private Long pid;
}
