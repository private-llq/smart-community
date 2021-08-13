package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyAdvanceDepositEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyAdvanceDepositQO;
import com.jsy.community.utils.PageInfo;

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
}
