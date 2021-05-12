package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description 用户全平台券(现金券等) Entity
 * @since 2021-01-28 13:54
 **/
@Data
@TableName("t_user_ticket")
public class UserTicketEntity extends BaseEntity{
	
	@ApiModelProperty(value = "用户id")
	private String uid;
	
	@ApiModelProperty(value = "现金券标题")
	private String title;
	
	@ApiModelProperty(value = "描述")
	private String remark;
	
	@ApiModelProperty(value = "类型 1.全平台现金抵扣券")
	private Integer type;
	
	@ApiModelProperty(value = "面值")
	private BigDecimal money;
	
	@ApiModelProperty(value = "要求最低消费(门槛)")
	private BigDecimal leastConsume;
	
	@ApiModelProperty(value = "是否使用 0.未使用 1.已使用")
	private Integer status;
	
	@ApiModelProperty(value = "过期时间")
	private LocalDateTime expireTime;
	
}
