package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.admin.AdminMenuEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

/**
 * @author chq459799974
 * @description 系统菜单Mapper
 * @since 2020-12-14 10:27
 **/
@Mapper
public interface AdminMenuMapper extends BaseMapper<AdminMenuEntity> {
	
	/**
	 * @Description: 添加菜单
	 * @Param: [adminMenuEntity]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Deprecated
	@Insert("insert into t_admin_menu(icon,name,url,pid,sort)" +
		"select #{entity.icon},#{entity.name},#{entity.url},#{entity.pid},max(sort)+1 from t_admin_menu where pid = #{entity.pid}")
	int addMenu(@Param("entity") AdminMenuEntity adminMenuEntity);
	
	/**
	 * @Description: 寻找父节点
	 * @Param: [pid]
	 * @Return: java.lang.Long
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Deprecated
	@Select("select id,pid,belong_to from t_admin_menu where id = #{pid}")
	AdminMenuEntity findParent(Long pid);
	
	/**
	 * @Description: 批量获取子菜单id列表(查询pid)
	 * @Param: [ids]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
//	List<Long> getSubIdList(List<Long> ids);
	
	/**
	 * @Description: 批量获取子菜单id列表(查询belong_to)
	 * @Param: [ids]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Deprecated
	List<Long> getIdBelongList(List<Long> ids);
	
	/**
	 * @Description: 查询用户菜单(老sql，暂时弃用)
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.sys.AppMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Deprecated
	List<AdminMenuEntity> queryUserMenu(Long uid);
	
	//================================================== 新版物业端原型 - 用户-菜单start =========================================================================
	
	/**
	* @Description: 根据idList批量查询菜单实体
	 * @Param: [list]
	 * @Return: java.util.List<com.jsy.community.entity.admin.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	List<AdminMenuEntity> queryMenuBatch(List<Long> list);
	
	/**
	 * @Description: 清空用户菜单
	 * @Param: [uid]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/3/23
	 **/
	@Delete("delete from t_admin_user_menu where uid = #{uid}")
	void clearUserMenu(String uid);
	
	/**
	 * @Description: 为用户批量添加菜单
	 * @Param: [menuIdsSet, uid]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/3/23
	 **/
	int addUserMenuBatch(@Param("collection") Set<Long> menuIdsSet, @Param("uid") String uid);
	
	/**
	 * @Description: 获取子菜单列表
	 * @Param: [id]
	 * @Return: java.util.List<com.jsy.community.entity.sys.AppMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Select("select * from t_admin_menu where pid = #{id} and deleted = 0")
	List<AdminMenuEntity> getChildrenList(Long id);
	
	//================================================== 新版物业端原型 - 用户-菜单end =========================================================================
	
	
}
