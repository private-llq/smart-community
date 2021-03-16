package com.jsy.community.qo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 系统角色
 * @since 2020-12-14 15:41
 **/
@Data
public class AdminRoleQO implements Serializable {
	
	@ApiModelProperty(value = "ID")
	private Long id;
	
	@ApiModelProperty(value = "角色名")
	private String name;
	
	@ApiModelProperty(value = "备注")
	private String remark;
}
