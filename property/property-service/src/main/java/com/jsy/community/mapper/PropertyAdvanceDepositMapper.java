package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyAdvanceDepositEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 物业预存款余额
 * @author: DKS
 * @create: 2021-08-11 16:15
 **/
public interface PropertyAdvanceDepositMapper extends BaseMapper<PropertyAdvanceDepositEntity> {
	/**
	 *@Author: DKS
	 *@Description: 导入批量新增充值余额
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/16 10:11
	 **/
	Integer saveAdvanceDeposit(@Param("list") List<PropertyAdvanceDepositEntity> propertyAdvanceDepositEntityList);
	
	/**
	 *@Author: DKS
	 *@Description: 根据houseId查询预存款余额
	 *@Param:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/16 10:11
	 **/
	PropertyAdvanceDepositEntity queryAdvanceDepositByHouseId (Long houseId, Long communityId);
	
	/**
	 *@Author: DKS
	 *@Description: 导入批量修改充值余额
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/16 11:58
	 **/
	Integer UpdateAdvanceDeposit(@Param("list") List<PropertyAdvanceDepositEntity> propertyAdvanceDepositEntityList);
}