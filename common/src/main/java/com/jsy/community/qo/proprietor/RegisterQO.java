package com.jsy.community.qo.proprietor;

import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

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
	@Length(min = 8, max = 30, message = "密码长度8-30")
	private String password;
}
