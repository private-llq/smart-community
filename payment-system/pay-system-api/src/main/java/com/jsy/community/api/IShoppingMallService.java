package com.jsy.community.api;

import java.util.Map;

/**
 * @author chq459799974
 * @description 社区商城相关业务
 * @since 2021-04-02 16:48
 **/
public interface IShoppingMallService {
	/**
	* @Description: 商城订单校验
	 * @Param: [orderData, token]
	 * @Return: java.util.Map<java.lang.String,java.lang.Object>
	 * @Author: chq459799974
	 * @Date: 2021/4/2
	**/
	Map<String,Object> validateShopOrder(Map<String,Object> orderData, String token);
	
	/**
	* @Description: 修改商城订单状态为完成
	 * @Param: [orderNo]
	 * @Return: java.util.Map<java.lang.String,java.lang.Object>
	 * @Author: chq459799974
	 * @Date: 2021/4/8
	**/
	Map<String,Object> completeShopOrder(String outTradeNo,String transactionId,Integer payType);
}
