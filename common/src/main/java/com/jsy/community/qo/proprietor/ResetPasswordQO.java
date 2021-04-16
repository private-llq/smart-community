package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 重置密码表单
 *
 * @author ling
 * @since 2020-11-13 17:11
 */
@Data
@ApiModel("重置密码表单")
public class ResetPasswordQO implements Serializable {
	@ApiModelProperty("重置密码的账号")
	@NotEmpty(groups = forgetPassVGroup.class, message = "账号不能为空")
	private String account;
	
	@ApiModelProperty("密码")
	@NotEmpty(groups = {forgetPassVGroup.class,updatePassVGroup.class}, message = "密码不能为空")
	@Length(groups = {forgetPassVGroup.class,updatePassVGroup.class}, min = 8, max = 30, message = "密码长度8-30")
	private String password;
	
	@ApiModelProperty("确认密码")
	@NotEmpty(groups = {forgetPassVGroup.class,updatePassVGroup.class}, message = "确认密码不能为空")
	@Length(groups = {forgetPassVGroup.class,updatePassVGroup.class}, min = 8, max = 30, message = "确认密码长度8-30")
	private String confirmPassword;
	
	/**
	 * 忘记密码验证
	 */
	public interface forgetPassVGroup{}
	
	/**
	 * 修改密码验证
	 */
	public interface updatePassVGroup{}
}
