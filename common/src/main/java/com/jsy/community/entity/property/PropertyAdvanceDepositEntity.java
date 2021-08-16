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
 * @description 物业预存款余额表
 * @since 2021/8/11  15:53
 **/
@Data
@ApiModel("物业预存款余额表")
@TableName("t_property_advance_deposit")
public class PropertyAdvanceDepositEntity extends BaseEntity {
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "房屋id")
	private Long houseId;
	
	@ApiModelProperty(value = "充值余额")
	private BigDecimal balance;
	
	@ApiModelProperty(value = "充值余额明细")
	@TableField(exist = false)
	private BigDecimal balanceRecord;
	
	@ApiModelProperty(value = "备注")
	private String comment;
	
	@ApiModelProperty(value = "创建人")
	private String createBy;
	
	@ApiModelProperty(value = "修改人")
	private String updateBy;
	
	@ApiModelProperty(value = "手机号")
	@TableField(exist = false)
	private String mobile;
	
	@ApiModelProperty(value = "房屋地址")
	@TableField(exist = false)
	private String address;
	
	@ApiModelProperty(value = "真实姓名")
	@TableField(exist = false)
	private String realName;
	
	@ApiModelProperty(value = "房屋号码")
	@TableField(exist = false)
	private String door;
	
	@ApiModelProperty(value = "付款金额")
	@TableField(exist = false)
	private BigDecimal payAmount;
	
	@ApiModelProperty(value = "到账金额")
	@TableField(exist = false)
	private BigDecimal receivedAmount;
}
