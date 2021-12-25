package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAppMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AppMenuEntity;
import com.jsy.community.mapper.AppMenuMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

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
	 * 首页展示的菜单数   本来原先需求是说每个社区首页展示的菜单数可有物业控制(可以有1个2个3个...)   最新产品暂定的需求：每个社区首页最多有3个菜单  其中有个菜单是固定的(我的房屋) pS:应该在社区入驻的同事为其添加一条菜单(我的房屋)   应该是写在大后台的
	 * 菜单可以考虑做缓存  暂时没做了 因为需求变得快   之前原本我是做了缓存的
	 **/
	private static final Integer INDEX_MENU_COUNT = 3;
	
	@Override
	public List<AppMenuEntity> listIndexMenu(Long communityId) {
		// 根据社区id查询中间表t_menu_community关于该社区的菜单id集合
		List<Long> menuIds = appMenuMapper.selectMenuIdByCommunityId(communityId,INDEX_MENU_COUNT);
		
		// TODO: 2021/2/5 如果该小区没有菜单，暂时也给他返回小区  原因：现阶段便于测试人员测试   后面上线要删掉这里
		// 如果传入的小区没有菜单，则返回id为27839755849728L的小区菜单
		if (CollectionUtils.isEmpty(menuIds)) {
			menuIds = appMenuMapper.selectMenuIdByCommunityId(27839755849728L,INDEX_MENU_COUNT);
		}
		// TODO: 2021/2/5 如果该小区没有菜单，暂时也给他返回小区  原因：现阶段便于测试人员测试   后面上线要删掉这里
		
		return appMenuMapper.selectByIds(menuIds);
	}



	@Override
	public List<AppMenuEntity>  moreIndexMenu(Long communityId) {
		// 根据社区id查询中间表t_menu_community关于该社区的菜单id集合
		List<Long> menuIds = appMenuMapper.selectMenuIdByCommunityId(communityId,99);
		
		// TODO: 2021/2/5 如果该小区没有菜单，暂时也给他返回小区  原因：现阶段便于测试人员测试   后面上线要删掉这里
		// 如果传入的小区没有菜单，则返回id为27839755849728L的小区菜单
		List<Long> testmenuIds = appMenuMapper.selectMenuIdByCommunityId(27839755849728L,50);
		if (CollectionUtils.isEmpty(menuIds)) {
			menuIds = testmenuIds;
		}
		// TODO: 2021/2/5 如果该小区没有菜单，暂时也给他返回小区  原因：现阶段便于测试人员测试   后面上线要删掉这里
		
		return appMenuMapper.selectBatchIds(menuIds);
	}

	@Override
	/**
	 * @Description: 查询app菜单V2版
	 * @author: Hu
	 * @since: 2021/8/16 9:24
	 * @Param: [communityId]
	 * @return: java.util.List<com.jsy.community.entity.AppMenuEntity>
	 */
	public List<AppMenuEntity> listAppMenu(Long communityId, Integer sysType, String version) {
		List<AppMenuEntity> appMenuEntities = appMenuMapper.listAppMenu(communityId, 9);

		return appMenuEntities;
	}


	/**
	 * @Description: 更多菜单  V2版
	 * @author: Hu
	 * @since: 2021/8/16 9:24
	 * @Param: [communityId]
	 * @return: java.util.List<com.jsy.community.entity.AppMenuEntity>
	 */
	@Override
	public List<AppMenuEntity> listAppMenuAll(Long communityId) {
		QueryWrapper<AppMenuEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("version", "v2");
		queryWrapper.orderByAsc("id");
		queryWrapper.last("limit 9, 999");
//		return appMenuMapper.selectList(new QueryWrapper<AppMenuEntity>().eq("path","/community/hotline"));
		return appMenuMapper.selectList(queryWrapper);
	}
}
