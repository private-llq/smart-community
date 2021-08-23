package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author DKS
 * @description 物业押金表
 * @since 2021/8/10  16:37
 **/
@Data
@ApiModel("物业押金表")
@TableName("t_property_deposit")
public class PropertyDepositEntity extends BaseEntity {
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "关联类型1房屋，2车位")
	private Long depositType;
	
	@ApiModelProperty(value = "关联类型名称")
	@TableField(exist = false)
	private String depositTypeName;
	
	@ApiModelProperty(value = "关联目标1房屋id2车位id")
	private Long depositTargetId;
	
	@ApiModelProperty(value = "关联目标名称")
	@TableField(exist = false)
	private String depositTargetIdName;
	
	@ApiModelProperty(value = "收费项目")
	private String payService;
	
	@ApiModelProperty(value = "开始时间")
	private LocalDate startTime;
	
	@ApiModelProperty(value = "结束时间")
	private LocalDate endTime;
	
	@ApiModelProperty(value = "账单金额")
	private BigDecimal billMoney;
	
	@ApiModelProperty(value = "状态（1.待支付2.已支付3.已退回）")
	private Integer status;
	
	@ApiModelProperty(value = "状态名称")
	@TableField(exist = false)
	private String statusName;
	
	@ApiModelProperty(value = "账单抬头")
	private String billTitle;
	
	@ApiModelProperty(value = "创建人")
	private String createBy;
	
	@ApiModelProperty(value = "修改人")
	private String updateBy;
	
	@ApiModelProperty(value = "小区名")
	@TableField(exist = false)
	private String communityName;
	
	@ApiModelProperty(value = "押金凭证号")
	private String depositVoucher;
}
