package com.jsy.community.qo.lease;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AliAppPayQO implements Serializable {
	
	@ApiModelProperty(value = "商户订单号")
	private String outTradeNo;
	
	@ApiModelProperty(value = "条目")
	private String subject;
	
	@ApiModelProperty(value = "交易金额(RMB)")
	private BigDecimal totalAmount;
}
