package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.vo.HouseVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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
	@Select("select community_id,house_id from t_user_house where check_status = 1 and uid = #{uid} order by create_time")
	List<UserHouseEntity> queryUserCommunityIds(String uid);
	
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
	 * 通过用户id和社区id查出用户房屋信息
	 * @param userId 		用户id
	 * @return				返回房屋信息列表
	 */
	@Select("select h.id,h.community_id,h.house_id,c.name as communityName,s.building,s.unit,s.floor,s.door from t_user_house as h LEFT JOIN t_house as s on h.house_id = s.id LEFT JOIN t_community as c on h.community_id = c.id  where h.deleted = 0 and s.deleted = 0 and c.deleted = 0 and h.uid = #{userId} and check_status = 1")
	List<HouseVo> queryUserHouseList(@Param("userId") String userId);

	/**
	 * 批量新增房屋信息
	 * @author YuLF
	 * @since  2020/12/24 14:07
	 */
    void addHouseBatch(@Param("userHouseList") List<UserHouseEntity> any);
}
