package com.jsy.community.qo.lease;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
* @Description: 支付宝接参
 * @Author: chq459799974
 * @Date: 2021/1/8
**/
@Data
public class AliAppPayQO implements Serializable {
	
	@ApiModelProperty(value = "商户订单号")
	private String outTradeNo;
	
	@ApiModelProperty(value = "条目")
	private String subject;
	
	@ApiModelProperty(value = "交易金额(RMB)")
	private BigDecimal totalAmount;
	
	@ApiModelProperty(value = "支付类型 1.APP 2.H5")
	private int payType;
	
	@ApiModelProperty(value = "交易来源 1.充值提现2.商城购物3.水电缴费4.物业管理5.房屋租金6.红包")
	private Integer tradeFrom;
	
	@ApiModelProperty(value = "订单详情")
	private Map<String,Object> orderData;
}
