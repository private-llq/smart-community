package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import org.apache.ibatis.annotations.MapKey;

import java.util.Collection;
import java.util.Map;

/**
 * @author chq459799974
 * @description 物业财务-收款单 Mapper
 * @since 2021-04-21 17:00
 **/
public interface PropertyFinanceReceiptMapper extends BaseMapper<PropertyFinanceReceiptEntity> {
	
	/**
	* @Description: 收款单号批量查 单号-收款单数据 映射
	 * @Param: [nums]
	 * @Return: java.util.Map<java.lang.String,com.jsy.community.entity.property.PropertyFinanceReceiptEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	**/
	@MapKey("receiptNum")
	Map<String,PropertyFinanceReceiptEntity> queryByReceiptNumBatch(Collection<String> nums);
	
}
