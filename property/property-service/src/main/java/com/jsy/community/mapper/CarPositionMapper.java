package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.vo.FeeRelevanceTypeVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CarPositionMapper extends BaseMapper<CarPositionEntity> {

    <T> void seavefile(List<T> list);

    List<CarPositionEntity> selectCarPosition(CarPositionEntity qo);

    /**
     *@Author: DKS
     *@Description: 查询小区下所有的车位
     *@Param: communityId:
     *@Return: java.util.List<com.jsy.community.entity.CarPositionEntity>
     *@Date: 2021/8/24 13:44
     **/
    List<CarPositionEntity> getAllCarPositionByCommunity(Long communityId);

    /**
     * @Description: 查询communityIds下所有车位数量
     * @author: DKS
     * @since: 2021/8/25 14:44
     * @Param: communityIdList
     * @return: Integer
     */
    Integer selectAllCarPositionByCommunityIds(@Param("list") List<Long> communityIdList);

	int relieve(Long id);

	/**
	 * @Description: 修改车位状态
	 * @author: Hu
	 * @since: 2021/8/27 17:02
	 * @Param:
	 * @return:
	 */
	@Update("update t_car_position set community_id=null,car_pos_status=0,binding_status=0,uid=null,owner_phone=null,begin_time=null,end_time=null where id = #{carPositionId}")
    void updateByPosition(@Param("carPositionId") Long carPositionId);
	
	/**
	 * @Description: 查询communityIds下所有已占用车位数量
	 * @author: DKS
	 * @since: 2021/9/3 9:53
	 * @Param: communityIdList
	 * @return: Integer
	 */
	Integer selectAllOccupyCarPositionByCommunityIds(@Param("list") List<Long> communityIdList);

	/**
	 * @Description: 查询所有业主自用或者月租车位
	 * @author: Hu
	 * @since: 2021/9/7 11:26
	 * @Param:
	 * @return:
	 */
	@Select("select id,car_position as name from t_car_position where deleted=0 and community_id=#{communityId} and car_pos_status=#{type}")
    List<FeeRelevanceTypeVo> getCarPosition(@Param("communityId") Long adminCommunityId, @Param("type") Integer type);

	/**
	 * @Description: 根据手机号查询绑定车位的id
	 * @Param: [mobile]
	 * @Return:
	 * @Author: DKS
	 * @Date: 2021/09/07
	 **/
	List<Long> queryBindCarPositionByMobile(@Param("mobile") String mobile, @Param("communityId") Long communityId);
}
