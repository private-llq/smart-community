package com.jsy.community.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.jsy.community.api.IAlipayService;
import com.jsy.community.constant.Const;
import com.jsy.community.utils.AlipayUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * @author chq459799974
 * @description 支付宝相关服务实现类
 * @since 2021-01-12 16:57
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class AlipayServiceImpl implements IAlipayService {
	
	@Autowired
	private AlipayUtils alipayUtils;
	
	/**
	* @Description: 换取accessToken
	 * @Param: [authCode]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/1/12
	**/
	@Override
	public String getAccessToken(String authCode){
		if(StringUtils.isEmpty(authCode)){
			return null;
		}
		AlipaySystemOauthTokenRequest alipaySystemOauthTokenRequest = new AlipaySystemOauthTokenRequest(); //创建API对应的request类
		alipaySystemOauthTokenRequest.setGrantType("authorization_code");
		alipaySystemOauthTokenRequest.setCode(authCode);
		AlipaySystemOauthTokenResponse response = null;
		try {
			response = alipayUtils.getDefaultCertClient().certificateExecute(alipaySystemOauthTokenRequest);
		} catch (Exception e) {
			log.error("支付宝 - 换取accessToken调用异常");
			e.printStackTrace();
		}
		if(response.isSuccess()){
//			System.out.print(response.getBody());
			return response.getAccessToken();
		}
		log.error("支付宝 - 换取accessToken失败");
		return null;
	}
	
	/**
	* @Description: 获取会员id
	 * @Param: [accessToken]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/1/12
	**/
	@Override
	public String getUserid(String accessToken){
		if(StringUtils.isEmpty(accessToken)){
			return null;
		}
		AlipayUserInfoShareResponse userInfoResponse = null;
		try {
			userInfoResponse = alipayUtils.getDefaultCertClient().certificateExecute(AlipayUtils.alipayUserInfoShareRequest,accessToken);
		} catch (Exception e) {
			log.error("支付宝 - 获取会员信息调用异常");
			e.printStackTrace();
		}
		if (userInfoResponse.isSuccess()){
//			System.out.println(userInfoResponse.getBody());
			return userInfoResponse.getUserId();
		}
		log.error("支付宝 - 获取会员信息失败");
		return null;
	}
	
}
