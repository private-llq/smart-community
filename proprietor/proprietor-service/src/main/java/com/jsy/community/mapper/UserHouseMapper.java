package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.vo.HouseVo;
import org.apache.ibatis.annotations.Param;
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
	* @Description: 查询业主房屋及所属社区
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.UserHouseEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
	@Select("select community_id,house_id from t_user_house where check_status = 1 and uid = #{uid}")
	List<UserHouseEntity> queryUserHouses(String uid);


	/**
	 * 批量更新业主房屋认证信息
	 * @param houseEntityList	参数列表
	 * @param uid				用户id
	 * @author YuLF
	 * @since  2020/12/18 14:18
	 */
    void updateProprietorHouseBatch(@Param("houseList") List<UserHouseEntity> houseEntityList, @Param("uid") String uid);


	/**
	 * 通过用户id和社区id查出用户房屋信息
	 * @param userId 		用户id
	 * @param communityId	社区id
	 * @return				返回房屋信息列表
	 */
	@Select("select h.id,h.community_id,h.house_id,c.name as communityName,s.building,s.unit,s.floor,s.door from t_user_house as h LEFT JOIN t_house as s on h.house_id = s.id LEFT JOIN t_community as c on h.community_id = c.id " +
			"where h.uid = #{userId} and h.community_id = #{communityId}")
	List<HouseVo> queryUserHouseList(@Param("userId") String userId,@Param("communityId") Long communityId);
}
