package com.jsy.community.api;

import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
	
	/**
	* @Description: 收款单号批量查 单号-收款单数据 映射
	 * @Param: [nums]
	 * @Return: java.util.Map<java.lang.String,com.jsy.community.entity.property.PropertyFinanceReceiptEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	**/
	Map<String,PropertyFinanceReceiptEntity> queryByReceiptNumBatch(Collection<String> nums);
	
	/**
	* @Description: 条件查询批量收款单号
	 * @Param: [query]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	**/
	List<String> queryReceiptNumsByCondition(PropertyFinanceReceiptEntity query);
}
