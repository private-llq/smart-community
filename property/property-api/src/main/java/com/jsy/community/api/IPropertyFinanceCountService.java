package com.jsy.community.api;

import com.jsy.community.entity.property.PropertyFinanceCountEntity;

import java.time.LocalDate;

/**
 * @author chq459799974
 * @description 财务统计
 * @since 2021-04-26 16:11
 **/
public interface IPropertyFinanceCountService {
	void orderPaidCount(PropertyFinanceCountEntity query);
}
