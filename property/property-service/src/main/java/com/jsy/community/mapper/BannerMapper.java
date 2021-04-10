package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.BannerEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chq459799974
 * @description 社区轮播图Mapper
 * @since 2020-4-10 10:27
 **/
public interface BannerMapper extends BaseMapper<BannerEntity> {

	@Select("select sort from t_banner where community_id = #{communityId} and sort is not null")
	List<Integer> queryBannerSortByCommunityId(Long communityId);
	
}
