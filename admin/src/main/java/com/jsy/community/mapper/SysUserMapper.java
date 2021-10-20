package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.sys.SysUserEntity;
import com.jsy.community.qo.sys.SysUserQO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 系统用户
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {
	
	/**
	* @Description: 备份用户角色
	 * @Param: [userId]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	**/
	@Select("select role_id from t_sys_user_role where user_id = #{userId}")
	List<Long> getUserRole(Long userId);
	
	/**
	* @Description: 清空用户角色
	 * @Param: [userId]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	**/
	@Delete("delete from t_sys_user_role where user_id = #{userId}")
	void clearUserRole(Long userId);
	
	/**
	* @Description: 批量添加用户角色
	 * @Param: [list, userId]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	**/
	int addUserRoleBatch(@Param("list") List<Long> list, @Param("userId") Long userId);
	
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
	 * @author: Pipi
	 * @description: 操作员条件查询
	 * @param sysUserQO: 查询条件
	 * @param startSize: 分页起点
	 * @param size: 每页数量
	 * @return: java.util.List<com.jsy.community.entity.admin.AdminUserEntity>
	 * @date: 2021/9/28 10:21
	 **/
	List<SysUserEntity> queryPageUserEntity(@Param("qo") SysUserQO sysUserQO,
	                                          @Param("startSize") Long startSize,
	                                          @Param("size") Long size);
	
	/**
	 * @author: Pipi
	 * @description: 操作员条件查询数量
	 * @param sysUserQO:
	 * @return: java.lang.Integer
	 * @date: 2021/9/28 11:41
	 **/
	Integer countPageUserEntity(@Param("qo") SysUserQO sysUserQO);
	
	/**
	 * @Description: 添加操作员
	 * @Param: [entity]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	 **/
	@Insert("insert into t_sys_user(id,username,password,salt,email,real_name,status,mobile,create_user_id,create_time) " +
		"values(#{entity.id},#{entity.username},#{entity.password},#{entity.salt},#{entity.email},#{entity.realName},#{entity.status},#{entity.mobile},#{entity.createUserId},now())")
	int addOperator(@Param("entity") SysUserEntity entity);
	
	/**
	 * @Description: 根据id查uid
	 * @Param: [id]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	 **/
	@Select("select id,mobile from t_sys_user where id = #{id} and deleted = 0")
	UserEntity queryUidById(Long id);
	
	/**
	 * @Description: 编辑操作员
	 * @Param: [entity]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	 **/
	int updateOperator(@Param("entity") SysUserEntity entity);
	
	/**
	 * @Description: 根据手机号查询用户数
	 * @Param: [mobile]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/7/22
	 **/
	@Select("select id from t_sys_user where deleted = 0 and mobile = #{mobile} limit 1")
	Long countUser(String mobile);
	
	/**
	 * @Description: uid查手机号
	 * @Param: [uid]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	 **/
	@Select("select mobile from t_sys_user where id = #{uid}")
	String queryMobileByUid(String uid);
	
	/**
	 * @Description: 更换手机号
	 * @Param: [newMobile, uid]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/4/19
	 **/
	@Update("update t_sys_user set mobile = #{newMobile} where mobile = #{oldMobile}")
	int changeMobile(@Param("newMobile")String newMobile, @Param("oldMobile")String oldMobile);
	
	/**
	 * 通过管理员id查出 管理员姓名
	 * @param userId  管理员id
	 * @return          返回管理员姓名
	 */
	String querySysNameByUid(String userId);
	
	/**
	 * @Description: 获取uid根据姓名模糊查询
	 * @Param: [userName]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: DKS
	 * @Date: 2021/10/20 13:43
	 **/
	List<String> queryUidListByRealName(@Param("userName")String userName);
	
	/**
	 * @Description: id查用户信息
	 * @Param: [uid]
	 * @Return: com.jsy.community.entity.sys.SysUserEntity
	 * @Author: DKS
	 * @Date: 2021/10/20 13:43
	 **/
	@Select("select * from t_sys_user where id = #{id}")
	SysUserEntity queryById(String id);
}
