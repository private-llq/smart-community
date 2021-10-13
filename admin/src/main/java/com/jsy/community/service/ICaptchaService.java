package com.jsy.community.service;

/**
 * 验证码
 *
 * @author DKS
 * @since 2021-10-12 16:01
 */
public interface ICaptchaService {
	/**
	 * @Description: 发送验证码
	 * @author: DKS
	 * @since: 2021/10/12 16:01
	 * @Param: mobile,type
	 * @return: boolean
	 */
	boolean sendMobile(String mobile, Integer type);
}
