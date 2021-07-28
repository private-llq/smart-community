package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author chq459799974
 * @description 角色-菜单
 * @since 2020-12-14 18:20
 **/
@Data
@TableName("t_admin_role_menu")
public class AdminRoleMenuEntity extends BaseEntity {
	
	@NotNull(message = "缺少角色ID")
	private Long roleId;//角色ID
	private List<Long> menuIds;//菜单ID
}
