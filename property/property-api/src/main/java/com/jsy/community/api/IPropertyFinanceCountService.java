package com.jsy.community.api;

import com.jsy.community.entity.property.PropertyFinanceCountEntity;
import com.jsy.community.vo.property.StatisticsVO;

import java.time.LocalDate;

/**
 * @author chq459799974
 * @description 财务统计
 * @since 2021-04-26 16:11
 **/
public interface IPropertyFinanceCountService {
	/**
	 *@Author: Pipi
	 *@Description: 缴费统计
	 *@Param: query:
	 *@Return: com.jsy.community.vo.property.StatisticsVO
	 *@Date: 2021/4/27 14:15
	 **/
	StatisticsVO orderPaidCount(PropertyFinanceCountEntity query);

	/**
	 * @Author: Pipi
	 * @Description: 应收统计
	 * @Param: query:
	 * @Return: com.jsy.community.vo.property.StatisticsVO
	 * @Date: 2021/4/27 14:15
	 **/
	StatisticsVO orderReceivableCount(PropertyFinanceCountEntity query);

	/**
	 *@Author: Pipi
	 *@Description: 结算统计
	 *@Param: query: 
	 *@Return: com.jsy.community.vo.property.StatisticsVO
	 *@Date: 2021/4/27 16:31
	 **/
	StatisticsVO statementCount(PropertyFinanceCountEntity query);
}
