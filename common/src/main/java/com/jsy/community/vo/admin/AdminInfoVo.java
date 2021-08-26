package com.jsy.community.vo.admin;

import com.jsy.community.entity.admin.AdminMenuEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 物业端用户VO
 * @Author qq459799974
 * @Date 2020/11/16 14:16
 **/
@Data
@ApiModel("物业端用户VO")
public class AdminInfoVo implements Serializable {
	/**
	 * 社区id
	 */
	private Long communityId;

	// 物业公司ID
	private Long companyId;

	/**
	 * 有权限的社区ids(List)
	 */
	private List<Long> communityIdList;
	/**
	 * 社区名
	 */
	private String communityName;
	/**
	 * 角色类型(第一版直接放进用户表)  1：超级管理员   2：普通用户
	 */
	private Integer roleType;
	
	/**
	 * uid
	 */
	private String uid;
	
	/**
	 * 姓名
	 */
	private String realName;
	
	/**
	 * 头像地址
	 */
	private String avatarUrl;
	
	/**
	 * 状态  0：禁用   1：正常
	 */
	private Integer status;
	
	/**
	 * 角色ID列表
	 */
	private List<Integer> roleIdList;
	
	/**
	 * 用户菜单列表
	 */
	private List<AdminMenuEntity> menuList;
	
	/**
	 * 手机号
	 */
	private String mobile;
	
	/**
	 * token
	 */
	private String token;
	
	/**
	 * 编号
	 */
	private String number;
	
	/**
	 * 本次登录类型 1.物业公司登录 2.小区管理员登录
	 */
	private Integer loginType;

	// 用户角色id
	private Long roleId;

}
