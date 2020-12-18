package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAppMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AppMenuEntity;
import com.jsy.community.mapper.AppMenuMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-11-24
 */
@DubboService(version = Const.version, group = Const.group_property)
public class AppMenuServiceImpl extends ServiceImpl<AppMenuMapper, AppMenuEntity> implements IAppMenuService {
	
	@Autowired
	private AppMenuMapper appMenuMapper;
	
	@Override
	public List<AppMenuEntity> listParentMenu() {
		QueryWrapper<AppMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("parent_id",0);
		return appMenuMapper.selectList(queryWrapper);
	}
	
	@Override
	public List<AppMenuEntity> listChildMenu() {
		QueryWrapper<AppMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.ne("parent_id",0);
		return appMenuMapper.selectList(queryWrapper);
	}
	
	@Override
	public List<AppMenuEntity> listChildMenuById(Long parentId) {
		QueryWrapper<AppMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("parent_id",parentId);
		return appMenuMapper.selectList(queryWrapper);
	}
}
