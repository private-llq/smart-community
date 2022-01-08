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
 * @Description
 * @Version 1.0
 **/
@Data
@ApiModel("app报修详情")
public class RepairVO implements Serializable {
	
	@ApiModelProperty(value = "报修状态 0 待处理 1 处理中 2 已处理 3 未通过审核")
	private Integer status;
	
	@ApiModelProperty(value = "图片地址")
	private String repairImg;
	
	@ApiModelProperty(value = "报修类别id")
	private Long type;
	
	@ApiModelProperty(value = "报修类别名称")
	private String typeName;
	
	@ApiModelProperty(value = "报修标题")
	private String title;
	
	@ApiModelProperty(value = "报修人姓名")
	private String name;
	
	@ApiModelProperty(value = "联系电话")
	private String phone;
	
	@ApiModelProperty(value = "报修地址")
	private String address;
	
	@ApiModelProperty(value = "订单编号")
	private String number;
	
	@ApiModelProperty(value = "下单时间")
	private Date orderTime;
	
	@ApiModelProperty(value = "订单备注")
	private String problem;
	
}
