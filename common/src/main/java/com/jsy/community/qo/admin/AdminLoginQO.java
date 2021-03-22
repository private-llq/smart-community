package com.jsy.community.qo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录表单
 */
@Data
public class AdminLoginQO {
	
	@ApiModelProperty(value = "账号")
	@NotBlank(message = "账号不能为空")
	private String account;
	
	@ApiModelProperty(value = "密码")
	@NotBlank(message = "账号不能为空")
	private String password;
	
	@ApiModelProperty(value = "验证码")
	private String captcha;
	
	
}
