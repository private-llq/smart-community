package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author DKS
 * @description
 * @since 2021/8/23  11:49
 **/
@Data
public class OpLogQO implements Serializable {
	@ApiModelProperty(value = "用户id")
	private Long userId;
	
	@ApiModelProperty(value = "用户名")
	private String userName;
	
	@ApiModelProperty(value = "社区ID")
	private Long communityId;
	
	@ApiModelProperty(value = "操作")
	private String operation;
}
