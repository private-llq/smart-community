package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author DKS
 * @description 物业预存款余额明细记录表
 * @since 2021/8/12  13:53
 **/
@Data
@ApiModel("物业预存款余额明细记录表")
@TableName("t_property_advance_deposit_record")
public class PropertyAdvanceDepositRecordEntity extends BaseEntity {
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "1.预存款支付 2.预存款充值：后台充值")
	private Integer type;
	
	@ApiModelProperty(value = "类型名称：预存款支付、预存款充值：后台充值")
	@TableField(exist = false)
	private String typeName;
	
	@ApiModelProperty(value = "账单id")
	private Long orderId;
	
	@ApiModelProperty(value = "存入金额")
	private BigDecimal depositAmount;
	
	@ApiModelProperty(value = "支出金额")
	private BigDecimal payAmount;
	
	@ApiModelProperty(value = "余额明细")
	private BigDecimal balanceRecord;
	
	@ApiModelProperty(value = "预存款id")
	private Long advanceDepositId;
	
	@ApiModelProperty(value = "备注")
	private String comment;
	
	@ApiModelProperty(value = "创建人")
	private String createBy;
	
	@ApiModelProperty(value = "最近更新人")
	private String updateBy;
}
