package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.AdminRoleEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chq459799974
 * @description 系统角色Mapper
 * @since 2020-12-14 15:29
 **/
@Mapper
public interface AdminRoleMapper extends BaseMapper<AdminRoleEntity> {
	/**
	 * @Description: 备份角色菜单
	 * @Param: [roleId]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Select("select menu_id from t_admin_role_menu where role_id = #{roleId}")
	List<Long> getRoleMenu(Long roleId);
	
	/**
	 * @Description: 清空角色菜单
	 * @Param: [roleId]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Delete("delete from t_admin_role_menu where role_id = #{roleId}")
	void clearRoleMenu(Long roleId);
	
	/**
	 * @Description: 为角色批量添加菜单
	 * @Param: [list, roleId]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	int addRoleMenuBatch(@Param("list") List<Long> list, @Param("roleId") Long roleId);
}
