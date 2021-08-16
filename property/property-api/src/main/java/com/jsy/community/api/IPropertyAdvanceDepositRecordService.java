package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyAdvanceDepositRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyAdvanceDepositRecordQO;
import com.jsy.community.utils.PageInfo;

/**
 * @program: com.jsy.community
 * @description: 物业预存款余额明细记录表
 * @author: DKS
 * @create: 2021-08-12 14:15
 **/
public interface IPropertyAdvanceDepositRecordService extends IService<PropertyAdvanceDepositRecordEntity> {
	
	/**
	 * @Description: 新增预存款变更明细记录
	 * @Param: [propertyAdvanceDepositRecordEntity]
	 * @Return: boolean
	 * @Author: DKS
	 * @Date: 2021/08/12
	 **/
	boolean addPropertyAdvanceDepositRecord(PropertyAdvanceDepositRecordEntity propertyAdvanceDepositRecordEntity);
	
	/**
	 * @Description: 预存款分页查询变更明细
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.PropertyAdvanceDepositRecordEntity>>
	 * @Author: DKS
	 * @Date: 2021/08/12
	 **/
	PageInfo<PropertyAdvanceDepositRecordEntity> queryPropertyAdvanceDepositRecord(BaseQO<PropertyAdvanceDepositRecordQO> baseQO);
}
