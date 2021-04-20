package com.jsy.community.vo.repair;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author lihao
 * @ClassName RepairPlanVO
 * @Date 2021/4/19  9:33
 * @Description 报修进程
 * @Version 1.0
 **/
@Data
@ApiModel("物业端报修进程展示")
public class RepairPlanVO implements Serializable {
	
	@ApiModelProperty(value = "报修时间")
	private LocalDateTime repairTime;
	
	@ApiModelProperty(value = "报修人")
	private String repairName;
	
	@ApiModelProperty(value = "报修人联系电话")
	private String repairPhone;
	
	@ApiModelProperty(value = "报修内容")
	private String problem;
	
	//==========================//
	
	@ApiModelProperty(value = "派单时间")
	private Date dealTime;
	
	@ApiModelProperty(value = "维修人")
	private String dealName;
	
	@ApiModelProperty(value = "维修人联系电话")
	private String dealPhone;
	
	//==========================//
	
	@ApiModelProperty(value = "完成时间")
	private Date successTime;
	
	//==========================//
	
	@ApiModelProperty(value = "评价时间")
	private Date commentTime;
	
	@ApiModelProperty(value = "评价类型[0 好评  1 差评]")
	private Integer commentStatus;
	
	@ApiModelProperty(value = "评价描述")
	private String comment;
	

}
