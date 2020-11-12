package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.qo.proprietor.LoginQO;

import java.util.List;

public interface IUserAuthService extends IService<UserAuthEntity> {
	List<UserAuthEntity> getList(boolean a);
	
	/**
	 * 通过指定字段查询业主认证信息
	 *
	 * @param qo 登录信息
	 * @return 业主ID
	 */
	Long checkUser(LoginQO qo);
}
