package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseMemberEntity;
import org.apache.ibatis.annotations.Param;

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

	/**
	 * @Description: 查询communityIds下所有居住人数数量
	 * @author: DKS
	 * @since: 2021/8/25 14:39
	 * @Param: communityIdList
	 * @return: Integer
	 */
	Integer selectAllPeopleByCommunityIds(@Param("list") List<Long> communityIdList);
	
	/**
	 * @Description: 查询communityIds下所有手机号码
	 * @author: DKS
	 * @since: 2021/8/30 17:49
	 * @Param: communityIdList
	 * @return: java.util.List<String>
	 */
	List<String> selectMobileListByCommunityIds(@Param("list") List<Long> communityIdList);
	
	/**
	 * @Description: 查询communityIds下所有手机号码并去重
	 * @author: DKS
	 * @since: 2021/8/30 17:50
	 * @Param: communityIdList
	 * @return: java.util.List<String>
	 */
	List<String> selectDistinctMobileListByCommunityIds(@Param("list") List<Long> communityIdList);
}
