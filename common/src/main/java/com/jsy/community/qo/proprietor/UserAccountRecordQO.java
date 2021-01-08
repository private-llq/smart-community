package com.jsy.community.qo.proprietor;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author chq459799974
 * @description 用户账户流水接参
 * @since 2021-01-08 10:59
 **/
@ApiModel("用户账户流水接参")
@Data
public class UserAccountRecordQO extends BaseEntity{
	
	@ApiModelProperty(value = "用户ID",hidden = true)
	private String uid;
	
	@ApiModelProperty(value = "交易来源1.充值提现2.商城购物3.水电缴费4.物业管理5.房屋租金")
	@Range(min = 1,max = 5, message = "非法交易来源")
	@NotNull(message = "请确定交易来源")
	private Integer tradeFrom;
	
	@ApiModelProperty(value = "交易类型1.收入2.支出")
	@Range(min = 1,max = 2, message = "非法交易类型")
	@NotNull(message = "请确定交易类型")
	private Integer tradeType;
	
	@ApiModelProperty(value = "交易金额")
	@NotNull(message = "请确定交易金额")
	@Range(min = 0,message = "交易金额错误")
	private BigDecimal tradeAmount;
	
	@ApiModelProperty(value = "交易后余额",hidden = true)
	private BigDecimal balance;
	
	@ApiModelProperty(value = "商品id")
	private Long goodsId;

}
