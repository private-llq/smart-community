package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 添加密码表单
 *
 * @author ling
 * @since 2020-11-13 11:46
 */
@Data
@ApiModel("添加密码表单")
public class AddPasswordQO implements Serializable {
	@ApiModelProperty("密码")
	@NotEmpty(message = "密码不能为空")
	@Length(min = 8, max = 30, message = "密码长度8-30")
	private String password;
	
	@ApiModelProperty("确认密码")
	@NotEmpty(message = "确认密码不能为空")
	@Length(min = 8, max = 30, message = "确认密码长度8-30")
	private String confirmPassword;
}
