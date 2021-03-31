package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 登录
 *
 * @author ling
 * @since 2020-11-12 15:57
 */
@Data
@ApiModel("登录表单")
public class LoginQO implements Serializable {
	@ApiModelProperty("登录的账号，手机号或账号或邮箱地址")
	@NotEmpty(message = "账号不能为空")
	private String account;
	
	@ApiModelProperty("验证码")
	private String code;
	
	@ApiModelProperty("密码")
	private String password;
	
	@ApiModelProperty("离线推送id")
	@NotEmpty(message = "离线推送id不能为空")
	private String regId;
}
