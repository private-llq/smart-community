package com.jsy.community.api;

import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

/**
 * @author chq459799974
 * @description 物业财务-收款单 Service
 * @since 2021-04-21 17:02
 **/
public interface IPropertyFinanceReceiptService {
	
	/**
	* @Description: 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo
	 * @Author: chq459799974
	 * @Date: 2021/4/21
	**/
	PageInfo queryPage(BaseQO<PropertyFinanceReceiptEntity> baseQO);
}
