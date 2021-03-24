package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

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
@ApiModel(value = "DepartmentVO对象", description = "通讯录部门树形结构展示效果")
public class DepartmentVO extends BaseVO {
	
	@ApiModelProperty(value = "父id")
	private Long pid;
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "部门名称")
	private String department;
	
	@ApiModelProperty(value = "部门电话")
	private String phone;
	
	@ApiModelProperty(value = "排序序号")
	private int sort;
	
	@ApiModelProperty(value = "部门人数")
	private int count;
	
	@ApiModelProperty(value = "子菜单")
	private List<DepartmentVO> children;
	
}
