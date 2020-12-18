package com.jsy.community.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.sys.SysUserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.NameAndCreatorQO;

import java.util.List;
import java.util.Map;


/**
 * 系统用户
 */
public interface IAdminUserService extends IService<SysUserEntity> {
	
	/**
	* @Description: 设置用户角色
	 * @Param: [roleIds, userId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	boolean setUserRoles(List<Long> roleIds, Long userId);
	
	IPage<SysUserEntity> queryPage(BaseQO<NameAndCreatorQO> qo);
	
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
	SysUserEntity queryByUserName(String username);
	
	/**
	 * 根据邮箱，查询系统用户
	 */
	SysUserEntity queryByEmail(String email);
	
	/**
	 * 保存用户
	 */
	void saveUser(SysUserEntity user);
	
	/**
	 * 修改用户
	 */
	boolean updateUser(SysUserEntity user);
	
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
	* @Description: 邮件注册邀请
	 * @Param: [sysUserEntity]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2020/11/30
	**/
	Map<String,String> invitation(SysUserEntity sysUserEntity);
	
	/**
	* @Description: 邮件注册激活确认
	 * @Param: [sysUserEntity]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2020/11/30
	**/
	Map<String,String> activation(SysUserEntity sysUserEntity);
	
	/**
	* @Description: 邮件邀请注册后设置用户名
	 * @Param: [uid, username]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	**/
	Map<String,String> setUserName(Long uid, String username);
}
