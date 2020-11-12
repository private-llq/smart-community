package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.vo.UserInfoVo;

/**
 * 业主接口
 *
 * @author ling
 * @date 2020-11-11 17:41
 */
public interface IUserService extends IService<UserEntity> {
	
	/**
	 * 登录接口
	 *
	 * @param qo 参数
	 * @return 登录结果
	 */
	UserInfoVo login(LoginQO qo);
}
