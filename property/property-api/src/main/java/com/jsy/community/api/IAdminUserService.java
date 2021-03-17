package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;
import java.util.Map;


/**
 * 系统用户
 */
public interface IAdminUserService extends IService<AdminUserEntity> {
	
	/**
	* @Description: 设置用户角色
	 * @Param: [roleIds, userId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	boolean setUserRoles(List<Long> roleIds, Long userId);
	
//	IPage<AdminUserEntity> queryPage(BaseQO<NameAndCreatorQO> qo);
	
	/**
	 * 查询用户的所有权限
	 *
	 * @param userId 用户ID
	 */
	List<String> queryAllPerms(Long userId);
	
	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);
	
	/**
	 * 根据用户名，查询系统用户
	 */
	AdminUserEntity queryByUserName(String username);
	
	/**
	 * 根据邮箱，查询系统用户
	 */
	AdminUserEntity queryByEmail(String email);
	
	/**
	 * 根据手机号，查询系统用户
	 */
	AdminUserEntity queryByMobile(String mobile);
	
	/**
	 * 保存用户
	 */
	void saveUser(AdminUserEntity user);
	
	/**
	 * 修改用户
	 */
	boolean updateUser(AdminUserEntity user);
	
	/**
	 * 删除用户
	 */
	void deleteBatch(Long[] userIds);
	
	/**
	 * 修改密码
	 *
	 * @param userId      用户ID
	 * @param password    原密码
	 * @param newPassword 新密码
	 */
	boolean updatePassword(Long userId, String password, String newPassword);
	
	/**
	* @Description: 查询用户名是否已存在
	 * @Param: [username]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/25
	**/
	boolean checkUsernameExists(String username);
	
	/**
	* @Description: 邮件注册邀请
	 * @Param: [sysUserEntity]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2020/11/30
	**/
	Map<String,String> invitation(AdminUserEntity adminUserEntity);
	
	/**
	* @Description: 邮件注册激活确认
	 * @Param: [sysUserEntity]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2020/11/30
	**/
	Map<String,String> activation(AdminUserEntity adminUserEntity);
	
	/**
	* @Description: 邮件邀请注册后设置用户名
	 * @Param: [uid, username]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	**/
	Map<String,String> setUserName(Long uid, String username);
	
	
	//==================================== 物业端（新）begin ====================================
	
	/**
	* @Description: 操作员条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo
	 * @Author: chq459799974
	 * @Date: 2021/3/16
	**/
	PageInfo queryOperator(BaseQO<AdminUserQO> baseQO);
	
	/**
	* @Description: 添加操作员
	 * @Param: [adminUserEntity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	boolean addOperator(AdminUserEntity adminUserEntity);
	
	
	//==================================== 物业端（新）end ====================================
}
