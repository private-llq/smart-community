package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.AppMenuEntity;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
public interface IAppMenuService extends IService<AppMenuEntity> {
	
//	/**
//	 * @return java.util.List<com.jsy.community.entity.AppMenuEntity>
//	 * @Author lihao
//	 * @Description 查询APP所有父菜单信息
//	 * @Date 2020/11/24 11:03
//	 * @Param []
//	 **/
//	List<AppMenuEntity> listParentMenu();
//
//	/**
//	 * @return java.util.List<com.jsy.community.entity.AppMenuEntity>
//	 * @Author lihao
//	 * @Description 根据父菜单id查询APP其子菜单信息
//	 * @Date 2020/11/25 9:10
//	 * @Param [parentId]
//	 **/
//	List<AppMenuEntity> listChildMenuById(Long parentId);
//
//	/**
//	 * @return java.util.List<com.jsy.community.vo.menu.FrontParentMenu>
//	 * @Author lihao
//	 * @Description 查询所有菜单【树形结构】
//	 * @Date 2021/2/2 17:25
//	 * @Param []
//	 **/
//	List<FrontParentMenu> listAdminMenu(Long communityId);
//
//	/**
//	 * @return void
//	 * @Author lihao
//	 * @Description 新增父菜单
//	 * @Date 2021/2/2 18:43
//	 * @Param [appMenuEntity]
//	 **/
//	void addParentMenu(AppMenuEntity appMenuEntity,Long communityId);
//
//	/**
//	 * @return void
//	 * @Author lihao
//	 * @Description 新增子菜单
//	 * @Date 2021/2/3 10:07
//	 * @Param [appMenuEntity]
//	 **/
//	void addChildMenu(AppMenuEntity appMenuEntity,Long communityId);
//
//	/**
//	 * @return void
//	 * @Author lihao
//	 * @Description 删除菜单
//	 * @Date 2021/2/3 10:18
//	 * @Param [id, communityId]
//	 **/
//	void removeMenu(Long id, Long communityId);
	
	/**
	 * @return java.util.List<com.jsy.community.entity.AppMenuEntity>
	 * @Author lihao
	 * @Description 查询所有菜单
	 * @Date 2021/3/23 10:18
	 * @Param [communityId]
	 **/
	List<AppMenuEntity> listMenu(Long communityId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 添加菜单
	 * @Date 2021/3/23 10:37
	 * @Param [appMenuEntity]
	 **/
	void appMenu(List<AppMenuEntity> appMenuEntityList);
}
