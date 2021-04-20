package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAppMenuService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AppMenuEntity;
import com.jsy.community.mapper.AppMenuMapper;
import com.jsy.community.vo.menu.AppMenuVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

//	@Override
//	public List<AppMenuEntity> listParentMenu() {
//		QueryWrapper<AppMenuEntity> wrapper = new QueryWrapper<>();
//		wrapper.eq("parent_id", PARENT_MARK);
//		return appMenuMapper.selectList(wrapper);
//	}
//
//	@Override
//	public List<AppMenuEntity> listChildMenuById(Long parentId) {
//		QueryWrapper<AppMenuEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("parent_id", parentId);
//		return appMenuMapper.selectList(queryWrapper);
//	}

//	@Override
//	public List<FrontParentMenu> listAdminMenu(Long communityId) {
//		// 1. 根据社区id查询中间表中所有菜单id
//		List<Long> menuIds = appMenuMapper.getMenuIdByCommunityId(communityId);
//		if (CollectionUtils.isEmpty(menuIds)) {
//			return null;
//		}
//
//		// 2. 根据菜单id查询出所有菜单
//		List<AppMenuEntity> appMenuEntities = appMenuMapper.selectBatchIds(menuIds);
//		List<AppMenuEntity> parentList = new ArrayList<AppMenuEntity>();
//		List<AppMenuEntity> childList = new ArrayList<AppMenuEntity>();
//
//		for (AppMenuEntity appMenuEntity : appMenuEntities) {
//			if ((PARENT_MARK).equals(appMenuEntity.getParentId())) {
//				parentList.add(appMenuEntity);
//			} else {
//				childList.add(appMenuEntity);
//			}
//		}
//
//		// 3. 封装菜单信息
//		List<FrontParentMenu> parentMenus =new ArrayList<>();
//		for (AppMenuEntity parent : parentList) {
//
//
//			List<FrontChildMenu> childMenus = new ArrayList<>();
//			for (AppMenuEntity child : childList) {
//				if (child.getParentId().equals(parent.getId())) {
//					FrontChildMenu frontChildMenu = new FrontChildMenu();
//					BeanUtils.copyProperties(child,frontChildMenu);
//					childMenus.add(frontChildMenu);
//				}
//			}
//
//			FrontParentMenu frontParentMenu = new FrontParentMenu();
//			frontParentMenu.setChildMenus(childMenus);
//			BeanUtils.copyProperties(parent,frontParentMenu);
//
//			parentMenus.add(frontParentMenu);
//		}
//		return parentMenus;
//	}
//
//	@Override
//	public void addParentMenu(AppMenuEntity appMenuEntity,Long communityId) {
//		if (!(PARENT_MARK).equals(appMenuEntity.getParentId())) {
//			throw new PropertyException("您添加的不是父菜单");
//		}
//
//		AppMenuEntity menuEntity = appMenuMapper.selectById(appMenuEntity.getId());
//		if (menuEntity==null) {
//			throw new PropertyException("您添加的数据不存在，请重新选择");
//		}
//
//		if (!(PARENT_MARK).equals(menuEntity.getParentId())) {
//			throw new PropertyException("您添加的不是父菜单");
//		}
//
//		// 保存到中间表
//		appMenuMapper.addParentMenu(appMenuEntity.getId(),communityId);
//	}
//
//	@Override
//	public void addChildMenu(AppMenuEntity appMenuEntity, Long communityId) {
//		if (PARENT_MARK.equals(appMenuEntity.getParentId())) {
//			throw new PropertyException("您添加的不是子菜单");
//		}
//
//		AppMenuEntity menuEntity = appMenuMapper.selectById(appMenuEntity.getId());
//		if (menuEntity==null) {
//			throw new PropertyException("您添加的数据不存在，请重新选择");
//		}
//
//		// 判断添加的菜单是否为父菜单
//		Long parentId = menuEntity.getParentId();
//		if (PARENT_MARK.equals(parentId)||!menuEntity.getMenuName().equals(appMenuEntity.getMenuName())) {
//			throw new PropertyException("您添加的不是子菜单");
//		}
//
//		// 保存到中间表
//		appMenuMapper.addParentMenu(appMenuEntity.getId(),communityId);
//	}
//
//	@Override
//	public void removeMenu(Long id, Long communityId) {
//		// 判断该菜单是不是一个父菜单
//		AppMenuEntity appMenuEntity = appMenuMapper.selectById(id);
//		if (!PARENT_MARK.equals(appMenuEntity.getParentId())) {
//			appMenuMapper.deleteById(id);
//		}
//
//		QueryWrapper<AppMenuEntity> wrapper = new QueryWrapper<>();
//		wrapper.eq("parent_id",id);
//		List<AppMenuEntity> appMenuEntities = appMenuMapper.selectList(wrapper);
//		if (appMenuEntities.size()>0) {
//			throw new PropertyException("请先删除所有子菜单");
//		}
//	}
	
	@Override
	public List<AppMenuEntity> listMenu(Long communityId) {
		// 1. 查询所有APP支持菜单
		List<AppMenuEntity> allAppList = appMenuMapper.selectList(null);
		
		for (AppMenuEntity appMenuEntity : allAppList) {
			appMenuEntity.setSort(99L);
			
			//2. 查询中间表
			Map<String, Long> menu = appMenuMapper.getMiddleMenu(appMenuEntity.getId(),communityId);
			if (menu != null) {
				// 说明该菜单存在于中间表，即它被勾选了
				appMenuEntity.setSort(menu.get("sort"));
				appMenuEntity.setChecked(1);
			} else {
				// 说明该菜单没存在于中间表, 即她没有被勾选了
				appMenuEntity.setChecked(0);
			}
			
		}
		
		//3. 对集合进行排序
		Collections.sort(allAppList, new Comparator<AppMenuEntity>() {
			@Override
			public int compare(AppMenuEntity o1, AppMenuEntity o2) {
				Long sort = o1.getSort();
				Long sort1 = o2.getSort();
				if (sort.equals(sort1) ) {
					return 0;
				}else {
					// 从小到大
					return sort > sort1 ? 1 : -1 ;
				}
			}
		});
		
		
//      todo 先不删 等前端联调测试完毕了来
//		// 2. 查询中间表
//		List<Map<String, Long>> idsAdnSort = appMenuMapper.listMenuId(communityId);
//		if (CollectionUtils.isEmpty(idsAdnSort)) {
//			return new ArrayList<AppMenuEntity>();
//		}
//		List<AppMenuEntity> appMenuEntityList = new ArrayList<>();
//		for (Map<String, Long> stringLongMap : idsAdnSort) {
//			Long id = stringLongMap.get("menu_id");
//			Long sort = stringLongMap.get("sort");
//			AppMenuEntity appMenuEntity = appMenuMapper.selectById(id);
//			appMenuEntity.setSort(sort);
//			appMenuEntityList.add(appMenuEntity);
//		}
//		Collections.sort(appMenuEntityList, new Comparator<AppMenuEntity>() {
//			@Override
//			public int compare(AppMenuEntity o1, AppMenuEntity o2) {
//				Long sort = o1.getSort();
//				Long sort1 = o2.getSort();
//				if (sort.equals(sort1) ) {
//					return 0;
//				}else {
//					// 从小到大
//					return sort > sort1 ? 1 : -1 ;
//				}
//			}
//		});
//
//
		
		
		return allAppList;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void appMenu(List<AppMenuVO> appMenuVOS) {
		// 判断添加的菜单id是否存在
		for (AppMenuVO appMenuEntity : appMenuVOS) {
			Long id = appMenuEntity.getMenuId();
			AppMenuEntity menuEntity = appMenuMapper.selectById(id);
			if (menuEntity == null) {
				throw new PropertyException("您选择的菜单不存在或不正确,请重新添加");
			}
			
			// 删除该社区原本有的中间表数据
			// 因为这个功能 新增和编辑是同一个  不管他是新增还是编辑  把他原本的清空
			appMenuMapper.deleteMiddleMenu(appMenuEntity.getCommunityId());
		}
		
		// 添加菜单到中间表
		appMenuMapper.insertMiddleMenu(appMenuVOS);
	}
}
