package com.jsy.community.service.impl;

import com.jsy.community.api.IUserLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLoginLogEntity;
import com.jsy.community.mapper.UserLoginLogMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author chq459799974
 * @description APP用户日志
 * @since 2020-11-28 16:44
 **/
@DubboService(version = Const.version, group = Const.group)
public class UserLogServiceImpl implements IUserLogService {
	
	@Autowired
	private UserLoginLogMapper userLoginLogMapper;
	
	/**
	 * @Description: 新增用户登录日志
	 * @Param: [userLoginLogEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	 **/
	@Override
	public boolean addUserLoginLog(UserLoginLogEntity userLoginLogEntity){
		int result = userLoginLogMapper.insert(userLoginLogEntity);
		if(result == 1){
			return true;
		}
		return false;
	}
	
}
