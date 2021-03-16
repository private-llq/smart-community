package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
	
	@ApiModelProperty(value = "父id")
	private Long pid;
	
	@TableField(exist = false)
	@ApiModelProperty(value = "数据库没有该字段")
	private String parentName;
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "部门名称")
	private String department;
	
	@ApiModelProperty(value = "部门图片")
	private String imgUrl;
	
	@ApiModelProperty(value = "部门电话")
	private String phone;
	
	@ApiModelProperty(value = "排序序号")
	private int sort;
	
}
