package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.SysUserEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 系统用户
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {
	
	//备份用户角色
	@Select("select role_id from t_sys_user_role where user_id = #{userId}")
	List<Long> getUserRole(Long userId);
	
	//清空用户角色
	@Delete("delete from t_sys_user_role where user_id = #{userId}")
	void clearUserRole(Long userId);
	
	//批量添加用户角色
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
	
}
