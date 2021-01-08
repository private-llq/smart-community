package com.jsy.community.api;

import com.jsy.community.entity.UserAccountRecordEntity;

/**
 * @author chq459799974
 * @description 用户账户流水Service
 * @since 2021-01-08 11:13
 **/
public interface IUserAccountRecordService {
	
	/** 
	* @Description: 新增账户流水
	 * @Param: [userAccountRecordEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	boolean addUcoinRecord(UserAccountRecordEntity userAccountRecordEntity);
	
}
