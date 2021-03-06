package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.admin.AdminUserAuthEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;
import com.zhsj.base.api.entity.RealUserDetail;
import com.zhsj.base.api.vo.PageVO;

import java.util.Collection;
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
	
//	/**
//	 * 根据用户名，查询系统用户
//	 */
//	AdminUserEntity queryByUserName(String username);
//
//	/**
//	 * 根据邮箱，查询系统用户
//	 */
//	AdminUserEntity queryByEmail(String email);
//
	
	/**
	 * 保存用户
	 */
	@Deprecated
	void saveUser(AdminUserEntity user);
	
	/**
	 * 修改用户
	 */
	@Deprecated
	boolean updateUser(AdminUserEntity user);
	
	/**
	 * 删除用户
	 */
	@Deprecated
	void deleteBatch(Long[] userIds);
	
	/**
	 * 修改密码
	 *
	 * @param userId      用户ID
	 * @param password    原密码
	 * @param newPassword 新密码
	 */
	@Deprecated
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
	//================ 资料或账户相关begin =================
	/**
	* @Description: 根据手机号查询用户是否存在
	 * @Param: [mobile]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	boolean isExistsByMobile(String mobile);
	
	/** 
	* @Description: 根据手机号查询登录用户
	 * @Param: [mobile]
	 * @Return: com.jsy.community.entity.admin.AdminUserAuthEntity
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	AdminUserAuthEntity queryLoginUserByMobile(String mobile);
	
	/**
	* @Description: 根据手机号查询登录用户(操作员)已加入小区idList
	 * @Param: [mobile]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	List<Long> queryCommunityIdList(String mobile);
	
	/**
	* @Description: 查询用户小区账户资料
	 * @Param: [mobile, communityId]
	 * @Return: com.jsy.community.entity.admin.AdminUserEntity
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	AdminUserEntity queryUserByMobile(String mobile,Long communityId);
	
	/**
	* @Description: 根据uid查询用户信息
	 * @Param: [uid]
	 * @Return: com.jsy.community.entity.admin.AdminUserEntity
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	AdminUserEntity queryByUid(String uid);
	
	/**
	* @Description: uid批量查姓名
	 * @Param: [uidList]
	 * @Return: java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.String>>
	 * @Author: chq459799974
	 * @Date: 2021/4/1
	**/
	Map<String, RealUserDetail> queryNameByUidBatch(Collection<String> uidList);
	
	/**
	 * @Description: 查询用户菜单id列表
	 * @Param: [id]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/4/9
	 **/
	List<String> queryUserMenuIdList(Long id);
	
	/**
	* @Description: 更新用户头像
	 * @Param: [url, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	boolean updateAvatar(String url,String uid);
	
	/**
	* @Description: 个人信息查询
	 * @Param: [uid]
	 * @Return: com.jsy.community.entity.admin.AdminUserEntity
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	AdminUserEntity queryPersonalData(String uid);
	
	/**
	* @Description: 修改密码
	 * @Param: [qo, uid]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	boolean updatePassword(ResetPasswordQO qo, String uid);
	
	/**
	* @Description: 修改手机号
	 * @Param: [newMobile, oldMobile]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/19
	**/
	boolean changeMobile(String newMobile,String oldMobile);
	//================ 资料或账户相关end =================
	
	//============== 操作员管理相关begin ===============
	/**
	* @Description: 操作员条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo
	 * @Author: chq459799974
	 * @Date: 2021/3/16
	**/
	PageVO<AdminUserEntity> queryOperator(BaseQO<AdminUserQO> baseQO);
	
	/**
	* @Description: 添加操作员
	 * @Param: [adminUserQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	Integer addOperator(AdminUserQO adminUserQO);
	
	/**
	 * @Description: 编辑操作员
	 * @Param: [adminUserQO]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/3/18
	 **/
	void updateOperator(AdminUserQO adminUserQO, Long id);
	
	/**
	 * @Description: 删除操作员
	 * @author: DKS
	 * @since: 2021/10/13 15:38
	 * @Author: DKS
	 * @Date: 2021/10/13
	 */
	void deleteOperator(Long id);
	
//	/**
//	* @Description: 重置密码
//	 * @Param: [id, uid]
//	 * @Return: boolean
//	 * @Author: chq459799974
//	 * @Date: 2021/3/18
//	**/
//	boolean resetPassword(Long id,String uid);
	
	/**
	* @Description: 根据id查询uid
	 * @Param: [id]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/9
	**/
	String queryUidById(Long id);
	
	/**
	* @Description: 根据手机号检查小区用户是否存在(t_admin_user)
	 * @Param: [mobile]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/4/19
	**/
	boolean checkUserExists(String mobile);
	
	/**
	* @Description: 根据uid查询手机号
	 * @Param: [uid]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/19
	**/
	String queryMobileByUid(String uid);
	//============== 操作员管理相关end ===============
	
	//==================================== 物业端（新）end ====================================
}
