package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 物业端用户
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_admin_user")
public class AdminUserEntity extends BaseEntity {
	
	/**
	 * 用户id
	 */
	private String uid;
	
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
	@NotBlank(groups = {inviteUserValidatedGroup.class}, message = "邮箱不能为空")
	@Email(groups = {inviteUserValidatedGroup.class}, message = "邮箱格式不正确")
	private String email;
	
	/**
	 * 手机号
	 */
	private String mobile;
	
	/**
	 * 昵称
	 */
	private String nickname;
	
	@NotBlank(groups = {inviteUserValidatedGroup.class}, message = "人员姓名不能为空")
	private String realName;
	
	/**
	 * 状态  0：禁用   1：正常
	 */
	private Integer status;
	
	/**
	 * 角色ID列表
	 */
	@TableField(exist = false)
	private List<Integer> roleIdList;
	
	/**
	 * 用户菜单列表
	 */
	@TableField(exist = false)
	private List<AdminMenuEntity> menuList;
	
	/**
	 * 创建者ID
	 */
	private String createUserId;
	
	/**
	 * 创建者姓名
	 */
	@TableField(exist = false)
	private String createUserName;
	
	/**
	 * token
	 */
	private String token;
	
	/**
	 * 注册邀请传参验证
	 */
	public interface inviteUserValidatedGroup{}
	
}
