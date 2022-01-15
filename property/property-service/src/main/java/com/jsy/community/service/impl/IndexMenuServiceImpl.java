//package com.jsy.community.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.jsy.community.api.IIndexMenuService;
//import com.jsy.community.api.PropertyException;
//import com.jsy.community.constant.Const;
//import com.jsy.community.entity.AppMenuEntity;
//import com.jsy.community.entity.IndexMenuEntity;
//import com.jsy.community.exception.JSYError;
//import com.jsy.community.exception.JSYException;
//import com.jsy.community.mapper.AppMenuMapper;
//import com.jsy.community.mapper.IndexMenuMapper;
//import com.jsy.community.qo.BaseQO;
//import com.jsy.community.utils.SnowFlake;
//import com.jsy.community.vo.FrontMenuVO;
//import com.jsy.community.vo.menu.FrontChildMenu;
//import com.jsy.community.vo.menu.FrontParentMenu;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * <p>
// * 菜单 服务实现类
// * </p>
// *
// * @author lihao
// * @since 2020-11-14
// */
//@DubboService(version = Const.version, group = Const.group_property)
//@Slf4j
//public class IndexMenuServiceImpl extends ServiceImpl<IndexMenuMapper, IndexMenuEntity> implements IIndexMenuService {
//
//	@Autowired
//	private IndexMenuMapper indexMenuMapper;
//
//	@Autowired
//	private AppMenuMapper appMenuMapper;
//
//	private static final Integer INDEXMENUCOUNT = 3;
//
//	@Override
//	public Integer saveMenu(IndexMenuEntity menuEntity) {
//		return indexMenuMapper.insert(menuEntity);
//	}
//
//	@Override
//	public Integer updateMenu(Long id, FrontMenuVO frontMenuVO) {
//		String parentName = frontMenuVO.getParentName();
//		QueryWrapper<IndexMenuEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("menu_name", parentName);
//		IndexMenuEntity menuEntity = indexMenuMapper.selectOne(queryWrapper);
//
//		IndexMenuEntity indexMenuEntity = new IndexMenuEntity();
//		BeanUtils.copyProperties(frontMenuVO, indexMenuEntity);
//		indexMenuEntity.setId(id);
//
//		if (menuEntity != null) {
//			indexMenuEntity.setParentId(menuEntity.getUserId());
//			return indexMenuMapper.updateById(indexMenuEntity);
//		}
//		return indexMenuMapper.updateById(indexMenuEntity);
//	}
//
//	@Override
//	public List<FrontMenuVO> listFrontMenu(BaseQO<IndexMenuEntity> baseQO) {
//		Page<IndexMenuEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
//		QueryWrapper<IndexMenuEntity> wrapper = new QueryWrapper<>();
//		String menuName = baseQO.getQuery().getMenuName();
//		String description = baseQO.getQuery().getDescr();
//		if (!StringUtils.isEmpty(menuName)) {
//			wrapper.like("menu_name", menuName);
//		}
//		if (!StringUtils.isEmpty(description)) {
//			wrapper.like("descr", description);
//		}
//		indexMenuMapper.selectPage(page, wrapper);
//
//		ArrayList<FrontMenuVO> frontMenuVOS = new ArrayList<>();
//
//		List<IndexMenuEntity> records = page.getRecords();
//		for (IndexMenuEntity record : records) {
//			FrontMenuVO menuVo = new FrontMenuVO();
//			BeanUtils.copyProperties(record, menuVo);
//			Long parentId = record.getParentId();
//			QueryWrapper<IndexMenuEntity> queryWrapper = new QueryWrapper<>();
//			queryWrapper.eq("id", parentId);
//			IndexMenuEntity menuEntity = indexMenuMapper.selectOne(queryWrapper);
//			if (menuEntity != null) {
//				menuVo.setParentName(menuEntity.getMenuName());
//			}
//			frontMenuVOS.add(menuVo);
//		}
//		return frontMenuVOS;
//	}
//
//	@Override
//	public List<IndexMenuEntity> listIndexMenu(Long communityId) {
//		QueryWrapper<IndexMenuEntity> wrapper = new QueryWrapper<>();
//		wrapper.ne("parent_id", 0).eq("community_id", communityId)
//			.eq("status", 0).last("limit " + INDEXMENUCOUNT);
//		return indexMenuMapper.selectList(wrapper);
//	}
//
//	@Override
//	public Integer removeMenu(Long id) {
//		QueryWrapper<IndexMenuEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("parent_id", id);
//		List<IndexMenuEntity> list = indexMenuMapper.selectList(queryWrapper);
//		if (!CollectionUtils.isEmpty(list)) {
//			log.debug("删除的菜单id：{}", id);
//			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请先删除子菜单");
//		}
//		return indexMenuMapper.deleteById(id);
//	}
//
//	@Override
//	public List<IndexMenuEntity> listParentMenu() {
//		QueryWrapper<IndexMenuEntity> wrapper = new QueryWrapper<>();
//		wrapper.eq("parent_id", 0L);
//		return indexMenuMapper.selectList(wrapper);
//	}
//
//	@Override
//	public FrontMenuVO getMenuById(Long id) {
//		IndexMenuEntity menuEntity = indexMenuMapper.selectById(id);
//		FrontMenuVO menuVo = new FrontMenuVO();
//		BeanUtils.copyProperties(menuEntity, menuVo);
//		return menuVo;
//	}
//
//	@Override
//	public Integer removeListMenu(Long[] ids) {
//		for (Long id : ids) {
//			IndexMenuEntity indexMenuEntity = indexMenuMapper.selectById(id);
//			if (indexMenuEntity.getParentId() == 0) {
//				log.debug("删除的菜单id：{}", indexMenuEntity.getUserId());
//				throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请先删除子菜单");
//			}
//		}
//		return indexMenuMapper.deleteBatchIds(Arrays.asList(ids));
//	}
//
//	@Override
//	public List<FrontMenuVO> moreListMenu() {
//		List<IndexMenuEntity> list = indexMenuMapper.selectList(null);
//
//		ArrayList<FrontMenuVO> arrayList = new ArrayList<>();
//		for (IndexMenuEntity indexMenuEntity : list) {
//			FrontMenuVO menuVo = new FrontMenuVO();
//			BeanUtils.copyProperties(indexMenuEntity, menuVo);
//			Long parentId = indexMenuEntity.getParentId();
//			IndexMenuEntity menuEntity = this.common(parentId);
//			if (menuEntity != null) {
//				menuVo.setParentName(menuEntity.getMenuName());
//			}
//			arrayList.add(menuVo);
//		}
//		return arrayList;
//	}
//
//	@Override
//	public List<FrontParentMenu> listAdminMenu(Long communityId) {
//		ArrayList<FrontParentMenu> list = new ArrayList<>();
//		// 1. 查询所有一级分类
//		QueryWrapper<IndexMenuEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("community_id", communityId);
//		queryWrapper.eq("parent_id", 0);
//		List<IndexMenuEntity> parentList = indexMenuMapper.selectList(queryWrapper);
//		if (CollectionUtils.isEmpty(parentList)) {
//			throw new PropertyException("该小区不存在");
//		}
//
//		//2. 查询所有二级分类
//		QueryWrapper<IndexMenuEntity> wrapper = new QueryWrapper<>();
//		wrapper.ne("parent_id", 0);
//		List<IndexMenuEntity> childMenu = indexMenuMapper.selectList(wrapper);
//		if (CollectionUtils.isEmpty(childMenu)) {
//			return null;
//		}
//
//		//3. 封装数据
//		for (IndexMenuEntity indexMenuEntity : parentList) {
//			FrontParentMenu parentMenu = new FrontParentMenu();
//			BeanUtils.copyProperties(indexMenuEntity, parentMenu);
//			list.add(parentMenu);
//
//			ArrayList<FrontChildMenu> childMenus = new ArrayList<>();
//			for (IndexMenuEntity menu : childMenu) {
//				if (menu.getParentId().equals(indexMenuEntity.getUserId())) {
//					FrontChildMenu frontChildMenu = new FrontChildMenu();
//					BeanUtils.copyProperties(menu, frontChildMenu);
//					childMenus.add(frontChildMenu);
//				}
//			}
//			parentMenu.setChildMenus(childMenus);
//		}
//		return list;
//	}
//
////	@Override
////	public Long addParentMenu(AppMenuEntity appMenuEntity) {
////		IndexMenuEntity indexMenuEntity = new IndexMenuEntity();
////		QueryWrapper<AppMenuEntity> queryWrapper = new QueryWrapper<>();
////		queryWrapper.eq("parent_id", 0);
////		List<AppMenuEntity> list = appMenuMapper.selectList(queryWrapper);
////		List<String> strings = new ArrayList<>(); // 用于存储父菜单名
////		for (AppMenuEntity menuEntity : list) {
////			String menuName = menuEntity.getMenuName();
////			strings.add(menuName);
////		}
////		if (!strings.contains(appMenuEntity.getMenuName())||appMenuEntity.getParentId()!=0) {
////			throw new PropertyException("您添加的不是父菜单");
////		}
////		BeanUtils.copyProperties(appMenuEntity, indexMenuEntity);
////		indexMenuEntity.setId(SnowFlake.nextId());
////		indexMenuMapper.insert(indexMenuEntity);
////		return indexMenuEntity.getUserId();//返回新增后数据的id
////	}
//
////	@Override
////	public void addChildMenu(AppMenuEntity appMenuEntity) {
////		IndexMenuEntity entity = new IndexMenuEntity();
////		QueryWrapper<AppMenuEntity> queryWrapper = new QueryWrapper<>();
////		queryWrapper.ne("parent_id", 0);
////		List<AppMenuEntity> list = appMenuMapper.selectList(queryWrapper);
////		List<String> strings = new ArrayList<>(); // 用于存储子菜单名
////		List<String> stringList = new ArrayList<>();// 用于存储子菜单跳转路径
////		for (AppMenuEntity menuEntity : list) {
////			String path = menuEntity.getPath();
////			stringList.add(path);
////			String menuName = menuEntity.getMenuName();
////			strings.add(menuName);
////		}
////		if (!strings.contains(appMenuEntity.getMenuName())||!stringList.contains(appMenuEntity.getPath())||appMenuEntity.getParentId()==0) {
////			throw new PropertyException("您添加的不是子菜单");
////		}
////		BeanUtils.copyProperties(appMenuEntity, entity);
////		entity.setId(SnowFlake.nextId());
////		indexMenuMapper.insert(entity);
////	}
//
//	private IndexMenuEntity common(Long parentId) {
//		QueryWrapper<IndexMenuEntity> wrapper = new QueryWrapper<>();
//		wrapper.eq("id", parentId);
//		return indexMenuMapper.selectOne(wrapper);
//	}
//}
