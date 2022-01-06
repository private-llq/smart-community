package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 注册
 *
 * @author ling
 * @since 2020-11-13 11:38
 */
@Data
@ApiModel("注册表单")
public class RegisterQO implements Serializable {
	
	@ApiModelProperty("注册的账号，手机号或邮箱地址")
	@NotEmpty(message = "账号不能为空")
	private String account;
	
	@ApiModelProperty("验证码")
	@NotEmpty(message = "验证码不能为空")
	private String code;

	@ApiModelProperty("业主添加家属时要用到的字段")
	private String name;

	/**
	 * 密码
	 */
	@NotEmpty(message = "密码不能为空")
//	@Length(min = 8, max = 30, message = "密码长度8-30")
	@Pattern(regexp = "^(?=.*[A-Z0-9])(?=.*[a-z0-9])(?=.*[a-zA-Z])(.{6,12})$", message = "请输入一个正确的6-12位密码,至少包含大写字母或小写字母或数字两种!")
	private String password;
}
