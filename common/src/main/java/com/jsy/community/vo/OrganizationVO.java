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
 * @ClassName OrganizationVO
 * @Date 2021/3/15  18:00
 * @Description 组织机构树形结构展示
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrganizationVO对象", description = "组织机构树形结构展示效果")
public class OrganizationVO implements Serializable {
	
	@ApiModelProperty(value = "组织id")
	private Long id;
	
	@ApiModelProperty(value = "父id")
	private Long pid;
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "组织名称")
	private String name;
	
	@ApiModelProperty(value = "排序序号 0-99")
	private Integer sort;
	
	@ApiModelProperty(value = "是否为维修部 0 否 1 是")
	private Integer isRepair;
	
	@ApiModelProperty(value = "子组织")
	private List<OrganizationVO> children;
}
