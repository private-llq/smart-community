package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseMemberEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  HouseMember 接口
 * </p>
 *
 * @author DKS
 * @since 2021-10-22
 */
@Mapper
public interface HouseMemberMapper extends BaseMapper<HouseMemberEntity> {
	/**
	 *@Author: DKS
	 *@Description: 根据业主姓名模糊查询
	 *@Param: communityId:
	 *@Return: java.util.List<java.util.Long>
	 *@Date: 2021/10/22 10:01
	 **/
	List<Long> getAllHouseIdByOwnerName(String ownerName);
	
	/**
	 * @Description: 根据房屋Id查询业主姓名
	 * @author: DKS
	 * @since: 2021/10/22 10:54
	 * @Param: houseId
	 * @return: java.util.String
	 */
	String getOwnerNameByHouseId(Long houseId);
	
	/**
	 * @Description: 根据房屋Id查询租户数量
	 * @author: DKS
	 * @since: 2021/10/22 11:34
	 * @Param: houseId
	 * @return: java.util.Integer
	 */
	Integer getTenantByHouseId(Long houseId);
}
