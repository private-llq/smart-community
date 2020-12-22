package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.vo.HouseVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 业主房屋
 * @since 2020-12-16 11:48
 **/
public interface IUserHouseService extends IService<UserHouseEntity> {
	
	/**
	 * @return java.lang.Boolean
	 * @Author lihao
	 * @Description
	 * @Date 2020/12/15 15:07
	 * @Param [uid, houseEntityList]
	 **/
	Boolean saveUserHouse(String uid, List<UserHouseEntity> houseEntityList);
	
	/**
	* @Description: 查询业主所有拥有房屋的社区id
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	**/
	List<UserHouseEntity> queryUserCommunityIds(String uid);
	
	/**
	* @Description: 查询业主房屋及所属社区
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.UserHouseEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
	List<UserHouseEntity> queryUserHouses(String uid);
	
	/**
	 * @Description: 检查用户是否是房主
	 * @Param: [uid, houseId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	 **/
	boolean checkHouseHolder(Long uid, Long houseId);


	/**
	 * 批量更新业主房屋认证信息
	 * @param houseEntityList	参数列表
	 * @param uid				用户id
	 * @author YuLF
	 * @since  2020/12/18 14:18
	 */
    void updateProprietorHouseBatch(List<UserHouseEntity> houseEntityList, String uid);


	/**
	 * 通过用户id和社区id查出用户房屋信息
	 * @author YuLF
	 * @since  2020/12/18 14:18
	 * @param userId 		用户id
	 * @param communityId	社区id
	 * @return				返回房屋信息列表
	 */
	List<HouseVo> queryUserHouseList(String userId, Long communityId);
}
