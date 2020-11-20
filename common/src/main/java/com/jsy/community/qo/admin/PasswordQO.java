package com.jsy.community.qo.admin;

import lombok.Data;

/**
 * 密码表单
 */
@Data
public class PasswordQO {
	/**
	 * 原密码
	 */
	private String password;
	/**
	 * 新密码
	 */
	private String newPassword;
	
}
