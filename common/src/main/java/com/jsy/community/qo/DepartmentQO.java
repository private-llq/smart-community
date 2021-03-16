package com.jsy.community.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author lihao
 * @ClassName DepartmentQO
 * @Date 2021/3/8  17:22
 * @Description TODO
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Department对象", description = "部门")
public class DepartmentQO implements Serializable {
	
	@ApiModelProperty(value = "部门id，更新时需要")
	@NotNull(groups = {updateDepartmentValidate.class}, message = "部门id不能为空")
	private Long id;
	
	@ApiModelProperty(value = "父id")
	@NotNull(groups = {addDepartmentValidate.class, updateDepartmentValidate.class}, message = "父id不能为空")
	private Long pid;
	
	@ApiModelProperty(value = "社区id")
	@NotNull(groups = {addDepartmentValidate.class, updateDepartmentValidate.class}, message = "社区id不能为空")
	private Long communityId;
	
	@ApiModelProperty(value = "部门名称")
	@NotBlank(groups = {addDepartmentValidate.class, updateDepartmentValidate.class}, message = "部门名称不能为空")
	private String department;
	
	@ApiModelProperty(value = "部门图片")
	private String imgUrl;
	
	@ApiModelProperty(value = "部门电话")
	@Size(groups = {addDepartmentValidate.class, updateDepartmentValidate.class}, min = 0, max = 3, message = "最多添加三个部门电话")
	private List<String> phone;
	
	@ApiModelProperty(value = "排序序号")
	private int sort;
	
	public interface addDepartmentValidate {
	}
	
	public interface updateDepartmentValidate {
	}
}
