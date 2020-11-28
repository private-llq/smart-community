package com.jsy.community.api;

import com.jsy.community.entity.UserLoginLogEntity;

/**
 * @author chq459799974
 * @description APP用户日志
 * @since 2020-11-28 16:43
 **/
public interface IUserLogService {
	
	/**
	* @Description: 新增用户登录日志
	 * @Param: [userLoginLogEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	**/
	boolean addUserLoginLog(UserLoginLogEntity userLoginLogEntity);

}
