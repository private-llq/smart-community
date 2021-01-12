package com.jsy.community.api;

/**
 * @author chq459799974
 * @description 支付宝相关服务类
 * @since 2021-01-12 17:15
 **/
public interface IAlipayService {
	
	/**
	* @Description: 换取accessToken
	 * @Param: [authCode]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/1/12
	**/
	String getAccessToken(String authCode);
	
	/**
	* @Description: 获取会员id
	 * @Param: [accessToken]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/1/12
	**/
	String getUserid(String accessToken);

}
