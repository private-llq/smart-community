package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author DKS
 * @description 大后台操作日志表
 * @since 2021/10/20  10:57
 **/
@Data
@ApiModel("大后台操作日志表")
@TableName("t_sys_op_log")
public class SysOpLogEntity extends BaseEntity {
	@ApiModelProperty(value = "用户id")
	private String userId;
	
	@ApiModelProperty(value = "用户名")
	@TableField(exist = false)
	private String userName;
	
	@ApiModelProperty(value = "操作")
	private String operation;
	
	@ApiModelProperty(value = "内容")
	private String content;
	
	@ApiModelProperty(value = "请求方法")
	private String method;
	
	@ApiModelProperty(value = "请求参数")
	private String params;
	
	@ApiModelProperty(value = "ip地址")
	private String ip;
	
	@ApiModelProperty(value = "创建人")
	private String createBy;
	
	@ApiModelProperty(value = "修改人")
	private String updateBy;
}
