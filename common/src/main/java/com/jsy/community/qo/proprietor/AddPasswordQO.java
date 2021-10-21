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
	@NotEmpty(groups = passwordVGroup.class,message = "密码不能为空")
	@Length(groups = passwordVGroup.class,min = 8, max = 30, message = "密码长度8-30")
	private String password;
	
	@ApiModelProperty("确认密码")
	@NotEmpty(groups = passwordVGroup.class,message = "确认密码不能为空")
	@Length(groups = passwordVGroup.class,min = 8, max = 30, message = "确认密码长度8-30")
	private String confirmPassword;
	
	@ApiModelProperty("支付密码")
	@NotEmpty(groups = payPasswordVGroup.class,message = "支付密码不能为空")
	@Length(groups = payPasswordVGroup.class,min = 6, max = 6, message = "支付密码长度为6位")
	private String payPassword;
	
	@ApiModelProperty("确认支付密码")
	@NotEmpty(groups = payPasswordVGroup.class,message = "确认支付密码不能为空")
	@Length(groups = payPasswordVGroup.class,min = 6, max = 6, message = "确认支付密码长度为6位")
	private String confirmPayPassword;

	@ApiModelProperty("原支付密码")
	@Length(groups = payPasswordVGroup.class,min = 6, max = 6, message = "原支付密码长度为6位")
	private String oldPayPassword;
	/**
	 * 密码操作
	 */
	public interface passwordVGroup{}
	
	/**
	 * 支付密码操作
	 */
	public interface payPasswordVGroup{}
}
