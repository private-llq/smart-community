package com.jsy.community.api;

/**
 * @author chq459799974
 * @description 租房用户相关Service
 * @since 2021-04-21 13:23
 **/
public interface ILeaseUserService {
	
	/**
	* @Description: 用户uid查imID
	 * @Param: [uid]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/21
	**/
	String queryIMIdByUid(String uid);
}
