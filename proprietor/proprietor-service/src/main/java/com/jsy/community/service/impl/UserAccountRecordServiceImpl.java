package com.jsy.community.service.impl;

import com.jsy.community.api.IUserAccountRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserAccountRecordEntity;
import com.jsy.community.mapper.UserAccountRecordMapper;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author chq459799974
 * @description 用户账户流水实现类
 * @since 2021-01-08 11:16
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserAccountRecordServiceImpl implements IUserAccountRecordService {
	
	@Autowired
	private UserAccountRecordMapper userAccountRecordMapper;
	
	/**
	* @Description: 新增账户流水
	 * @Param: [userAccountRecordEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	@Override
	public boolean addAccountRecord(UserAccountRecordEntity userAccountRecordEntity){
		return userAccountRecordMapper.insert(userAccountRecordEntity) == 1;
	}
	
	//TODO 查询账户流水
	public PageInfo queryAccountRecord(){
		return null;
	}
	
}
