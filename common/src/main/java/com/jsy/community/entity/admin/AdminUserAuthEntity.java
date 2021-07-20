package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

/**
 * @author chq459799974
 * @description 物业端用户登录账户
 * @since 2021-03-24 17:37
 **/
@Data
@TableName("t_admin_user_auth")
public class AdminUserAuthEntity extends BaseEntity {
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
	private String communityIds;
	
	private String token;//登录token
	
	private String createBy;//创建人
	private String updateBy;//更新人
}
