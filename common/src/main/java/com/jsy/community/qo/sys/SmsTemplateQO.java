package com.jsy.community.qo.sys;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 短信模板
 * @author: DKS
 * @since: 2021/12/8 11:44
 */
@Data
public class SmsTemplateQO implements Serializable {
	@ApiModelProperty(value = "短信分类id")
	private Long smsTypeId;
	
	@ApiModelProperty(value = "状态（1.启用 2.禁用）")
	private Integer status;
	
	@ApiModelProperty(value = "关键字")
	private String keyword;
}
