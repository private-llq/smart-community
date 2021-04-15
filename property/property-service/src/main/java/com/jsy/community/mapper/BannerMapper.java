package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.BannerEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 社区轮播图Mapper
 * @since 2020-4-10 10:27
 **/
public interface BannerMapper extends BaseMapper<BannerEntity> {

	/**
	* @Description: 查找所有已占用排序位
	 * @Param: [communityId]
	 * @Return: java.util.List<java.lang.Integer>
	 * @Author: chq459799974
	 * @Date: 2021/4/15
	**/
	@Select("select sort from t_banner where community_id = #{communityId} and sort is not null")
	List<Integer> queryBannerSortByCommunityId(Long communityId);
	
	/**
	* @Description: 轮播图撤销发布，剩余轮播图重排序
	 * @Param: [id, communityId]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/4/15
	**/
	@Update("update t_banner set sort = sort - 1 " +
		"where sort > " +
		"(" +
		"select temp.sort from " +
		"(select sort from t_banner where id = #{id}) temp" +
		")" +
		"and community_id = #{communityId}")
	void resortBanner(@Param("id")Long id, @Param("communityId")Long communityId);
	
	/**
	* @Description: 取消撤销发布对象的排序
	 * @Param: [id]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/4/15
	**/
	@Update("update t_banner set sort = null where id = #{id}")
	void cancelSort(Long id);
	
	/**
	* @Description: 批量修改排序
	 * @Param: [map]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021/4/15
	**/
	int changeSorts(@Param("map") Map<Long,Integer> map);
}
