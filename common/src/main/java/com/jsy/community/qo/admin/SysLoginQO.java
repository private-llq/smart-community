package com.jsy.community.qo.admin;

import lombok.Data;

/**
 * 登录表单
 */
@Data
public class SysLoginQO {
	private String username;
	private String password;
	private String captcha;
	private String uuid;
	
	
}
