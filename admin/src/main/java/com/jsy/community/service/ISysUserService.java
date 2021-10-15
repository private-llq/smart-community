package com.jsy.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.sys.SysUserAuthEntity;
import com.jsy.community.entity.sys.SysUserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;
import com.jsy.community.qo.sys.NameAndCreatorQO;
import com.jsy.community.qo.sys.SysUserQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;
import java.util.Map;


/**
 * 系统用户
 */
public interface ISysUserService extends IService<SysUserEntity> {
	
	/**
	* @Description: 设置用户角色
	 * @Param: [roleIds, userId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	boolean setUserRoles(List<Long> roleIds,Long userId);
	
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
	 */
	boolean updatePassword(ResetPasswordQO qo, String uid);
	
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
	Map<String,String> setUserName(Long uid,String username);
	
	/**
	 * @Description: 根据手机号查询用户是否存在
	 * @Param: [mobile]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/12
	 **/
	boolean isExistsByMobile(String mobile);
	
	/**
	 * @Description: 根据手机号查询登录用户
	 * @Param: [mobile]
	 * @Return: com.jsy.community.entity.sys.SysUserAuthEntity
	 * @Author: DKS
	 * @Date: 2021/10/12
	 **/
	SysUserAuthEntity queryLoginUserByMobile(String mobile);
	
	/**
	 * @Description: 查询用户小区账户资料
	 * @Param: [mobile, communityId]
	 * @Return: com.jsy.community.entity.sys.SysUserEntity
	 * @Author: DKS
	 * @Date: 2021/10/12
	 **/
	SysUserEntity queryUserByMobile(String mobile, Long communityId);
	
	/**
	 * @Description: 操作员条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	PageInfo queryOperator(BaseQO<SysUserQO> baseQO);
	
	/**
	 * @Description: 添加操作员
	 * @Param: [sysUserEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	void addOperator(SysUserEntity sysUserEntity);
	
	/**
	 * @Description: 编辑操作员
	 * @Param: [sysUserEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	void updateOperator(SysUserEntity sysUserEntity);
	
	/**
	 * @Description: 删除操作员
	 * @author: DKS
	 * @since: 2021/10/13 15:38
	 * @Author: DKS
	 * @Date: 2021/10/13
	 */
	void deleteOperator(Long id);
	
	/**
	 * @Description: 根据手机号检查小区用户是否存在(t_sys_user)
	 * @Param: [mobile]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	boolean checkUserExists(String mobile);
	
	/**
	 * @Description: 根据uid查询手机号
	 * @Param: [uid]
	 * @Return: java.lang.String
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	String queryMobileByUid(String uid);
	
	/**
	 * @Description: 修改手机号
	 * @Param: [newMobile, oldMobile]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	boolean changeMobile(String newMobile,String oldMobile);
}
