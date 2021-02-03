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
	 * @return com.jsy.community.entity.AppMenuEntity
	 * @Author lihao
	 * @Description 根据社区id查询所有菜单id
	 * @Date 2021/2/2 17:51
	 * @Param [communityId]
	 **/
	List<Long> getMenuIdByCommunityId(Long communityId);
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description
	 * @Date 2021/2/2 18:56
	 * @Param [appMenuEntity, communityId]
	 **/
	void addParentMenu(@Param("id") Long id, @Param("communityId")Long communityId);
}
