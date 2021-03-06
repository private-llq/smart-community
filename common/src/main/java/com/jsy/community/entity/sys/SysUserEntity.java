package com.jsy.community.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import com.zhsj.base.api.domain.PermitMenu;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 后台系统用户
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_sys_user")
public class SysUserEntity extends BaseEntity {
	
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 密码
	 */
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
	private List<PermitMenu> menuList;
	
	/**
	 * 创建者ID
	 */
	private Long createUserId;
	
	/**
	 * token
	 */
	@TableField(exist = false)
	private String token;
	
	// 角色Id
	@TableField(exist = false)
	private Long roleId;
	
	// 角色IdStr
	@TableField(exist = false)
	private String roleIdStr;
	
	// 角色名称
	@TableField(exist = false)
	private String roleName;
	
	/**
	 * 注册邀请传参验证
	 */
	public interface inviteUserValidatedGroup{}
	
}
