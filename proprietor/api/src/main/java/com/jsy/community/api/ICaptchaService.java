package com.jsy.community.api;

/**
 * 验证码
 *
 * @author ling
 * @date 2020-11-12 10:38
 */
public interface ICaptchaService {
	boolean sendMobile(String mobile);
	
	boolean sendEmail(String email);
}
