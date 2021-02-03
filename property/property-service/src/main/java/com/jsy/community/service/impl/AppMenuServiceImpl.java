package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAppMenuService;
import com.jsy.community.api.PropertyException;
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
@DubboService(version = Const.version, group = Const.group_property)
public class AppMenuServiceImpl extends ServiceImpl<AppMenuMapper, AppMenuEntity> implements IAppMenuService {
	
	@Autowired
	private AppMenuMapper appMenuMapper;
	
	/**
	 * 父菜单的parentId
	 **/
	private static final Long PARENT_MARK = 0L;
	
	@Override
	public List<AppMenuEntity> listParentMenu() {
		QueryWrapper<AppMenuEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("parent_id", PARENT_MARK);
		return appMenuMapper.selectList(wrapper);
	}
	
	@Override
	public List<AppMenuEntity> listChildMenuById(Long parentId) {
		QueryWrapper<AppMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("parent_id", parentId);
		return appMenuMapper.selectList(queryWrapper);
	}
	
	@Override
	public List<FrontParentMenu> listAdminMenu(Long communityId) {
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
	
	@Override
	public void addParentMenu(AppMenuEntity appMenuEntity,Long communityId) {
		if (!(PARENT_MARK).equals(appMenuEntity.getParentId())) {
			throw new PropertyException("您添加的不是父菜单");
		}
		
		AppMenuEntity menuEntity = appMenuMapper.selectById(appMenuEntity.getId());
		if (menuEntity==null) {
			throw new PropertyException("您添加的数据不存在，请重新选择");
		}
		
		if (!(PARENT_MARK).equals(menuEntity.getParentId())) {
			throw new PropertyException("您添加的不是父菜单");
		}
		
		// 保存到中间表
		appMenuMapper.addParentMenu(appMenuEntity.getId(),communityId);
	}
	
	@Override
	public void addChildMenu(AppMenuEntity appMenuEntity, Long communityId) {
		if (PARENT_MARK.equals(appMenuEntity.getParentId())) {
			throw new PropertyException("您添加的不是子菜单");
		}
		
		AppMenuEntity menuEntity = appMenuMapper.selectById(appMenuEntity.getId());
		if (menuEntity==null) {
			throw new PropertyException("您添加的数据不存在，请重新选择");
		}
		
		// 判断添加的菜单是否为父菜单
		Long parentId = menuEntity.getParentId();
		if (PARENT_MARK.equals(parentId)) {
			throw new PropertyException("您添加的不是子菜单");
		}
		
		// 保存到中间表
		appMenuMapper.addParentMenu(appMenuEntity.getId(),communityId);
		
	}
	
	@Override
	public void removeMenu(Long id, Long communityId) {
		// 判断该菜单是不是一个父菜单
		AppMenuEntity appMenuEntity = appMenuMapper.selectById(id);
		if (!PARENT_MARK.equals(appMenuEntity.getParentId())) {
			appMenuMapper.deleteById(id);
		}
		
		QueryWrapper<AppMenuEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("parent_id",id);
		List<AppMenuEntity> appMenuEntities = appMenuMapper.selectList(wrapper);
		if (appMenuEntities.size()>0) {
			throw new PropertyException("请先删除所有子菜单");
		}
	}
}
