package com.jsy.community.api;

/**
 * 公共的
 *
 * @author ling
 * @since 2020-11-13 14:58
 */
public interface ICommonService {
	/**
	 * 校验验证码，失败抛异常
	 *
	 * @param account 账号
	 * @param code    验证码
	 */
	void checkVerifyCode(String account, String code);
}
