package com.jsy.community.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

/**
 * @author DKS
 * @description 大后台用户登录账户
 * @since 2021-10-12 16:21
 **/
@Data
@TableName("t_sys_user_auth")
public class SysUserAuthEntity extends BaseEntity {
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 盐
	 */
	private String salt;
	/**
	 * 有权限的社区ids
	 */
	@TableField(exist = false)
	private String communityIds;
	@TableField(exist = false)
	private String token;//登录token
	
	private String createBy;//创建人
	private String updateBy;//更新人
}
