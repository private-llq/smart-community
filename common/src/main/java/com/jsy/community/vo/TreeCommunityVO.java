package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author lihao
 * @ClassName TreeCommunityVO
 * @Date 2021/3/16  9:51
 * @Description  该VO用于社区通讯录与组织机构
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TreeCommunityVO对象", description = "该VO用于社区通讯录与组织机构")
public class TreeCommunityVO implements Serializable {
	private Long communityId;
	
	private String communityName;
	
	// 社区总人数
	private Integer count;
	
	// 组织结构
	private List<OrganizationVO> organizationVOList;
	
	// 社区通讯录
	private List<DepartmentVO> departmentVOList;
}
