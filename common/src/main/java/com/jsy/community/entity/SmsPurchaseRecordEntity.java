package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
	
	@ApiModelProperty(value = "商品")
	private String goods;
	
	@ApiModelProperty(value = "订单金额")
	private BigDecimal orderMoney;
	
	@ApiModelProperty(value = "状态(1.已支付 已收货)")
	private Integer status;
	
	@ApiModelProperty(value = "状态名称(1.已支付 已收货)")
	@TableField(exist = false)
	private String statusName;
}
