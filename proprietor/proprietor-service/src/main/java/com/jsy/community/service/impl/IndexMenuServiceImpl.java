package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IIndexMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.IndexMenuEntity;
import com.jsy.community.mapper.IndexMenuMapper;
import com.jsy.community.vo.menu.FrontChildMenu;
import com.jsy.community.vo.menu.FrontParentMenu;
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
@DubboService(version = Const.version, group = Const.group_proprietor)
@Slf4j
public class IndexMenuServiceImpl extends ServiceImpl<IndexMenuMapper, IndexMenuEntity> implements IIndexMenuService {
	
	@Autowired
	private IndexMenuMapper indexMenuMapper;
	
	// TODO 首页展示菜单数量 暂定3个
//	@Value(value = "${jsy.menu}")
	private Integer INDEXMENUCOUNT = 3;
	
	@Override
	public List<IndexMenuEntity> listIndexMenu(Long communityId) {
		QueryWrapper<IndexMenuEntity> wrapper = new QueryWrapper<>();
		wrapper.ne("parent_id", 0).eq("community_id", communityId).eq("status", 0).last("limit " + INDEXMENUCOUNT);
		return indexMenuMapper.selectList(wrapper);
	}
	
	@Override
	public List<FrontParentMenu> moreIndexMenu(Long communityId) {
		ArrayList<FrontParentMenu> list = new ArrayList<>();
		// 1. 查询所有一级分类
		QueryWrapper<IndexMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("community_id", communityId);
		queryWrapper.eq("parent_id", 0);
		List<IndexMenuEntity> parentList = indexMenuMapper.selectList(queryWrapper);
		
		//2. 查询所有二级分类
		QueryWrapper<IndexMenuEntity> wrapper = new QueryWrapper<>();
		wrapper.ne("parent_id", 0);
		List<IndexMenuEntity> childMenu = indexMenuMapper.selectList(wrapper);
		
		//3. 封装数据
		for (IndexMenuEntity indexMenuEntity : parentList) {
			FrontParentMenu parentMenu = new FrontParentMenu();
			BeanUtils.copyProperties(indexMenuEntity, parentMenu);
			list.add(parentMenu);
			
			ArrayList<FrontChildMenu> childMenus = new ArrayList<>();
			for (IndexMenuEntity menu : childMenu) {
				if (menu.getParentId().equals(indexMenuEntity.getId())) {
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
