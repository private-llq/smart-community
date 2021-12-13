package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: com.jsy.community
 * @description: 短信购买记录
 * @author: DKS
 * @create: 2021-09-02 09:05
 **/
@Data
@TableName("t_sms_purchase_record")
public class SmsPurchaseRecordEntity extends BaseEntity {
	@ApiModelProperty(value = "公司id")
	private Long companyId;
	
	@ApiModelProperty(value = "订单编号")
	private String orderNum;
	
	@ApiModelProperty(value = "支付订单号")
	private String transactionId;
	
	@ApiModelProperty(value = "商品")
	private Integer goods;
	
	@ApiModelProperty(value = "订单金额")
	private BigDecimal orderMoney;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@ApiModelProperty(value = "支付时间")
	private String payTime;
	
	@ApiModelProperty(value = "状态(1.已支付 已收货)")
	private Integer status;
	
	@ApiModelProperty(value = "支付uid")
	private String payBy;
	
	@ApiModelProperty(value = "支付方式(1.微信 2.支付宝 3.其他)")
	private Integer payType;
	
	@ApiModelProperty(value = "支付账号")
	@TableField(exist = false)
	private String payByPhone;
	
	@ApiModelProperty(value = "状态名称(1.已支付 已收货)")
	@TableField(exist = false)
	private String statusName;
}
