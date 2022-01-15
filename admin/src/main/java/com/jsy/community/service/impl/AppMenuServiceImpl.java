//package com.jsy.community.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.jsy.community.constant.UploadRedisConst;
//import com.jsy.community.entity.AppMenuEntity;
//import com.jsy.community.exception.JSYException;
//import com.jsy.community.mapper.AppMenuMapper;
//import com.jsy.community.service.IAppMenuService;
//import com.jsy.community.utils.SnowFlake;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeanUtils;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * <p>
// * 菜单 服务实现类
// * </p>
// *
// * @author lihao
// * @since 2020-11-24
// */
//@Service
//@Slf4j
//public class AppMenuServiceImpl extends ServiceImpl<AppMenuMapper, AppMenuEntity> implements IAppMenuService {
//
//	@Resource
//	private AppMenuMapper appMenuMapper;
//
//	@Resource
//	private StringRedisTemplate stringRedisTemplate;
//
//	/**
//	 *  父菜单的parentId
//	 **/
//	private static final Long PARENT_MARK = 0L;
//
//
//	@Override
//	public List<FrontParentMenu> listAdminMenu() {
//		ArrayList<FrontParentMenu> list = new ArrayList<>();
//		// 1. 查询所有一级分类
//		QueryWrapper<AppMenuEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("parent_id", PARENT_MARK);
//		List<AppMenuEntity> parentList = appMenuMapper.selectList(queryWrapper);
//
//		//2. 查询所有二级分类
//		QueryWrapper<AppMenuEntity> wrapper = new QueryWrapper<>();
//		wrapper.ne("parent_id", PARENT_MARK);
//		List<AppMenuEntity> childMenu = appMenuMapper.selectList(wrapper);
//		if (CollectionUtils.isEmpty(childMenu)) {
//			return null;
//		}
//
//		//3. 封装数据
//		if (!CollectionUtils.isEmpty(parentList)) {
//			for (AppMenuEntity frontMenuEntity : parentList) {
//				FrontParentMenu parentMenu = new FrontParentMenu();
//				BeanUtils.copyProperties(frontMenuEntity, parentMenu);
//				list.add(parentMenu);
//
//				ArrayList<FrontChildMenu> childMenus = new ArrayList<>();
//				if (!CollectionUtils.isEmpty(childMenu)) {
//					for (AppMenuEntity menu : childMenu) {
//						if (menu.getParentId().equals( frontMenuEntity.getUserId())) {
//							FrontChildMenu frontChildMenu = new FrontChildMenu();
//							BeanUtils.copyProperties(menu, frontChildMenu);
//							childMenus.add(frontChildMenu);
//						}
//					}
//				}
//				parentMenu.setChildMenus(childMenus);
//			}
//		}
//		return list;
//	}
//
//	@Override
//	public void insertAdminMenu(AppMenuEntity adminMenu) {
//		if (!adminMenu.getParentId().equals(PARENT_MARK)) {
//			throw new JSYException("您添加的不是父菜单");
//		}
//		adminMenu.setId(SnowFlake.nextId());
//		adminMenu.setDaytimeIcon(null);
//		adminMenu.setNightIcon(null);
//		adminMenu.setPath(null);
//		appMenuMapper.insert(adminMenu);
//	}
//
//	@Override
//	public void removeAdminMenu(Long id) {
//		QueryWrapper<AppMenuEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("parent_id", id);
//		List<AppMenuEntity> list = appMenuMapper.selectList(queryWrapper);
//		if (!CollectionUtils.isEmpty(list)) {
//			log.debug("删除的菜单id：{}", id);
//			throw new JSYException("请先删除子菜单");
//		}
//		appMenuMapper.deleteById(id);
//	}
//
//	@Override
//	public void insertChildMenu(AppMenuEntity adminMenu) {
//		if (adminMenu.getParentId().equals(PARENT_MARK)) {
//			throw new JSYException("您添加的不是子菜单");
//		}
//		QueryWrapper<AppMenuEntity> wrapper = new QueryWrapper<>();
//		wrapper.eq("id",adminMenu.getParentId());
//		AppMenuEntity appMenuEntity = appMenuMapper.selectOne(wrapper);
//		if (appMenuEntity==null) {
//			throw new JSYException("您所添加的子菜单所对应的父菜单不存在");
//		}
//		String daytimeIcon = adminMenu.getDaytimeIcon();
//		if (!StringUtils.isEmpty(daytimeIcon)) {
//			stringRedisTemplate.opsForSet().add(UploadRedisConst.REPAIR_COMMENT_IMG_ALL, daytimeIcon);
//		}
//		String nightIcon = adminMenu.getNightIcon();
//		if (!StringUtils.isEmpty(nightIcon)) {
//			stringRedisTemplate.opsForSet().add(UploadRedisConst.REPAIR_COMMENT_IMG_ALL, nightIcon);
//		}
//		adminMenu.setId(SnowFlake.nextId());
//		appMenuMapper.insert(adminMenu);
//	}
//
//	@Override
//	public void updateAdminMenu(AppMenuEntity adminMenu) {
//		if (!adminMenu.getParentId().equals(PARENT_MARK)) {
//			log.debug("修改的菜单id：{}",adminMenu.getParentId());
//			throw new JSYException("您修改的不是父菜单");
//		}
//		AppMenuEntity appMenuEntity = appMenuMapper.selectById(adminMenu.getUserId());
//		if (!appMenuEntity.getParentId().equals(PARENT_MARK)) {
//			throw new JSYException("您修改的不是父菜单");
//		}
//		adminMenu.setDaytimeIcon(null);
//		adminMenu.setNightIcon(null);
//		adminMenu.setPath(null);
//		appMenuMapper.updateById(adminMenu);
//	}
//}
