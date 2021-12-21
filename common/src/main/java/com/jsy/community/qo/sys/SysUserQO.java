package com.jsy.community.qo.sys;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author DKS
 * @description 操作员查询QO
 * @since 2021-10-13 13:40
 **/
@Data
public class SysUserQO implements Serializable {
	
	@ApiModelProperty(value = "id")
	private Long id;
	
	@ApiModelProperty(value = "查询条件 name")
	private String name;
	
	@ApiModelProperty(value = "姓名")
	private String nickName;
	
	@ApiModelProperty(value = "电话号码")
	private String phone;
	
	@ApiModelProperty(value = "密码")
	private String password;
	
	@ApiModelProperty(value = "身份证号")
	private String idCard;
	
	@ApiModelProperty(value = "状态 0.正常 1.禁用")
	private Integer status;

	// 公司ID
	private Long companyId;

	// 角色ID
	private Long roleId;

}
