package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserUroraTagsEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author chq459799974
 * @description 用户极光推送tags
 * @since 2021-01-14 11:31
 **/
public interface UserUroraTagsMapper extends BaseMapper<UserUroraTagsEntity> {
	
	@Update("update t_user_urora_tags set community_tags = " +
		"if(" +
			"find_in_set(#{entity.communityTags},community_tags) = 0," +
				"if(trim(community_tags) = ''," +
					"concat(replace(community_tags,' ',''),#{entity.communityTags})," +
					"concat(replace(community_tags,' ',''),',',#{entity.communityTags}) " +
				")" +
			",replace(community_tags,' ','')" +
		")" +
		"where uid = #{entity.uid}")
	int appendTags(@Param("entity") UserUroraTagsEntity entity);
}
