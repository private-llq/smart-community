package com.jsy.community.qo.proprietor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lihao
 * @ClassName RepairCommentQO
 * @Date 2020/12/26  9:18
 * @Description
 * @Version 1.0
 **/
@Data
@ApiModel("报修评价")
public class RepairCommentQO implements Serializable {
	@ApiModelProperty(value = "订单id")
	private Long id;
	
	@ApiModelProperty(value = "业主id")
	@JsonIgnore
	private String uid;
	
	@ApiModelProperty(value = "用户评价")
	private String appraise;
	
	@ApiModelProperty(value = "评价类型")
	private Integer status;
	
	@ApiModelProperty(value = "图片地址")
	private String filePath;
}
