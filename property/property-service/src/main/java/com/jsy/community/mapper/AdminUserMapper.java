package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.AdminUserEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 系统用户
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUserEntity> {
	
	/**
	 * @Description: 备份用户角色
	 * @Param: [userId]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Select("select role_id from t_admin_user_role where user_id = #{userId}")
	List<Long> getUserRole(Long userId);
	
	/**
	 * @Description: 清空用户角色
	 * @Param: [userId]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Delete("delete from t_admin_user_role where user_id = #{userId}")
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
	AdminUserEntity queryByUserName(String username);
	
	/**
	 * 根据邮箱，查询系统用户
	 */
	AdminUserEntity queryByEmail(String email);
	
	/**
	 * 根据手机号，查询系统用户
	 */
	AdminUserEntity queryByMobile(String mobile);
	
}
