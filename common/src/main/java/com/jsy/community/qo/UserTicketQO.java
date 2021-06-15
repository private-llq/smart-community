package com.jsy.community.qo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description 用户全平台券(现金券等) QO
 * @since 2021-01-28 13:54
 **/
@Data
public class UserTicketQO extends BaseQO {
	
	@ApiModelProperty(value = "券id")
	private Long ticketId;
	
	@ApiModelProperty(value = "用户id")
	private String uid;
	
	@ApiModelProperty(value = "类型 1.全平台现金抵扣券")
	private Integer type;
	
	@ApiModelProperty(value = "是否使用 0.未使用 1.已使用")
	private Integer status;
	
	@ApiModelProperty(value = "是否使用 0.未过期 1.已过期")
	private Integer expired;
	
	public static final Integer TICKET_UNUSED = 0; //未使用
	public static final Integer TICKET_USED = 1; //已使用
	
	public static final Integer TICKET_UNEXPIRED = 0; //未过期
	public static final Integer TICKET_EXPIRED = 1; //已过期
}
