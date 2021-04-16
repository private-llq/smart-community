package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lihao
 * @ClassName DepartmentVO
 * @Date 2021/3/11  17:32
 * @Description 通讯录部门树形结构展示效果
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "DepartmentStaffVO", description = "通讯录Excel导出")
public class DepartmentStaffVO implements Serializable {
	
	@ApiModelProperty(value = "员工姓名")
	private String person;
	
	@ApiModelProperty(value = "部门")
	private String department;
	
	@ApiModelProperty(value = "联系电话")
	private String phone;
	
	@ApiModelProperty(value = "职务")
	private String duty;
	
	@ApiModelProperty(value = "邮箱")
	private String email;
	
	@ApiModelProperty(value = "添加失败原因")
	private String failReason;
}
