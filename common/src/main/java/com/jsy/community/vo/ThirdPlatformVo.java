package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 三方平台信息
 *
 * @author ling
 * @since 2020-11-16 16:10
 */
@Data
@AllArgsConstructor
@ApiModel("三方平台信息")
public class ThirdPlatformVo implements Serializable {
	@ApiModelProperty("平台名")
	private String name;
	
	@ApiModelProperty("重定向地址")
	private String redirectUrl;
}
