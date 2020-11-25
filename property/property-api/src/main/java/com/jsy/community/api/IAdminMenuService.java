package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.AdminMenuEntity;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-11-24
 */
public interface IAdminMenuService extends IService<AdminMenuEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.AdminMenuEntity>
	 * @Author lihao
	 * @Description 查询所有父菜单
	 * @Date 2020/11/24 11:03
	 * @Param []
	 **/
	List<AdminMenuEntity> listParentMenu();
	
	/**
	 * @return java.util.List<com.jsy.community.entity.AdminMenuEntity>
	 * @Author lihao
	 * @Description 查询所有子菜单
	 * @Date 2020/11/24 11:03
	 * @Param []
	 **/
	List<AdminMenuEntity> listChildMenu();
	
	/**
	 * @return java.util.List<com.jsy.community.entity.AdminMenuEntity>
	 * @Author lihao
	 * @Description 根据父菜单id查询其子菜单
	 * @Date 2020/11/25 9:10
	 * @Param [parentId]
	 **/
	List<AdminMenuEntity> listChildMenuById(Long parentId);
}
