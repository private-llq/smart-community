package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author chq459799974
 * @description 三方登录表实体类
 * @since 2021-01-11 16:04
 **/
@Data
@TableName("t_user_third_platform")
public class UserThirdPlatformEntity extends BaseEntity{
	
	@ApiModelProperty(value = "用户ID",hidden = true)
	private String uid;
	
	@ApiModelProperty(value = "三方平台唯一id")
	private String thirdPlatformId;
	
	@ApiModelProperty(value = "三方平台类型 1.支付宝 2.微信 3.QQ")
	@NotNull(message = "缺少三方平台类型")
	private Integer thirdPlatformType;

	@ApiModelProperty(value = "账户真实姓名")
	private String realname;

}
