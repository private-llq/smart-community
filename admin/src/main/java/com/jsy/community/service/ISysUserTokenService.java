package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.SysUserTokenEntity;
import com.jsy.community.vo.CommonResult;

/**
 * 用户Token
 */
public interface ISysUserTokenService extends IService<SysUserTokenEntity> {
	
	/**
	 * 生成token
	 *
	 * @param userId 用户ID
	 */
	CommonResult<Object> createToken(long userId);
	
	/**
	 * 退出，修改token值
	 *
	 * @param userId 用户ID
	 */
	void logout(long userId);
	
}
