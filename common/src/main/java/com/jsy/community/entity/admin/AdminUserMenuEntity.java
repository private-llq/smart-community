package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author chq459799974
 * @description 用户-菜单 (新版物业端原型 一个小区用户量少，不设角色，直接关联菜单)
 * @since 2020-3-23 13:36
 **/
@Data
@TableName("t_admin_user_menu")
public class AdminUserMenuEntity extends BaseEntity {
	
	@NotNull(message = "缺少用户ID")
	private Long userId;//用户ID
	private List<Long> menuIds;//菜单ID
}
