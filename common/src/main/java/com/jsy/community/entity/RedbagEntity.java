package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chq459799974
 * @description 红包(单发/转账、群发)
 * @since 2021-01-18 11:15
 **/
@Data
@TableName("t_redbag")
public class RedbagEntity extends BaseEntity{
	
	@TableField(exist = false)
	private Long deleted;
	
	@ApiModelProperty(value = "发送人ID")
	private String userUuid;
	
	@ApiModelProperty(value = "接收人ID")
	private String receiveUserUuid;
	
	@ApiModelProperty(value = "红包UUID", hidden = true)
	private String uuid;
	
	@ApiModelProperty(value = "红包名称")
	private String name;
	
	@ApiModelProperty(value = "币种")
	private Integer type;
	
	@ApiModelProperty(value = "总金额")
	private BigDecimal money;
	
	//===================================群发增加参数==============================================
	@ApiModelProperty(value = "红包个数")
	private Integer number;
	
	@ApiModelProperty(value = "已领取人数")
	private Integer receivedCount;
	
	@ApiModelProperty(value = "群id")
	private String groupUuid;
	
	@ApiModelProperty(value = "交易类型 1.红包 2.群红包 3.转账")
	private Integer businessType;
	
	@ApiModelProperty(value = "红包状态 0.未领取 1.领取中 2.已领完 -1.已退回")
	private Integer status;
	
	@ApiModelProperty(value = "来源类型 1.个人 2.官方")
	private Integer fromType;
	
}
