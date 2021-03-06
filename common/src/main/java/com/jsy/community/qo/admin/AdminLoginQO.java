package com.jsy.community.qo.admin;

import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 登录表单
 */
@Data
public class AdminLoginQO {
//	@ApiModelProperty(value = "登录社区ID")
//	@NotNull(message = "请选择登录社区")
//	private Long communityId;
	
	@ApiModelProperty(value = "账号")
	@NotBlank(message = "账号不能为空")
	@Pattern(regexp = RegexUtils.REGEX_MOBILE, message = "非法手机号")
	private String account;
	
	@ApiModelProperty(value = "密码")
	private String password;
	
	@ApiModelProperty(value = "验证码")
	private String captcha;
	
	@ApiModelProperty("手机验证码")
	private String code;
}
