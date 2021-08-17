package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyAdvanceDepositEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyAdvanceDepositQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 物业预存款余额
 * @author: DKS
 * @create: 2021-08-11 16:15
 **/
public interface IPropertyAdvanceDepositService extends IService<PropertyAdvanceDepositEntity> {
	
	/**
	 * @Description: 预存款充值余额
	 * @Param: [propertyAdvanceDepositEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/08/11
	 **/
	boolean addRechargePropertyAdvanceDeposit(PropertyAdvanceDepositEntity propertyAdvanceDepositEntity);
	
	/**
	 * @Description: 修改预存款充值余额
	 * @Param: [propertyAdvanceDepositEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/08/11
	 **/
	boolean updateRechargePropertyAdvanceDeposit(PropertyAdvanceDepositEntity propertyAdvanceDepositEntity);
	
	/**
	 * @Description: 分页查询预存款余额
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.PropertyAdvanceDepositEntity>>
	 * @Author: DKS
	 * @Date: 2021/08/12
	 **/
	PageInfo<PropertyAdvanceDepositEntity> queryPropertyAdvanceDeposit(BaseQO<PropertyAdvanceDepositQO> baseQO);
	
	/**
	 *@Author: DKS
	 *@Description: 导入充值余额
	 *@Param: excel:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/16 10:05
	 **/
	Integer saveAdvanceDeposit(List<PropertyAdvanceDepositEntity> propertyAdvanceDepositEntityList, Long communityId, String uid);
	
	/**
	 *@Author: DKS
	 *@Description: 根据houseId查询预存款余额
	 *@Param:
	 *@Return: com.jsy.community.vo.CommonResult
	 *@Date: 2021/8/16 10:11
	 **/
	PropertyAdvanceDepositEntity queryAdvanceDepositByHouseId (Long houseId, Long communityId);
}
