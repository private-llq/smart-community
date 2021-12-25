package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.AppMenuEntity;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
public interface IAppMenuService extends IService<AppMenuEntity> {
	
	/**
	 * @return java.util.List<com.jsy.community.entity.IndexMenuEntity>
	 * @Author lihao
	 * @Description 查询app首页菜单
	 * @Date 2021/2/3 13:50
	 * @Param [communityId：社区id]
	 **/
	@Deprecated
	List<AppMenuEntity> listIndexMenu(Long communityId);

	/**
	 * @return java.util.List<com.jsy.community.entity.IndexMenuEntity>
	 * @Author lihao
	 * @Description 更多菜单
	 * @Date 2021/2/3 14:12
	 * @Param [communityId：社区id]
	 **/
	@Deprecated
	List<AppMenuEntity>  moreIndexMenu(Long communityId);

	/**
	 * @Description: 查询所有菜单   v2版
	 * @author: Hu
	 * @since: 2021/8/16 9:22
	 * @Param:
	 * @return:
	 */
	List<AppMenuEntity> listAppMenu(Long communityId, Integer sysType, String version);

	/**
	 * @Description: 更多菜单   v2版
	 * @author: Hu
	 * @since: 2021/8/16 9:22
	 * @Param:
	 * @return:
	 */
	List<AppMenuEntity> listAppMenuAll(Long communityId);
}
