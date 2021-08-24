package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author DKS
 * @description
 * @since 2021/8/23  16:24
 **/
@Data
public class FinanceLogQO implements Serializable {
	@ApiModelProperty(value = "用户id")
	private Long userId;
	
	@ApiModelProperty(value = "用户名")
	private String userName;
	
	@ApiModelProperty(value = "社区ID")
	private Long communityId;
	
	@ApiModelProperty(value = "开始时间")
	private LocalDate startTime;
	
	@ApiModelProperty(value = "结束时间")
	private LocalDate endTime;
}
