package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.AppMenuEntity;
import com.jsy.community.entity.IndexMenuEntity;
import com.jsy.community.vo.FrontMenuVO;
import com.jsy.community.qo.BaseQO;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author lihao
 * @since 2020-11-14
 */
public interface IIndexMenuService extends IService<IndexMenuEntity> {
	
	
	/**
	 * @return java.lang.Long
	 * @Author lihao
	 * @Description 添加菜单信息
	 * @Date 2020/11/14 17:37
	 * @Param [menuEntity]
	 **/
	Integer saveMenu(IndexMenuEntity menuEntity);
	
	/**
	 * @return java.lang.Integer
	 * @Author lihao
	 * @Description 根据id修改菜单信息
	 * @Date 2020/11/14 17:52
	 * @Param [menuEntity]
	 **/
	Integer updateMenu(Long id, FrontMenuVO frontMenuVO);
	
	/**
	 * @return java.util.List<com.jsy.community.entity.IndexMenuEntity>
	 * @Author lihao
	 * @Description 分页查询所有菜单信息
	 * @Date 2020/11/14 17:59
	 * @Param [baseEntity]
	 **/
	List<FrontMenuVO> listFrontMenu(BaseQO<IndexMenuEntity> baseQO);
	
	/**
	 * @return java.util.List<com.jsy.community.entity.IndexMenuEntity>
	 * @Author lihao
	 * @Description 查询首页展示的菜单选项
	 * @Date 2020/11/14 21:17
	 * @Param [number]
	 **/
	List<IndexMenuEntity> listIndexMenu(Long communityId);
	
	/**
	 * @return java.lang.Integer
	 * @Author lihao
	 * @Description 根据id删除菜单信息
	 * @Date 2020/11/14 21:42
	 * @Param [id]
	 **/
	Integer removeMenu(Long id);
	
	/**
	 * @return java.util.List<com.jsy.community.entity.IndexMenuEntity>
	 * @Author lihao
	 * @Description 查询所有父菜单
	 * @Date 2020/11/14 22:07
	 * @Param []
	 **/
	List<IndexMenuEntity> listParentMenu();
	
	/**
	 * @return com.jsy.community.entity.IndexMenuEntity
	 * @Author lihao
	 * @Description 根据id查询菜单信息
	 * @Date 2020/11/15 15:00
	 * @Param [id]
	 **/
	FrontMenuVO getMenuById(Long id);
	
	/**
	 * @return java.lang.Integer
	 * @Author lihao
	 * @Description 批量删除菜单信息
	 * @Date 2020/11/16 14:02
	 * @Param [ids]
	 **/
	Integer removeListMenu(Long[] ids);
	
	/**
	 * @return java.util.List<com.jsy.community.entity.IndexMenuEntity>
	 * @Author lihao
	 * @Description 更多服务
	 * @Date 2020/11/16 14:02
	 * @Param []
	 **/
	List<FrontMenuVO> moreListMenu();
	
//	/**
//	 * @return java.util.List<com.jsy.community.vo.menu.FrontParentMenu>
//	 * @Author lihao
//	 * @Description 树形结构
//	 * @Date 2020/11/17 10:14
//	 * @Param []
//	 **/
//	List<FrontParentMenu> listAdminMenu(Long communityId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 新增父菜单
	 * @Date 2020/11/23 17:28
	 * @Param [frontMenuEntity]
	 **/
	Long addParentMenu(AppMenuEntity appMenuEntity);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 新增子菜单
	 * @Date 2020/11/23 17:32
	 * @Param [frontMenuEntity]
	 **/
	void addChildMenu(AppMenuEntity appMenuEntity);
}
