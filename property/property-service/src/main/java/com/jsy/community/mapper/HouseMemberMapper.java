package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseMemberEntity;

import java.util.List;

/**
 * <p>
 *  HouseMember 接口
 * </p>
 *
 * @author lihao
 * @since 2021-03-13
 */
public interface HouseMemberMapper extends BaseMapper<HouseMemberEntity> {
	/**
	 *@Author: DKS
	 *@Description: 查询小区下所有的业主
	 *@Param: communityId:
	 *@Return: java.util.List<com.jsy.community.entity.HouseMemberEntity>
	 *@Date: 2021/8/24 13:44
	 **/
	List<HouseMemberEntity> getAllOwnerByCommunity(Long communityId);
	
	/**
	 *@Author: DKS
	 *@Description: 查询小区下所有的租户
	 *@Param: communityId:
	 *@Return: java.util.List<com.jsy.community.entity.HouseMemberEntity>
	 *@Date: 2021/8/24 13:44
	 **/
	List<HouseMemberEntity> getAllTenantByCommunity(Long communityId);
}
