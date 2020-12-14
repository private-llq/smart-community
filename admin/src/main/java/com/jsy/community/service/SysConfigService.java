package com.jsy.community.service;

import com.jsy.community.entity.admin.SysMenuEntity;
import com.jsy.community.entity.admin.SysRoleEntity;
import com.jsy.community.qo.admin.SysMenuQO;
import com.jsy.community.qo.admin.SysRoleQO;

import java.util.List;

/**
 * @author chq459799974
 * @description 系统配置，菜单，角色，权限等
 * @since 2020-12-14 10:55
 **/
public interface SysConfigService {
	
	//==================================================== Menu菜单 ===============================================================
	/**
	 * @Description: 新增菜单
	 * @Param: [sysMenuEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	boolean addMenu(SysMenuEntity sysMenuEntity);
	
	/**
	* @Description: 级联删除
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	boolean delMenu(Long id);
	
	/**
	 * @Description: 修改菜单
	 * @Param: [sysMenuQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	boolean updateMenu(SysMenuQO sysMenuQO);
	
	/**
	 * @Description: 菜单列表
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.admin.SysMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	List<SysMenuEntity> listOfMenu();
	
	//==================================================== Role角色 ===============================================================
	/**
	* @Description: 添加角色
	 * @Param: [sysRoleEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	boolean addRole(SysRoleEntity sysRoleEntity);
	
	/**
	* @Description: 删除角色
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	boolean delRole(Long id);
	
	/**
	* @Description: 修改角色
	 * @Param: [sysRoleQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	boolean updateRole(SysRoleQO sysRoleQO);
	
	/**
	* @Description: 角色列表
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.admin.SysRoleEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	List<SysRoleEntity> listOfRole();
}
