package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_user_auth")
public class UserAuthEntity extends BaseEntity {
	public static final String CODE_TYPE_NOTE = "验证码类型，1注册，2登录，3忘记密码";
	
	/**
	 * 注册验证码
	 */
	public static final int CODE_TYPE_REGISTER = 1;
	
	/**
	 * 登录验证码
	 */
	public static final int CODE_TYPE_LOGIN = 2;
	
	/**
	 * 忘记密码
	 */
	public static final int CODE_TYPE_FORGET_PWD = 3;
	
	private Long uid;
	private String username;
	private String email;
	private String mobile;
	private String password;
	private String salt;
}
