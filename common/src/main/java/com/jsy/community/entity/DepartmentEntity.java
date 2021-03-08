package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @return
 * @Author lihao
 * @Description 部门
 * @Date 2020/11/28 10:06
 * @Param
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_department")
@ApiModel(value = "Department对象", description = "部门")
public class DepartmentEntity extends BaseEntity {
	
	@ApiModelProperty(value = "社区id")
	@NotNull(groups = {addDepartmentValidate.class, updateDepartmentValidate.class}, message = "社区id不能为空")
	private Long communityId;
	
	@ApiModelProperty(value = "部门名称")
	@NotBlank(groups = {addDepartmentValidate.class, updateDepartmentValidate.class}, message = "部门名称不能为空")
	private String department;
	
	@ApiModelProperty(value = "部门图片")
	private String imgUrl;
	
	@ApiModelProperty(value = "部门电话")
	private String phone;
	
	
	public interface addDepartmentValidate {
	}
	
	public interface updateDepartmentValidate {
	}
}
