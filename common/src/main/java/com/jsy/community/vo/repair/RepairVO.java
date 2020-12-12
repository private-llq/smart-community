package com.jsy.community.vo.repair;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lihao
 * @ClassName RepairVO
 * @Date 2020/12/8  15:05
 * @Description TODO
 * @Version 1.0
 **/
@Data
@ApiModel("报修详情")
public class RepairVO implements Serializable {
	
	@ApiModelProperty(value = "报修状态 0 待处理 1 处理中 2 已处理 3 未通过审核")
	private Integer status;
	
	@ApiModelProperty(value = "图片地址")
	private String repairImg;
	
	@ApiModelProperty(value = "报修类别 0 抹灰 1 防水 2 墙面 3 门窗 4 排水")
	private Integer type;
	
	@ApiModelProperty(value = "报修人姓名")
	private String name;
	
	@ApiModelProperty(value = "联系电话")
	private String phone;
	
	@ApiModelProperty(value = "报修地址id")
	private Long userHouseId;
	
	@ApiModelProperty(value = "订单编号")
	private String number;
	
	@ApiModelProperty(value = "下单时间")
	private Date orderTime;
	
	@ApiModelProperty(value = "订单备注")
	private String problem;
	
}