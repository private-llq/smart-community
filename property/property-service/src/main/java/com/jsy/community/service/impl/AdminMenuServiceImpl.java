package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAdminMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AdminMenuEntity;
import com.jsy.community.mapper.AdminMenuMapper;
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
@DubboService(version = Const.version, group = Const.group)
public class AdminMenuServiceImpl extends ServiceImpl<AdminMenuMapper, AdminMenuEntity> implements IAdminMenuService {
	
	@Autowired
	private AdminMenuMapper adminMenuMapper;
	
	@Override
	public List<AdminMenuEntity> listParentMenu() {
		QueryWrapper<AdminMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("parent_id",0);
		return adminMenuMapper.selectList(queryWrapper);
	}
	
	@Override
	public List<AdminMenuEntity> listChildMenu() {
		QueryWrapper<AdminMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.ne("parent_id",0);
		return adminMenuMapper.selectList(queryWrapper);
	}
	
	@Override
	public List<AdminMenuEntity> listChildMenuById(Long parentId) {
		QueryWrapper<AdminMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("parent_id",parentId);
		return adminMenuMapper.selectList(queryWrapper);
	}
}
