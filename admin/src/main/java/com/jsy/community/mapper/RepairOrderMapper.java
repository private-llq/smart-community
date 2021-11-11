package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.RepairOrderEntity;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 * 报修订单信息 Mapper 接口
 * </p>
 *
 * @author DKS
 * @since 2021-11-09
 */
@Mapper
public interface RepairOrderMapper extends BaseMapper<RepairOrderEntity> {
	/**
	 *@Author: DKS
	 *@Description: 根据时间段查询报修费用总计
	 *@Param: startTime,endTime:
	 *@Date: 2021/11/09 13:49
	 **/
	BigDecimal repairTurnover(LocalDate startTime, LocalDate endTime);
}
