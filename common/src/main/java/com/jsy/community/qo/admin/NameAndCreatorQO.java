package com.jsy.community.qo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 名称创建者通用查询QO
 *
 * @author ling
 * @since 2020-11-20 14:44
 */
@Data
@ApiModel("名称创建者通用查询QO")
public class NameAndCreatorQO implements Serializable {
	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("创建者ID")
	private Long createUserId;
}
