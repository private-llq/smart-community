package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description: 支付配置
 * @author: DKS
 * @create: 2021-09-09 09:39
 **/
@Data
@TableName("t_pay_configure")
public class PayConfigureEntity extends BaseEntity {
	@ApiModelProperty(value = "物业id")
	private Long companyId;
	
	@ApiModelProperty(value = "支付参数")
	private Integer payParam;
	
	@ApiModelProperty(value = "身份标识")
	private String identify;
	
	@ApiModelProperty(value = "身份秘钥")
	private String identityKey;
	
	@ApiModelProperty(value = "微信支付商户号")
	private String wechatMerchant;
	
	@ApiModelProperty(value = "微信支付秘钥")
	private String wechatKey;
	
	@ApiModelProperty(value = "微信退款证书名")
	private String wechatRefund;
	
	@ApiModelProperty(value = "收款支付宝账号")
	private String alipayAccount;
	
	@ApiModelProperty(value = "合作者身份")
	private String partnerIdentity;
	
	@ApiModelProperty(value = "支付宝秘钥")
	private String alipayKey;
	
	@ApiModelProperty(value = "支付宝appid")
	private String alipayAppid;
	
	@ApiModelProperty(value = "支付宝退款证书名")
	private String alipayRefund;
}
