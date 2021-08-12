package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyDepositEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyDepositQO;
import com.jsy.community.utils.PageInfo;

/**
 * @program: com.jsy.community
 * @description: 物业押金账单
 * @author: DKS
 * @create: 2021-08-10 17:35
 **/
public interface IPropertyDepositService extends IService<PropertyDepositEntity> {
	
	/**
	 * @Description: 新增物业押金账单
	 * @Param: [propertyDepositEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/08/11
	 **/
	boolean addPropertyDeposit(PropertyDepositEntity propertyDepositEntity);
	
	/**
	 * @Description: 修改物业押金账单
	 * @Param: [propertyDepositEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/08/11
	 **/
	boolean updatePropertyDeposit(PropertyDepositEntity propertyDepositEntity);
	
	/**
	 * @Description: 删除物业押金账单
	 * @Param: [id,communityId]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/08/11
	 **/
	boolean deletePropertyDeposit(Long id,Long communityId);
	
	/**
	 * @Description: 分页查询物业押金账单
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.PropertyDepositEntity>>
	 * @Author: DKS
	 * @Date: 2021/08/11
	 **/
	PageInfo<PropertyDepositEntity> queryPropertyDeposit(BaseQO<PropertyDepositQO> baseQO);
}
