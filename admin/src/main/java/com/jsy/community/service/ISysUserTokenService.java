package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.admin.SysUserEntity;
import com.jsy.community.entity.admin.SysUserTokenEntity;


/**
 * 用户Token
 */
public interface ISysUserTokenService extends IService<SysUserTokenEntity> {
	
	/**
	 * 生成token
	 *
	 * @param user 用户信息
	 */
	String createToken(SysUserEntity user);
	
}
