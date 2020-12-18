package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.AdminMenuEntity;
import com.jsy.community.entity.admin.AdminMenuEntity2;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chq459799974
 * @description 系统菜单Mapper
 * @since 2020-12-14 10:27
 **/
@Mapper
public interface AdminMenuMapper2 extends BaseMapper<AdminMenuEntity2> {
	
	/**
	 * @Description: 添加菜单
	 * @Param: [adminMenuEntity]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Insert("insert into t_admin_menu(icon,name,url,pid,sort)" +
		"select #{entity.icon},#{entity.name},#{entity.url},#{entity.pid},max(sort)+1 from t_admin_menu where pid = #{entity.pid}")
	int addMenu(@Param("entity") AdminMenuEntity2 adminMenuEntity);
	
	/**
	 * @Description: 寻找父节点
	 * @Param: [pid]
	 * @Return: java.lang.Long
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Select("select id,pid,belong_to from t_admin_menu where id = #{pid}")
	AdminMenuEntity2 findParent(Long pid);
	
	/**
	 * @Description: 获取子菜单列表
	 * @Param: [id]
	 * @Return: java.util.List<com.jsy.community.entity.sys.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	@Select("select * from t_admin_menu where pid = #{id}")
	List<AdminMenuEntity2> getChildrenList(Long id);
	
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
	List<Long> getIdBelongList(List<Long> ids);
	
	/**
	 * @Description: 查询用户菜单
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.sys.AdminMenuEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	 **/
	List<AdminMenuEntity2> queryUserMenu(Long uid);
	
}
