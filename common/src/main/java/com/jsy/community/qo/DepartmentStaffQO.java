package com.jsy.community.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author lihao
 * @ClassName DepartmentStaffQO
 * @Date 2021/3/9  15:27
 * @Description
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="DepartmentStaff对象", description="用于员工添加和修改")
public class DepartmentStaffQO implements Serializable {
	@ApiModelProperty(value = "员工id，更新时需要")
	@NotNull(groups = {DepartmentStaffQO.updateStaffValidate.class}, message = "员工id不能为空")
	private Long id;
	
	@ApiModelProperty(value = "部门id")
	@NotNull(groups = {DepartmentStaffQO.addStaffValidate.class, DepartmentStaffQO.updateStaffValidate.class}, message = "部门id不能为空")
	private Long departmentId;
	
	@ApiModelProperty(value = "社区id")
	@NotNull(groups = {DepartmentStaffQO.addStaffValidate.class, DepartmentStaffQO.updateStaffValidate.class}, message = "社区id不能为空")
	private Long communityId;
	
	@ApiModelProperty(value = "员工")
	@NotBlank(groups = {DepartmentStaffQO.addStaffValidate.class, DepartmentStaffQO.updateStaffValidate.class},message = "员工姓名不能为空")
	@Length(groups = {DepartmentStaffQO.addStaffValidate.class, DepartmentStaffQO.updateStaffValidate.class},min = 1,max = 50)

	private String person;
	
	@ApiModelProperty(value = "联系电话")
	@Size(groups = {DepartmentStaffQO.addStaffValidate.class, DepartmentStaffQO.updateStaffValidate.class},min = 0,max = 3,message = "最多添加三个联系电话")
	private List<String> phone;
	
	@ApiModelProperty(value = "职务")
	@NotBlank(groups = {DepartmentStaffQO.addStaffValidate.class, DepartmentStaffQO.updateStaffValidate.class},message = "职务不能为空")
	@Length(groups = {DepartmentStaffQO.addStaffValidate.class, DepartmentStaffQO.updateStaffValidate.class},min = 1,max = 20)
	private String duty;
	
	@ApiModelProperty(value = "邮箱")
	private String email;
	
	public interface addStaffValidate {
	}
	
	public interface updateStaffValidate {
	}
}
