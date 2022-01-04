package com.jsy.community.qo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
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

	// 用户uid
	private String uid;
	
	@ApiModelProperty(value = "社区ID")
	private Long communityId;
	
	@ApiModelProperty(value = "有权限的社区ids(List)")
	private List<String> communityIdList;
	
	@ApiModelProperty(value = "查询条件 name")
	private String name;
	
	@ApiModelProperty(value = "编号")
	private String number;
	
	@ApiModelProperty(value = "姓名")
	private String nickName;
	
	@ApiModelProperty(value = "电话号码")
	private String mobile;
	
	// 密码
	@Pattern(regexp = "^(?=.*[A-Z0-9])(?=.*[a-z0-9])(?=.*[a-zA-Z])(.{6,12})$", message = "请输入一个正确的6-12位密码,至少包含大写字母或小写字母或数字两种!")
	private String password;
	
	@ApiModelProperty(value = "身份证号")
	private String idCard;
	
	@ApiModelProperty(value = "状态 0.正常 1.禁用")
	private Integer status;

	// 公司ID
	private Long companyId;

	// 物业角色ID
	private Long roleId;
	
	// 小区角色ID
	private Long communityRoleId;
	
	@ApiModelProperty(value = "物业公司名称")
	private String companyName;

}
