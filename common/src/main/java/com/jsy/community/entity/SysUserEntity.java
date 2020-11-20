package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 系统用户
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_sys_user")
public class SysUserEntity extends BaseEntity {
	
	/**
	 * 用户名
	 */
	@NotBlank(message = "用户名不能为空")
	private String username;
	
	/**
	 * 密码
	 */
	@NotBlank(message = "密码不能为空")
	private String password;
	
	/**
	 * 盐
	 */
	private String salt;
	
	/**
	 * 邮箱
	 */
	@NotBlank(message = "邮箱不能为空")
	@Email(message = "邮箱格式不正确")
	private String email;
	
	/**
	 * 手机号
	 */
	private String mobile;
	
	/**
	 * 状态  0：禁用   1：正常
	 */
	private Integer status;
	
	/**
	 * 角色ID列表
	 */
	@TableField(exist = false)
	private List<Long> roleIdList;
	
	/**
	 * 创建者ID
	 */
	private Long createUserId;
}
