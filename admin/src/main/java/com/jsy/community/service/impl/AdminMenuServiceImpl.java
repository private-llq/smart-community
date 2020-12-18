package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.AppMenuEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.AdminMenuMapper;
import com.jsy.community.service.IAdminMenuService;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.menu.FrontChildMenu;
import com.jsy.community.vo.menu.FrontParentMenu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
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
@Service
@Slf4j
public class AdminMenuServiceImpl extends ServiceImpl<AdminMenuMapper, AppMenuEntity> implements IAdminMenuService {
	
	@Resource
	private AdminMenuMapper adminMenuMapper;
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	@Override
	public List<FrontParentMenu> listAdminMenu() {
		ArrayList<FrontParentMenu> list = new ArrayList<>();
		// 1. 查询所有一级分类
		QueryWrapper<AppMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("parent_id", 0);
		List<AppMenuEntity> parentList = adminMenuMapper.selectList(queryWrapper);
		if (CollectionUtils.isEmpty(parentList)) {
			throw new JSYException("该小区不存在");
		}
		
		//2. 查询所有二级分类
		QueryWrapper<AppMenuEntity> wrapper = new QueryWrapper<>();
		wrapper.ne("parent_id", 0);
		List<AppMenuEntity> childMenu = adminMenuMapper.selectList(wrapper);
		if (CollectionUtils.isEmpty(childMenu)) {
			return null;
		}
		
		//3. 封装数据
		for (AppMenuEntity frontMenuEntity : parentList) {
			FrontParentMenu parentMenu = new FrontParentMenu();
			BeanUtils.copyProperties(frontMenuEntity, parentMenu);
			list.add(parentMenu);
			
			ArrayList<FrontChildMenu> childMenus = new ArrayList<>();
			for (AppMenuEntity menu : childMenu) {
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
	public void insertAdminMenu(AppMenuEntity adminMenu) {
		if (!adminMenu.getParentId().equals(0L)) {
			throw new JSYException("您添加的不是父菜单");
		}
		adminMenu.setId(SnowFlake.nextId());
		adminMenuMapper.insert(adminMenu);
	}
	
	@Override
	public void removeAdminMenu(Long id) {
		QueryWrapper<AppMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("parent_id", id);
		List<AppMenuEntity> list = adminMenuMapper.selectList(queryWrapper);
		if (!CollectionUtils.isEmpty(list)) {
			log.debug("删除的菜单id：{}", id);
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请先删除子菜单");
		}
	}
	
	@Override
	public void insertChildMenu(AppMenuEntity adminMenu) {
		String icon = adminMenu.getIcon();// 图片地址
		if (!StringUtils.isEmpty(icon)) {
			stringRedisTemplate.opsForSet().add("menu_img_all", icon);// 最终上传时将图片地址再存入redis
		}
		adminMenu.setId(SnowFlake.nextId());
		adminMenuMapper.insert(adminMenu);
	}
}
