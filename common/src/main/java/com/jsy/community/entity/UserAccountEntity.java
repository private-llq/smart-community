package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chq459799974
 * @description 用户账户实体
 * @since 2021-01-08 10:59
 **/
@ApiModel("用户账户实体类")
@Data
@TableName("t_user_account")
public class UserAccountEntity extends BaseEntity{
	
	@TableField(exist = false)
	private Integer deleted;
	
	@TableField(exist = false)
	private Long id;
	
	@ApiModelProperty(value = "用户ID")
	private String uid;
	
	@ApiModelProperty(value = "账户余额")
	private BigDecimal balance;
	
}
