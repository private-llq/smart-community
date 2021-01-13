package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chq459799974
 * @description 用户账户流水实体
 * @since 2021-01-08 10:59
 **/
@ApiModel("用户账户流水实体类")
@Data
@TableName("t_user_account_record")
public class UserAccountRecordEntity  extends BaseEntity{
	
	@ApiModelProperty(value = "用户ID")
	private String uid;
	
	@ApiModelProperty(value = "交易来源1.充值提现2.商城购物3.水电缴费4.物业管理")
	private Integer tradeFrom;
	
	@ApiModelProperty(value = "交易类型1.收入2.支出")
	private Integer tradeType;
	
	@ApiModelProperty(value = "交易金额")
	private BigDecimal tradeAmount;
	
	@ApiModelProperty(value = "交易后余额")
	private BigDecimal balance;
	
	@ApiModelProperty(value = "商品id")
	private Long goodsId;

}