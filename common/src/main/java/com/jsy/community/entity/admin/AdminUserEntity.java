package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import com.jsy.community.utils.RegexUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
	private Long communityId;
	
	/**
	 * 用户id
	 */
	private String uid;
	
	/**
	 * 编号
	 */
	@NotBlank(groups = addOperatorValidatedGroup.class, message = "缺少编号")
	@Length(groups = {addOperatorValidatedGroup.class,updateOperatorValidatedGroup.class}, max = 20, message = "编号长度超限")
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
	@TableField(exist = false)
	private String password;
	
	/**
	 * 是否设置密码 0.否 1.是
	 */
	@TableField(exist = false)
	private Integer hasPassword;
	
	/**
	 * 盐
	 */
	@TableField(exist = false)
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
	@Pattern(groups = {addOperatorValidatedGroup.class, updateOperatorValidatedGroup.class}, regexp = RegexUtils.REGEX_MOBILE, message = "电话号码错误，只支持电信|联通|移动")
	@NotBlank(groups = addOperatorValidatedGroup.class, message = "缺少电话号码")
	private String mobile;
	
	/**
	 * 身份证号
	 */
	@Pattern(groups = {addOperatorValidatedGroup.class, updateOperatorValidatedGroup.class}, regexp = RegexUtils.REGEX_ID_CARD, message = "身份证格式错误")
	@NotBlank(groups = addOperatorValidatedGroup.class, message = "缺少身份证号")
	private String idCard;
	
	/**
	 * 昵称
	 */
	private String nickname;
	
	/**
	 * 真实姓名
	 */
	@Pattern(groups = {addOperatorValidatedGroup.class, updateOperatorValidatedGroup.class}, regexp = RegexUtils.REGEX_REAL_NAME, message = "请输入一个正确的姓名")
	@Length( groups = {addOperatorValidatedGroup.class, updateOperatorValidatedGroup.class}, min = 2, max = 20, message = "姓名长度不在正常范围之内!")
	@NotBlank(groups = {inviteUserValidatedGroup.class,addOperatorValidatedGroup.class}, message = "人员姓名不能为空")
	private String realName;
	
	/**
	 * 头像地址
	 */
	private String avatarUrl;
	
	/**
	 * 组织机构名称
	 */
	@TableField(exist = false)
	private String orgName;
	
	/**
	 * 状态  0：正常   1：禁用
	 */
	@NotNull(groups = addOperatorValidatedGroup.class, message = "缺少停启用状态")
	@Range(groups = {addOperatorValidatedGroup.class,updateOperatorValidatedGroup.class}, min = 0, max = 1, message = "操作员停/启用状态不正确")
	private Integer status;
	
	/**
	 * 角色类型(第一版直接放进用户表)  1：超级管理员   2：普通用户
	 */
	private Integer roleType;
	
	/**
	 * 角色(账户)类型名称
	 */
	@TableField(exist = false)
	private String roleTypeName;
	
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
	 * 菜单ID列表(新版物业端原型 一个小区用户量少，不设角色，直接关联菜单)
	 */
	@TableField(exist = false)
	private List<Long> menuIdList;
	
	/**
	 * 菜单功能授权数统计
	 */
	@TableField(exist = false)
	private Integer menuCount;
	
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
	 * 修改操作员传参验证
	 */
	public interface updateOperatorValidatedGroup{}
	
	/**
	 * 登录传参验证
	 */
	public interface loginValidatedGroup{}
	
}
