package com.jsy.community.api;

import com.jsy.community.entity.property.PropertyAccountBankEntity;

/**
 * @author chq459799974
 * @description 社区账户接口
 * @since 2021-04-20 17:39
 **/
public interface IPropertyAccountService {
	
	/**
	* @Description: 社区id查对公账号 - 银行卡
	 * @Param: [communityId]
	 * @Return: com.jsy.community.entity.property.PropertyAccountBankEntity
	 * @Author: chq459799974
	 * @Date: 2021/4/20
	**/
	PropertyAccountBankEntity queryBankAccount(Long communityId);
	
	/**
	* @Description: 根据id查对公账号 - 银行卡
	 * @Param: [id]
	 * @Return: com.jsy.community.entity.property.PropertyAccountBankEntity
	 * @Author: chq459799974
	 * @Date: 2021/4/21
	**/
	PropertyAccountBankEntity queryBankAccountById(Long id);
}
