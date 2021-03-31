package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
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
	
}
