package com.jsy.community.service;

import com.jsy.community.entity.sys.SysMenuEntity;
import com.jsy.community.entity.sys.SysRoleEntity;
import com.jsy.community.entity.sys.SysUserRoleEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SysMenuQO;
import com.jsy.community.qo.sys.SysRoleQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * @author chq459799974
 * @description 系统配置，菜单，角色，权限等
 * @since 2020-12-14 10:55
 **/
public interface ISysConfigService {
	
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
	 * @Return: java.util.List<com.jsy.community.entity.sys.AppMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	List<SysMenuEntity> listOfMenu();
	
	/**
	 * @Description: 查询用户菜单权限(新接口)
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.sys.SysMenuEntity>
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	List<SysMenuEntity> queryMenuByUid(Long roleId);
	
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
	 * @Return: java.util.List<com.jsy.community.entity.sys.SysRoleEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	List<SysRoleEntity> listOfRole();
	
	/**
	 * @Description: 角色列表 分页查询
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.SysRoleEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	PageInfo<SysRoleEntity> queryPage(BaseQO<SysRoleEntity> baseQO);
	
	/**
	 * @author: DKS
	 * @description: 根据用户uid查询用户的角色id
	 * @param uid: 用户uid
	 * @return: java.lang.Long
	 * @date: 2021/10/12 17:05
	 **/
	SysUserRoleEntity queryRoleIdByUid(String uid);
	
	//==================================================== 角色-菜单 ===============================================================
	
	/**
	* @Description: 为角色设置菜单
	 * @Param: [menuIds, roleId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	**/
	boolean setRoleMenus(List<Long> menuIds,Long roleId);
	
	//==================================================== 用户-菜单 ===============================================================
	/**
	* @Description: 查询用户菜单权限
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.sys.AppMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	**/
	List<SysMenuEntity> queryUserMenu(Long uid);
	
	/**
	 * @author: DKS
	 * @description: 查询角色详情
	 * @param roleId: 角色ID
	 * @return: com.jsy.community.entity.admin.AdminRoleEntity
	 * @date: 2021/10/18 16:10
	 **/
	SysRoleEntity queryRoleDetail(Long roleId);
}
