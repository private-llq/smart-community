package com.jsy.community.qo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ling
 * @since 2020-11-16 17:05
 */
@Data
@ApiModel("三方登录")
public class ThirdPlatformQo implements Serializable {
	private String code;
	private String authCode;
	private String state;
	private String authorizationCode;
}
