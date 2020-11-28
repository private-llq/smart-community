package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.vo.FrontMenuVO;
import com.jsy.community.entity.AdminMenuEntity;
import com.jsy.community.entity.FrontMenuEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.MenuMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.vo.menu.FrontChildMenu;
import com.jsy.community.vo.menu.FrontParentMenu;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	// TODO 首页展示菜单数量 暂定5个
	private final Integer INDEXMENUCOUNT = 3;
	
	@Override
	public Integer saveMenu(FrontMenuEntity menuEntity) {
		return menuMapper.insert(menuEntity);
	}
	
	@Override
	public Integer updateMenu(Long id, FrontMenuVO frontMenuVO) {
		String parentName = frontMenuVO.getParentName();
		QueryWrapper<FrontMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("menu_name", parentName);
		FrontMenuEntity menuEntity = menuMapper.selectOne(queryWrapper);
		
		FrontMenuEntity frontMenuEntity = new FrontMenuEntity();
		BeanUtils.copyProperties(frontMenuVO, frontMenuEntity);
		frontMenuEntity.setId(id);
		
		if (menuEntity != null) {
			frontMenuEntity.setParentId(menuEntity.getId());
			return menuMapper.updateById(frontMenuEntity);
		}
		return menuMapper.updateById(frontMenuEntity);
	}
	
	@Override
	public List<FrontMenuVO> listFrontMenu(BaseQO<FrontMenuEntity> baseQO) {
		Page<FrontMenuEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
		QueryWrapper<FrontMenuEntity> wrapper = new QueryWrapper<>();
		String menuName = baseQO.getQuery().getMenuName();
		String description = baseQO.getQuery().getDescr();
		if (!StringUtils.isEmpty(menuName)) {
			wrapper.like("menu_name", menuName);
		}
		if (!StringUtils.isEmpty(description)) {
			wrapper.like("descr", description);
		}
		menuMapper.selectPage(page, wrapper);
		
		
		ArrayList<FrontMenuVO> frontMenuVOS = new ArrayList<>();
		
		List<FrontMenuEntity> records = page.getRecords();
		for (FrontMenuEntity record : records) {
			FrontMenuVO menuVo = new FrontMenuVO();
			BeanUtils.copyProperties(record, menuVo);
			Long parentId = record.getParentId();
			QueryWrapper<FrontMenuEntity> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("id", parentId);
			FrontMenuEntity menuEntity = menuMapper.selectOne(queryWrapper);
			if (menuEntity != null) {
				menuVo.setParentName(menuEntity.getMenuName());
			}
			frontMenuVOS.add(menuVo);
		}
		return frontMenuVOS;
	}
	
	@Override
	public List<FrontMenuEntity> listIndexMenu(Long communityId) {
		QueryWrapper<FrontMenuEntity> wrapper = new QueryWrapper<>();
//		wrapper.ne("parent_id", 0).eq("community_id",communityId).orderByAsc("sort").last("limit " + INDEXMENUCOUNT);
		wrapper.ne("parent_id", 0).eq("community_id", communityId).eq("status", 0).last("limit " + INDEXMENUCOUNT);
		return menuMapper.selectList(wrapper);
	}
	
	@Override
	public Integer removeMenu(Long id) {
		QueryWrapper<FrontMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("parent_id", id);
		List<FrontMenuEntity> list = menuMapper.selectList(queryWrapper);
		if (!CollectionUtils.isEmpty(list)) {
			log.debug("删除的菜单id：{}", id);
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请先删除子菜单");
		}
		return menuMapper.deleteById(id);
	}
	
	@Override
	public List<FrontMenuEntity> listParentMenu() {
		QueryWrapper<FrontMenuEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("parent_id", 0L);
		return menuMapper.selectList(wrapper);
	}
	
	@Override
	public FrontMenuVO getMenuById(Long id) {
		FrontMenuEntity menuEntity = menuMapper.selectById(id);
		FrontMenuVO menuVo = new FrontMenuVO();
		BeanUtils.copyProperties(menuEntity, menuVo);

//		Long parentId = menuEntity.getParentId();
//		FrontMenuEntity entity = this.common(parentId);
//		if (entity != null) {
//			menuVo.setParentName(entity.getMenuName());
//			log.info("父菜单名：{}" + menuVo.getParentName());
//			return menuVo;
//		}
//		log.info("父菜单名：{}" + menuVo.getParentName());
//		menuVo.setParentName("");
		return menuVo;
	}
	
	@Override
	public Integer removeListMenu(Long[] ids) {
		for (Long id : ids) {
			FrontMenuEntity frontMenuEntity = menuMapper.selectById(id);
			if (frontMenuEntity.getParentId() == 0) {
				log.debug("删除的菜单id：{}", frontMenuEntity.getId());
				throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请先删除子菜单");
			}
		}
		return menuMapper.deleteBatchIds(Arrays.asList(ids));
	}
	
	@Override
	public List<FrontMenuVO> moreListMenu() {
		List<FrontMenuEntity> list = menuMapper.selectList(null);
		
		ArrayList<FrontMenuVO> arrayList = new ArrayList<>();
		for (FrontMenuEntity frontMenuEntity : list) {
			FrontMenuVO menuVo = new FrontMenuVO();
			BeanUtils.copyProperties(frontMenuEntity, menuVo);
			Long parentId = frontMenuEntity.getParentId();
			FrontMenuEntity menuEntity = this.common(parentId);
			if (menuEntity != null) {
				menuVo.setParentName(menuEntity.getMenuName());
			}
			arrayList.add(menuVo);
		}
		return arrayList;
	}
	
	@Override
	public List<FrontParentMenu> listMenu(Long communityId) {
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
	
	@Override
	public Long addParentMenu(AdminMenuEntity adminMenuEntity) {
		FrontMenuEntity frontMenuEntity = new FrontMenuEntity();
		BeanUtils.copyProperties(adminMenuEntity, frontMenuEntity);
		menuMapper.insert(frontMenuEntity);
		return frontMenuEntity.getId();//返回新增后数据的id
	}
	
	@Override
	public void addChildMenu(AdminMenuEntity adminMenuEntity) {
		FrontMenuEntity entity = new FrontMenuEntity();
		BeanUtils.copyProperties(adminMenuEntity, entity);
		menuMapper.insert(entity);
		
	}
	
	private FrontMenuEntity common(Long parentId) {
		QueryWrapper<FrontMenuEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("id", parentId);
		return menuMapper.selectOne(wrapper);
	}
}
