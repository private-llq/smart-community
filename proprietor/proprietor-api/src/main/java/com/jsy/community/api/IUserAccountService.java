package com.jsy.community.api;

import com.jsy.community.qo.RedbagQO;
import com.jsy.community.qo.proprietor.UserAccountTradeQO;
import com.jsy.community.vo.UserAccountVO;

/**
 * @author chq459799974
 * @description 用户账户Service
 * @since 2021-01-08 11:13
 **/
public interface IUserAccountService {
	
	/**
	* @Description: 创建用户账户
	 * @Param: [uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	boolean createUserAccount(String uid);
	
	/**
	* @Description: 查询余额
	 * @Param: [uid]
	 * @Return: com.jsy.community.vo.UserAccountVO
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	UserAccountVO queryBalance(String uid);
	
	/**
	* @Description: 账户交易
	 * @Param: [userid, uAccountRecordQO]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	void trade(UserAccountTradeQO uAccountRecordQO);
	
}
