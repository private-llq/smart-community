package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.AppMenuEntity;
import com.jsy.community.vo.menu.AppMenuVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 菜单 Mapper 接口
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@Mapper
public interface AppMenuMapper extends BaseMapper<AppMenuEntity> {
	
	
	/**
	 * @return com.jsy.community.entity.AppMenuEntity
	 * @Author lihao
	 * @Description 根据社区id查询所有菜单id
	 * @Date 2021/2/2 17:51
	 * @Param [communityId]
	 **/
	List<Long> getMenuIdByCommunityId(Long communityId);
	
	/**
	 * @return java.util.List<java.lang.Long>
	 * @Author lihao
	 * @Description 查询中间表菜单id集合
	 * @Date 2021/3/23 10:23
	 * @Param [communityId]
	 **/
	List<Long> listMenuId(Long communityId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 批量添加菜单到中间表
	 * @Date 2021/2/2 18:56
	 * @Param [appMenuEntity, communityId]
	 **/
	void insertMiddleMenu(List<AppMenuVO> appMenuVOS);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 批量删除菜单中间表
	 * @Date 2021/3/23 11:27
	 * @Param [ids]
	 **/
	void deleteMiddleMenu(Long id);
}
