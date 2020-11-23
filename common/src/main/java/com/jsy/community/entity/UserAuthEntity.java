package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_user_auth")
@ApiModel("业主认证表")
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
	
	@ApiModelProperty("业主ID")
	private Long uid;
	
	@ApiModelProperty("用户名")
	private String username;
	
	@ApiModelProperty("邮箱地址")
	private String email;
	
	@ApiModelProperty("手机号")
	private String mobile;
	
	@ApiModelProperty("密码")
	private String password;
	
	@ApiModelProperty("密码")
	private String salt;
}
