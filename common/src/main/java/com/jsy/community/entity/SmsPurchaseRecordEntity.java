package com.jsy.community.entity;

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
}
