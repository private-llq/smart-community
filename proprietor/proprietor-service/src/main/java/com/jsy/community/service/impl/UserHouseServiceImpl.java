package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.mapper.CarMapper;
import com.jsy.community.mapper.UserHouseMapper;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.HouseVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 业主房屋实现类
 * @since 2020-12-16 11:47
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserHouseServiceImpl extends ServiceImpl<UserHouseMapper, UserHouseEntity> implements IUserHouseService {

	@Autowired
	private UserHouseMapper userHouseMapper;
	
	/**
	 * @return java.lang.Boolean
	 * @Author lihao
	 * @Description
	 * @Date 2020/12/15 15:07
	 * @Param [uid, houseEntityList]
	 **/
	@Override
	public Boolean saveUserHouse(String uid, List<UserHouseEntity> houseEntityList) {
		if (!CollectionUtils.isEmpty(houseEntityList)) {
			for (UserHouseEntity houseEntity : houseEntityList) {
				Long communityId = houseEntity.getCommunityId();
				Long id = houseEntity.getId();

				houseEntity.setUid(uid);
				houseEntity.setCommunityId(communityId);
				houseEntity.setHouseId(id);
				houseEntity.setCheckStatus(2);//审核中
				houseEntity.setId(SnowFlake.nextId());
				
				userHouseMapper.insert(houseEntity);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * @Description: 查询业主所有拥有房屋的社区id
	 * @Param: [uid]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	 **/
	@Override
	public List<UserHouseEntity> queryUserCommunityIds(String uid){
		return userHouseMapper.queryUserCommunityIds(uid);
	}
	
	/**
	* @Description: 查询业主房屋及所属社区
	 * @Param: [uid]
	 * @Return: java.util.List<com.jsy.community.entity.UserHouseEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
	@Override
	public List<UserHouseEntity> queryUserHouses(String uid){
		return userHouseMapper.queryUserHouses(uid);
	}
	
	/**
	 * @Description: 检查用户是否是房主
	 * @Param: [uid, houseId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	 **/
	@Override
	public boolean checkHouseHolder(Long uid, Long houseId) {
		Integer integer = userHouseMapper.selectCount(new QueryWrapper<UserHouseEntity>().eq("uid", uid).eq("house_id", houseId));
		if (integer == 1) {
			return true;
		}
		return false;
	}


	/**
	 * 批量更新业主房屋认证信息
	 * @param houseEntityList	参数列表
	 * @param uid				用户id
	 * @author YuLF
	 * @since  2020/12/18 14:18
	 */
	@Override
	public void updateProprietorHouseBatch(List<UserHouseEntity> houseEntityList, String uid) {
		userHouseMapper.updateProprietorHouseBatch(houseEntityList, uid);
	}


	/**
	 * 通过用户id和社区id查出用户房屋信息
	 * @param userId 		用户id
	 * @return				返回房屋信息列表
	 */
	@Override
	public List<HouseVo> queryUserHouseList(String userId) {
		return userHouseMapper.queryUserHouseList(userId);
	}

	@Override
	public boolean hasHouse(String uid, Long communityId) {
		return false;
	}

	/**
	 * @Description: 查询指定小区内是否有房(是否是业主)
	 * @Param: [uid, communityId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/23
	 **/
	@Override
	public boolean hasHouse(String uid,Long communityId){
		Integer count = userHouseMapper.selectCount(new QueryWrapper<UserHouseEntity>().eq("uid", uid).eq("community_id", communityId));
		return count > 0;
	}
}
