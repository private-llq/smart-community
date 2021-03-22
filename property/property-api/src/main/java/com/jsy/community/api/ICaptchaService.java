package com.jsy.community.api;

/**
 * 验证码
 *
 * @author ling
 * @since 2020-11-12 10:38
 */
public interface ICaptchaService {
	/**
	 * 发送手机验证码
	 *
	 * @param mobile 手机号
	 * @param type   验证码类型
	 */
	boolean sendMobile(String mobile, Integer type);
}
