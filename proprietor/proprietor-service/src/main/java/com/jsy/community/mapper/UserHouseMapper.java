package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserHouseEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chq459799974
 * @description 业主房屋
 * @since 2020-11-25 16:46
 **/
public interface UserHouseMapper extends BaseMapper<UserHouseEntity> {
	
	/**
	* @Description: 查询业主房屋所属小区id
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	**/
	@Select("select community_id from t_user_house where check_status = 1 and uid = #{uid}")
	List<Long> queryUserCommunityIds(String uid);
	
	/**
	* @Description: 查询业主房屋id
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	**/
	@Select("select house_id from t_user_house where check_status = 1 and uid = #{uid}")
	List<Long> queryUserHouseIds(String uid);
	
}
