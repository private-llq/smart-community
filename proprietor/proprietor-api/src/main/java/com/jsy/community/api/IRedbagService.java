package com.jsy.community.api;

import com.jsy.community.qo.RedbagQO;

import java.util.Map;

/**
 * @author chq459799974
 * @description 红包Service
 * @since 2021-01-18 14:30
 **/
public interface IRedbagService {
	
	/**
	 * @Description: 发红包
	 * @Param: [redbagQO]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/1/18
	 **/
	void sendRedbag(RedbagQO redbagQO);
	
	/**
	* @Description: 收取红包
	 * @Param: [redbagQO]
	 * @Return: java.util.Map<java.lang.String,java.lang.Object>
	 * @Author: chq459799974
	 * @Date: 2021/1/18
	**/
	Map<String,Object> receiveRedbag(RedbagQO redbagQO);
	
	/**
	* @Description: 红包退回
	 * @Param: [uuid]
	 * @Return: java.util.Map<java.lang.String,java.lang.Object>
	 * @Author: chq459799974
	 * @Date: 2021/1/19
	**/
	Map<String,Object> sendBackRedbag(String uuid);
	
}
