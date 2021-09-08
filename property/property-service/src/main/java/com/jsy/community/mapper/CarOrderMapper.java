package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CarOrderEntity;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车辆缴费订单 Mapper 接口
 * </p>
 *
 * @author DKS
 * @since 2021-09-03 14:20
 */
public interface CarOrderMapper extends BaseMapper<CarOrderEntity> {
	/**
	 *@Author: DKS
	 *@Description: 查询communityId下每月的车位费统计
	 *@Param: communityId
	 *@Return: java.util.List<>
	 *@Date: 2021/9/3 15:05
	 **/
	List<Map<String, BigDecimal>> selectMonthCarPositionFeeByCommunityId(@Param("communityId") Long communityId, @Param("startTime") LocalDate startTime, @Param("endTime")LocalDate endTime);
	
	/**
	 *@Author: DKS
	 *@Description: 根据时间段查询物业小区年车位费用总计
	 *@Param: startTime,endTime,communityId:
	 *@Date: 2021/9/3 15:08
	 **/
	BigDecimal CarPositionFeeByYear(@Param("startTime")LocalDate startTime, @Param("endTime")LocalDate endTime, @Param("communityId") Long communityId);
	
	/**
	 *@Author: DKS
	 *@Description: 根据时间段查询小区当月每天车位费用总计
	 *@Param: startTime,endTime,communityId:
	 *@Return: java.util.List<>
	 *@Date: 2021/9/3 15:20
	 **/
	List<Map<String,BigDecimal>> carPositionByDate(LocalDate startTime, LocalDate endTime, Long communityId);
	
	/**
	 *@Author: DKS
	 *@Description: 根据时间段查询小区月费用总计
	 *@Param: startTime,endTime,communityId:
	 *@Date: 2021/9/3 15:23
	 **/
	BigDecimal carPositionByMonth(LocalDate startTime, LocalDate endTime, Long communityId);

}
