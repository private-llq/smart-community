package com.jsy.community.qo.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chq459799974
 * @description 系统角色
 * @since 2020-12-14 15:41
 **/
@Data
public class AdminRoleQO implements Serializable {
	
	@ApiModelProperty(value = "ID")
	private Long id;
	
	@ApiModelProperty(value = "社区ID")
	private Long communityId;
	
	@ApiModelProperty(value = "角色名")
	private String name;
	
	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "菜单ID列表")
	private List<Long> menuIds;
}
