package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.BannerEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * banner轮播图 Mapper 接口
 *
 * @author chq459799974
 * @since 2020-11-16
 */
public interface BannerMapper extends BaseMapper<BannerEntity> {
	
	/**
	* @Description: 刷新点击量
	 * @Param: [map]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/30
	**/
	void refreshClickCount(@Param("map") Map<Long,Long> map);
	
	/**
	* @Description: 查询轮播图 带通用轮播图(根据配置项决定)
	 * @Param: [communityId, position]
	 * @Return: java.util.List<com.jsy.community.entity.BannerEntity>
	 * @Author: chq459799974
	 * @Date: 2021/5/7
	**/
//	@Select("select id,position,sort,url from t_banner where community_id = #{communityId} and position = #{position} \n" +
//		"or community_id = 0 order by community_id desc,sort")
	@Select("select id,position,sort,url from t_banner where (community_id = #{communityId} and position = #{position} \n" +
		"${condition}) and (deleted = 0) order by community_id desc,sort")
	List<BannerEntity> queryListByCommunityIdAndPosition(@Param("communityId")Long communityId,@Param("position")Integer position,@Param("condition")String condition);
	
}
