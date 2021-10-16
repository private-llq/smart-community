package com.jsy.community.api;

import com.jsy.community.entity.UserAccountRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.UserAccountRecordQO;
import com.jsy.community.utils.PageInfo;

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
	boolean addAccountRecord(UserAccountRecordEntity userAccountRecordEntity);
	
	/**
	* @Description: 查询账户流水
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo
	 * @Author: chq459799974
	 * @Date: 2021/2/7
	**/
	PageInfo<UserAccountRecordEntity> queryAccountRecord(BaseQO<UserAccountRecordQO> baseQO);
	
}
