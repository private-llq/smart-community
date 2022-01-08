package com.jsy.community.api;

import com.jsy.community.entity.lease.AiliAppPayRecordEntity;

import java.util.Map;

/**
 * @author chq459799974
 * @description 支付宝回调
 * @since 2021-01-06 14:33
 **/
public interface AliAppPayCallbackService {
	
	/**
	* @Description: 回调验签/订单处理
	 * @Param: [paramsMap]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/8
	**/
	String dealCallBack(Map<String, String> paramsMap);
	
	/**
	* @Description: 回调订单处理
	 * @Param: [order]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/4/8
	**/
	Boolean dealOrder(AiliAppPayRecordEntity order);
	
}
