package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
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
	
	/**
	* @Description: 条件查询批量收款单号
	 * @Param: [query]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/4/23
	**/
	//TODO 加上社区id ? 看收款时能不能获取到 批量缴费项目是否必定全部是同一社区？
	List<String> queryReceiptNumsByCondition(@Param("query")PropertyFinanceReceiptEntity query);
	
	//根据时间查询List<Map<月份,收款单号>>
	List<Map<String,String>>queryReceiptNumsAndMonthByMonth(@Param("query")PropertyFinanceReceiptEntity query);
	
	//根据时间查询Map<月份,Map<月份,'缴费单号1,缴费单号2'>>
//	@MapKey("perMonth")
//	Map<String,Map<String,String>>queryReceiptNumsAndMonthMapByMonth(@Param("query")PropertyFinanceReceiptEntity query);
	
	List<Map<String,String>> queryReceiptNumsAndMonthMapByMonth(@Param("query")PropertyFinanceReceiptEntity query);
}
