package com.jsy.community.vo.sys;

import com.jsy.community.entity.admin.AdminMenuEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 大后台用户VO
 * @Author DKS
 * @Date 2021/10/12 17:12
 **/
@Data
@ApiModel("大后台用户VO")
public class SysInfoVo implements Serializable {
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
	private List<Long> roleIdList;
	
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

	// 用户角色id
	private Long roleId;

}
