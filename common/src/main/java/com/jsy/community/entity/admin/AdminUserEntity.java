package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 物业端用户
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_admin_user")
public class AdminUserEntity extends BaseEntity {
	
	/**
	 * 社区ID
	 */
	@NotNull(groups = addOperatorValidatedGroup.class, message = "缺少社区ID")
	private Long communityId;
	
	/**
	 * 用户id
	 */
	private String uid;
	
	/**
	 * 编号
	 */
	@NotBlank(groups = addOperatorValidatedGroup.class, message = "缺少编号")
	@Length(groups = addOperatorValidatedGroup.class, max = 20, message = "编号长度超限")
	private String number;
	
	/**
	 * 用户名
	 */
	@NotBlank(groups = loginValidatedGroup.class, message = "用户名不能为空")
	private String username;
	
	/**
	 * 密码
	 */
	@NotBlank(groups = loginValidatedGroup.class, message = "密码不能为空")
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
	@NotBlank(groups = addOperatorValidatedGroup.class, message = "缺少电话号码")
	private String mobile;
	
	/**
	 * 身份证号
	 */
	@NotBlank(groups = addOperatorValidatedGroup.class, message = "身份证号")
	private String idCard;
	
	/**
	 * 昵称
	 */
	private String nickname;
	
	/**
	 * 真实姓名
	 */
	@NotBlank(groups = {inviteUserValidatedGroup.class,addOperatorValidatedGroup.class}, message = "人员姓名不能为空")
	private String realName;
	
	/**
	 * 组织机构名称
	 */
	@TableField(exist = false)
	private String orgName;
	
	/**
	 * 状态  0：正常   1：禁用
	 */
	@Range(groups = addOperatorValidatedGroup.class, min = 1, max = 2, message = "操作员停/启用状态不正确")
	private Integer status;
	
	/**
	 * 角色类型(第一版直接放进用户表)  1：超级管理员   2：普通用户
	 */
	private Integer roleType;
	
	/**
	 * 组织机构id
	 */
	@NotNull(groups = addOperatorValidatedGroup.class, message = "缺少组织机构")
	private Long orgId;
	
	/**
	 * 职务
	 */
	@NotBlank(groups = addOperatorValidatedGroup.class, message = "缺少职务")
	private String job;
	
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
	 * 创建者UID (老功能暂时使用，新接口统一用createBy)
	 */
	@TableField(exist = false)
	private String createUserId;
	
	/**
	 * 创建者UID
	 */
	private String createBy;
	
	/**
	 * 创建者姓名
	 */
	@TableField(exist = false)
	private String createUserName;
	
	/**
	 * 最近更新者UID
	 */
	private String updateBy;
	
	/**
	 * 最近更新者姓名
	 */
	@TableField(exist = false)
	private String updateUserName;
	
	/**
	 * token
	 */
	@TableField(exist = false)
	private String token;
	
	/**
	 * 注册邀请传参验证
	 */
	public interface inviteUserValidatedGroup{}
	
	/**
	 * 添加操作员传参验证
	 */
	public interface addOperatorValidatedGroup{}
	
	/**
	 * 登录传参验证
	 */
	public interface loginValidatedGroup{}
	
}
