package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author chq459799974
 * @description 用户账户VO
 * @since 2021-01-08 10:59
 **/
@ApiModel("用户账户VO")
@Data
public class UserAccountVO implements Serializable {
	
	@ApiModelProperty(value = "用户ID")
	private String uid;
	
	@ApiModelProperty(value = "账户余额")
	private BigDecimal balance;
	
}
