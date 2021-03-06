package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.AppMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
	 * @return java.util.List<java.lang.Long>
	 * @Author lihao
	 * @Description 根据社区id去查询中间表查菜单id集合
	 * @Date 2021/2/3 13:59
	 * @Param [communityId]
	 **/
	List<Long> selectMenuIdByCommunityId(@Param("communityId") Long communityId, @Param("count") Integer count);
	
	/**
	 * @return java.util.List<java.lang.Long>
	 * @Author lihao
	 * @Description 更多菜单
	 * @Date 2021/2/3 14:13
	 * @Param [communityId]
	 **/
	List<Long> getMenuIdByCommunityId(Long communityId);

	/**
	 * @Description: 排序查询菜单
	 * @author: Hu
	 * @since: 2021/8/12 14:54
	 * @Param:
	 * @return:
	 */
	List<AppMenuEntity> selectByIds(@Param("menuIds") List<Long> menuIds);

	/**
	 * @Description: 查询所有app菜单
	 * @author: Hu
	 * @since: 2021/8/16 9:28
	 * @Param:
	 * @return:
	 */
	List<AppMenuEntity> listAppMenu(@Param("communityId") Long communityId, @Param("size") int size);
}
