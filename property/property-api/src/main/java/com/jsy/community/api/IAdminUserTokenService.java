package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.entity.admin.AdminUserTokenEntity;


/**
 * 用户Token
 */
public interface IAdminUserTokenService extends IService<AdminUserTokenEntity> {
	
	/**
	 * 生成token
	 *
	 * @param user 用户信息
	 */
	String createToken(AdminUserEntity user);
	
}
