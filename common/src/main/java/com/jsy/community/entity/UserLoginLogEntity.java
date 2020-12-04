package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description APP用户登录日志
 * @since 2020-11-28 09:20
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_login_log")
@ApiModel(value="UserLog对象", description="APP用户登录日志")
public class UserLoginLogEntity implements Serializable {
	
	@ApiModelProperty("业主ID")
	private String uid;
	
	@ApiModelProperty("系统类型 1.安卓 2.IOS 3.其他")
	private Integer st;
	
	@ApiModelProperty("系统版本号")
	private String sv;
	
	@ApiModelProperty("手机品牌")
	private String brand;
	
	@ApiModelProperty("手机型号")
	private String model;
	
	@ApiModelProperty("IP地址")
	private String ip;
	
	@ApiModelProperty("操作类型 1.登录2.登出")
	private Integer op_type;
	
	@ApiModelProperty("客户端版本号 兼容旧版客户端用户")
	private String cv;
	
	@ApiModelProperty("屏幕分辨率宽度")
	private String px;
	
	@ApiModelProperty("屏幕分辨率高度")
	private String py;
	
	@ApiModelProperty("登录时经度")
	private Double lon;
	
	@ApiModelProperty("登录时纬度")
	private Double lat;
	
	@ApiModelProperty("创建时间")
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;
}
