package com.jsy.community.api;


import com.jsy.community.entity.admin.AdminCommunityEntity;
import com.jsy.community.entity.admin.AdminMenuEntity;
import com.jsy.community.entity.admin.AdminRoleEntity;
import com.jsy.community.entity.admin.AdminUserRoleEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminMenuQO;
import com.jsy.community.qo.admin.AdminRoleQO;
import com.zhsj.base.api.domain.PermitMenu;
import com.zhsj.base.api.vo.PageVO;

import java.util.List;

/**
 * @author chq459799974
 * @description 系统配置，菜单，角色，权限等
 * @since 2020-12-14 10:55
 **/
public interface IAdminConfigService {
	
	//==================================================== Menu菜单 ===============================================================
	/**
	 * @Description: 新增菜单
	 * @Param: [sysMenuEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	boolean addMenu(AdminMenuEntity adminMenuEntity);
	
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
	boolean updateMenu(AdminMenuQO adminMenuQO);
	
	/**
	 * @Description: 菜单列表
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.AppMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	List<AdminMenuEntity> listOfMenu();
	
	/**
	 * @Description: 根据角色类型查询所有菜单
	 * @author: DKS
	 * @since: 2021/12/25 9:23
	 * @Param: [roleType, id]
	 * @return: java.util.List<com.zhsj.base.api.domain.PermitMenu>
	 */
	List<PermitMenu> MenuPage(Integer roleType, Long id);
	
	//==================================================== Role角色 ===============================================================
	/**
	* @Description: 添加角色
	 * @Param: [sysRoleEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	void addRole(AdminRoleEntity adminRoleEntity);
	
	/**
	* @Description: 删除角色
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	void delRole(List<Long> roleIds, Long companyId);
	
	/**
	* @Description: 修改角色
	 * @Param: [sysRoleQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	void updateRole(AdminRoleQO adminRoleQO, Long id);
	
	/**
	* @Description: 角色列表 分页查询
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.sys.SysRoleEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	PageVO<AdminRoleEntity> queryPage(BaseQO<AdminRoleEntity> baseQO);

	PageVO<AdminRoleEntity> queryPageAll(BaseQO<AdminRoleEntity> baseQO);

	/**
	 * @author: Pipi
	 * @description: 查询角色详情
	 * @param roleId: 角色ID
	 * @param companyId: 物业公司ID
	 * @return: com.jsy.community.entity.admin.AdminRoleEntity
	 * @date: 2021/8/9 10:33
	 **/
	AdminRoleEntity queryRoleDetail(Long roleId, Long companyId);

	//==================================================== 用户-角色 ===============================================================
	/**
	 * @author: Pipi
	 * @description: 根据用户uid查询用户的角色id
	 * @param uid: 用户uid
	 * @return: java.lang.Long
	 * @date: 2021/8/6 10:50
	 **/
	AdminUserRoleEntity queryRoleIdByUid(String uid);
	
	//==================================================== 用户-菜单 ===============================================================
	/**
	* @Description: 查询用户菜单权限(老接口，暂时弃用)
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.sys.AppMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	**/
	List<AdminMenuEntity> queryUserMenu(Long uid);
	
	//================================================== 新版物业端原型 - 用户-菜单start =========================================================================
	/**
	* @Description: 查询用户菜单权限(新接口)
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.admin.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	List<AdminMenuEntity> queryMenuByUid(Long roleId, Integer loginType);
	
	/**
	* @Description: 统计用户菜单数
	 * @Param: [uid]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/4/8
	**/
	Integer countUserMenu(String uid);
	
	/**
	* @Description: 查询用户菜单id列表
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/4/9
	**/
	List<String> queryUserMenuIdList(String uid);
	
	/**
	* @Description: 为用户分配菜单
	 * @Param: [menuIds, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/23
	**/
	void setUserMenus(List<Long> menuIds,String uid);
	
	/**
	* @Description: 管理员社区权限列表查询
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.admin.AdminCommunityEntity>
	 * @Author: chq459799974
	 * @Date: 2021/7/22
	**/
	List<AdminCommunityEntity> listAdminCommunity(String uid);
	
	/**
	* @Description: 管理员社区权限id列表查询
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/7/22
	**/
	List<Long> queryAdminCommunityIdListByUid(String uid);

	/**
	 * @author: Pipi
	 * @description: 新增用户与小区权限数据
	 * @param uid: 用户id
     * @param communityId: 社区id
	 * @return: java.lang.Integer
	 * @date: 2021/7/22 10:35
	 **/
	Integer addAdminCommunity(String uid, Long communityId);

	
	/**
	* @Description: 批量添加用户社区权限
	 * @Param: [communityIds, uid]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/7/22
	**/
	void updateAdminCommunityBatch(List<String> communityIds,String uid);
	//================================================== 新版物业端原型 - 用户-菜单end =========================================================================
}
