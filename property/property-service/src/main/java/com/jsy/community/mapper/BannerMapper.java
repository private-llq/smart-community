package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.BannerEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author chq459799974
 * @description 社区轮播图Mapper
 * @since 2020-4-10 10:27
 **/
public interface BannerMapper extends BaseMapper<BannerEntity> {

	//查找所有已占用排序位
	@Select("select sort from t_banner where community_id = #{communityId} and sort is not null")
	List<Integer> queryBannerSortByCommunityId(Long communityId);
	
	//轮播图撤销发布，剩余轮播图重排序
	@Update("update t_banner set sort = sort - 1 " +
		"where sort > " +
		"(" +
		"select temp.sort from " +
		"(select sort from t_banner where id = #{id}) temp" +
		")" +
		"and community_id = #{communityId}")
	void resortBanner(@Param("id")Long id, @Param("communityId")Long communityId);
	
	//取消撤销发布对象的排序
	@Update("update t_banner set sort = null where id = #{id}")
	void cancelSort(Long id);
}
