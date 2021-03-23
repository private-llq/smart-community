package com.jsy.community.service.impl;

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
 * @author lihao
 * @since 2020-11-24
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
public class AppMenuServiceImpl extends ServiceImpl<AppMenuMapper, AppMenuEntity> implements IAppMenuService {
	
	@Autowired
	private AppMenuMapper appMenuMapper;
	
	/**
	 * 首页展示的菜单数
	 **/
	private static final Integer INDEX_MENU_COUNT = 3;
	
	@Override
	public List<AppMenuEntity> listIndexMenu(Long communityId) {
		// 根据社区id查询中间表关于该社区的菜单id集合
		List<Long> menuIds = appMenuMapper.selectMenuIdByCommunityId(communityId,INDEX_MENU_COUNT);
		
		// TODO: 2021/2/5 如果该小区没有菜单，暂时也给他返回小区  原因：现阶段便于测试人员测试
		// 如果传入的小区没有菜单，则返回id为27839755849728L的小区菜单
		List<Long> testmenuIds = appMenuMapper.selectMenuIdByCommunityId(27839755849728L,INDEX_MENU_COUNT);
		if (menuIds==null) {
			menuIds = testmenuIds;
		}
		
		return appMenuMapper.selectBatchIds(menuIds);
		
	}
	
	@Override
	public List<AppMenuEntity>  moreIndexMenu(Long communityId) {
		// 根据社区id查询中间表关于该社区的菜单id集合
		List<Long> menuIds = appMenuMapper.selectMenuIdByCommunityId(communityId,50);
		
		// TODO: 2021/2/5 如果该小区没有菜单，暂时也给他返回小区  原因：现阶段便于测试人员测试
		// 如果传入的小区没有菜单，则返回id为27839755849728L的小区菜单
		List<Long> testmenuIds = appMenuMapper.selectMenuIdByCommunityId(27839755849728L,50);
		if (menuIds==null) {
			menuIds = testmenuIds;
		}
		
		return appMenuMapper.selectBatchIds(menuIds);
	}
}
