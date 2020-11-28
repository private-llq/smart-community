package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FrontMenuEntity;
import com.jsy.community.mapper.MenuMapper;
import com.jsy.community.vo.FrontChildMenu;
import com.jsy.community.vo.FrontParentMenu;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author lihao
 * @since 2020-11-14
 */
@DubboService(version = Const.version, group = Const.group)
@Slf4j
public class MenuServiceImpl extends ServiceImpl<MenuMapper, FrontMenuEntity> implements IMenuService {
	
	@Autowired
	private MenuMapper menuMapper;
	
	// TODO 首页展示菜单数量 暂定3个
	private final Integer INDEXMENUCOUNT = 3;
	
	@Override
	public List<FrontMenuEntity> listIndexMenu(Long communityId) {
		QueryWrapper<FrontMenuEntity> wrapper = new QueryWrapper<>();
		wrapper.ne("parent_id", 0).eq("community_id", communityId).eq("status", 0).last("limit " + INDEXMENUCOUNT);
		return menuMapper.selectList(wrapper);
	}
	
	@Override
	public List<FrontParentMenu> moreIndexMenu(Long communityId) {
		ArrayList<FrontParentMenu> list = new ArrayList<>();
		// 1. 查询所有一级分类
		QueryWrapper<FrontMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("community_id", communityId);
		queryWrapper.eq("parent_id", 0);
		List<FrontMenuEntity> parentList = menuMapper.selectList(queryWrapper);
		
		//2. 查询所有二级分类
		QueryWrapper<FrontMenuEntity> wrapper = new QueryWrapper<>();
		wrapper.ne("parent_id", 0);
		List<FrontMenuEntity> childMenu = menuMapper.selectList(wrapper);
		
		//3. 封装数据
		for (FrontMenuEntity frontMenuEntity : parentList) {
			FrontParentMenu parentMenu = new FrontParentMenu();
			BeanUtils.copyProperties(frontMenuEntity, parentMenu);
			list.add(parentMenu);
			
			ArrayList<FrontChildMenu> childMenus = new ArrayList<>();
			for (FrontMenuEntity menu : childMenu) {
				if (menu.getParentId().equals(frontMenuEntity.getId())) {
					FrontChildMenu frontChildMenu = new FrontChildMenu();
					BeanUtils.copyProperties(menu, frontChildMenu);
					childMenus.add(frontChildMenu);
				}
			}
			parentMenu.setChildMenus(childMenus);
		}
		return list;
	}
}
