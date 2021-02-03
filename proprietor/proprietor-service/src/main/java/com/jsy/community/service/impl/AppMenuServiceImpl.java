package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAppMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AppMenuEntity;
import com.jsy.community.mapper.AppMenuMapper;
import com.jsy.community.vo.menu.FrontChildMenu;
import com.jsy.community.vo.menu.FrontParentMenu;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class AppMenuServiceImpl extends ServiceImpl<AppMenuMapper, AppMenuEntity> implements IAppMenuService {
	
	@Autowired
	private AppMenuMapper appMenuMapper;
	
	/**
	 * 首页展示的菜单数
	 **/
	private static final Integer INDEX_MENU_COUNT = 3;
	
	/**
	 * 父菜单的parentId
	 **/
	private static final Long PARENT_MARK = 0L;
	
	@Override
	public List<AppMenuEntity> listIndexMenu(Long communityId) {
		// 根据社区id查询中间表关于该社区的菜单id集合
		List<Long> menuIds = appMenuMapper.selectMenuIdByCommunityId(communityId);
		
		// 根据菜单id查询所有菜单信息
		List<AppMenuEntity> list = appMenuMapper.selectBatchIds(menuIds);
		
		List<AppMenuEntity> menuEntityList = new ArrayList<>();
		for (AppMenuEntity appMenuEntity : list) {
			if (PARENT_MARK.equals(appMenuEntity.getParentId())) {
				continue;
			}
			menuEntityList.add(appMenuEntity);
			if (menuEntityList.size()==3) {
				break;
			}
		}
		return menuEntityList;
	}
	
	@Override
	public List<FrontParentMenu> moreIndexMenu(Long communityId) {
		// 1. 根据社区id查询中间表中所有菜单id
		List<Long> menuIds = appMenuMapper.getMenuIdByCommunityId(communityId);
		if (CollectionUtils.isEmpty(menuIds)) {
			return null;
		}
		
		// 2. 根据菜单id查询出所有菜单
		List<AppMenuEntity> appMenuEntities = appMenuMapper.selectBatchIds(menuIds);
		List<AppMenuEntity> parentList = new ArrayList<AppMenuEntity>();
		List<AppMenuEntity> childList = new ArrayList<AppMenuEntity>();
		
		for (AppMenuEntity appMenuEntity : appMenuEntities) {
			if ((PARENT_MARK).equals(appMenuEntity.getParentId())) {
				parentList.add(appMenuEntity);
			} else {
				childList.add(appMenuEntity);
			}
		}
		
		// 3. 封装菜单信息
		List<FrontParentMenu> parentMenus =new ArrayList<>();
		for (AppMenuEntity parent : parentList) {
			
			
			List<FrontChildMenu> childMenus = new ArrayList<>();
			for (AppMenuEntity child : childList) {
				if (child.getParentId().equals(parent.getId())) {
					FrontChildMenu frontChildMenu = new FrontChildMenu();
					BeanUtils.copyProperties(child,frontChildMenu);
					childMenus.add(frontChildMenu);
				}
			}
			
			FrontParentMenu frontParentMenu = new FrontParentMenu();
			frontParentMenu.setChildMenus(childMenus);
			BeanUtils.copyProperties(parent,frontParentMenu);
			
			parentMenus.add(frontParentMenu);
		}
		return parentMenus;
	}
}
