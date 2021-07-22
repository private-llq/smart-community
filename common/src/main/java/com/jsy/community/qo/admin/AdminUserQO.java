package com.jsy.community.qo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chq459799974
 * @description 操作员查询QO
 * @since 2021-03-16 09:42
 **/
@Data
public class AdminUserQO implements Serializable {
	
	@ApiModelProperty(value = "id")
	private Long id;
	
	@ApiModelProperty(value = "社区ID")
	private Long communityId;
	
	@ApiModelProperty(value = "有权限的社区ids(List)")
	private List<Long> communityIdList;
	
	@ApiModelProperty(value = "查询条件 name")
	private String name;
	
	@ApiModelProperty(value = "编号")
	private String number;
	
	@ApiModelProperty(value = "姓名")
	private String realName;
	
	@ApiModelProperty(value = "电话号码")
	private String mobile;
	
	@ApiModelProperty(value = "身份证号")
	private String idCard;
	
	@ApiModelProperty(value = "状态 0.正常 1.禁用")
	private Integer status;
}
