package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.FrontMenuEntity;
import com.jsy.community.vo.menu1.FrontParentMenu;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-11-14
 */
public interface IMenuService extends IService<FrontMenuEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.FrontMenuEntity>
	 * @Author lihao
	 * @Description 查询首页展示的菜单选项
	 * @Date 2020/11/14 21:17
	 * @Param [number]
	 **/
	List<FrontMenuEntity> listIndexMenu(Long communityId);
	
	/**
	 * @return java.util.List<com.jsy.community.vo.menu1.FrontParentMenu>
	 * @Author lihao
	 * @Description 树形结构
	 * @Date 2020/11/17 10:14
	 * @Param []
	 **/
	List<FrontParentMenu> listMenu();
}
