package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author chq459799974
 * @description 用户-角色
 * @since 2020-12-14 15:59
 **/
@Data
@TableName("t_sys_user_role")
public class AdminUserRoleEntity extends BaseEntity {
	
	@NotNull(message = "缺少用户ID")
	private Long userId;//用户ID
	private List<Long> roleIds;//角色ID
}
