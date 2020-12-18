package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.AppMenuEntity;
import com.jsy.community.vo.menu.FrontParentMenu;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
public interface IAdminMenuService extends IService<AppMenuEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.AppMenuEntity>
	 * @Author lihao
	 * @Description 查询app所有父菜单
	 * @Date 2020/12/2 11:45
	 * @Param []
	 **/
	List<FrontParentMenu> listAdminMenu();
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 添加父菜单
	 * @Date 2020/12/2 13:39
	 * @Param [adminMenu]
	 **/
	void insertAdminMenu(AppMenuEntity adminMenu);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 删除菜单
	 * @Date 2020/12/2 13:39
	 * @Param [id]
	 **/
	void removeAdminMenu(Long id);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 添加子菜单
	 * @Date 2020/12/2 13:42
	 * @Param [adminMenu]
	 **/
	void insertChildMenu(AppMenuEntity adminMenu);
}
