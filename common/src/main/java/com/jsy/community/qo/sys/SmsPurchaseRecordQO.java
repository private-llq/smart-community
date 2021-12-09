package com.jsy.community.qo.sys;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 短信购买记录
 * @author: DKS
 * @since: 2021/12/9 15:47
 */
@Data
public class SmsPurchaseRecordQO implements Serializable {
	
	@ApiModelProperty(value = "状态（1.已付款 2.未付款）")
	private Integer status;
	
	@ApiModelProperty(value = "关键字")
	private String keyword;
}
