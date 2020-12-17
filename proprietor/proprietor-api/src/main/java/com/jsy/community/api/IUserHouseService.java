package com.jsy.community.api;

import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserHouseEntity;

import java.util.List;

/**
 * @author chq459799974
 * @description 业主房屋
 * @since 2020-12-16 11:48
 **/
public interface IUserHouseService {
	
	/**
	 * @return java.lang.Boolean
	 * @Author lihao
	 * @Description
	 * @Date 2020/12/15 15:07
	 * @Param [uid, houseEntityList]
	 **/
	Boolean saveUserHouse(String uid, List<HouseEntity> houseEntityList);
	
	/**
	* @Description: 查询业主所有拥有房屋的社区id
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	**/
	List<Long> queryUserCommunityIds(String uid);
	
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
}
