package com.jsy.community.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author chq459799974
 * @description 三方登录QO
 * @since 2021-01-11 16:04
 **/
@Data
public class UserThirdPlatformQO implements Serializable {
	
	@ApiModelProperty(value = "三方平台唯一id")
	private String thirdPlatformId;
	
	@ApiModelProperty(value = "authCode")
	private String authCode;
	
	@ApiModelProperty(value = "三方平台类型 1.支付宝 2.微信 3.QQ")
	@NotNull(message = "缺少三方平台类型")
	@Range(min = 1, max = 3, message = "非法三方平台类型")
	private Integer thirdPlatformType;
	
	@ApiModelProperty(value = "手机号")
	private String mobile;
	
	@ApiModelProperty(value = "验证码")
	private String code;

}
