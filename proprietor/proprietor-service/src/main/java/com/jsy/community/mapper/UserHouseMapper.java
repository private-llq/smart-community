package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.qo.proprietor.UserHouseQo;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.UserHouseVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * @author chq459799974
 * @description 业主房屋
 * @since 2020-11-25 16:46
 **/
public interface UserHouseMapper extends BaseMapper<UserHouseEntity> {
	
	/**
	* @Description: 查询业主房屋id和所属小区id(房屋已认证的)
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	**/
//	@Select("select community_id,house_id from t_user_house where check_status = 1 and uid = #{uid} order by create_time")
	@Select("select community_id,house_id from t_user_house where uid = #{uid} order by create_time")
	List<UserHouseEntity> queryUserHouseIdsAndCommunityIds(String uid);
	
	/**
	* @Description: 查询业主房屋及所属社区(房屋已认证的)
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.UserHouseEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
//	@Select("select community_id,house_id from t_user_house where check_status = 1 and uid = #{uid}")
	@Select("select community_id,house_id from t_user_house where uid = #{uid}")
	List<UserHouseEntity> queryUserHouses(String uid);

	/**
	* @Description: 查询用户社区id(房屋已认证的)
	 * @Param: [uid]
	 * @Return: java.util.Set<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2021/3/31
	**/
//	@Select("select community_id from t_user_house where uid = #{uid} and check_status = 1")
	@Select("select community_id from t_user_house where uid = #{uid} and deleted=0")
	Set<Long> queryUserHousesOfCommunityIds(String uid);


	/**
	 * 通过用户id查出用户房屋信息
	 * @param userId 		用户id
	 * @return				返回房屋信息列表
	 */
	List<HouseVo> queryUserHouseList(@Param("userId") String userId);

	/**
	 * 批量新增房屋信息
	 * @param any 		需要更新的房屋信息
	 * @param uid		用户id
	 * @author YuLF
	 * @since  2020/12/24 14:07
	 */
    void addHouseBatch(@Param("anyHouse") List<UserHouseQo> any, @Param("uid") String uid);

	/**
	 * 更新房屋信息
	 * @param h 	房屋信息
	 * @param uid	用户id
	 */
	void update(UserHouseQo h, String uid);

	/**
	 * @Description: 查询所有我的房间
	 * @author: Hu
	 * @since: 2021/8/20 16:22
	 * @Param:
	 * @return:
	 */
    List<UserHouseVO> meHouse(@Param("uid") String uid);

    /**
     * @Description: 查询用户社区（家属和租户）
     * @author: Hu
     * @since: 2021/9/22 14:01
     * @Param:
     * @return:
     */
	@Select("select community_id from t_house_member where uid = #{uid} and deleted=0")
	Set<Long> queryRelationHousesOfCommunityIds(@Param("uid") String uid);
}
