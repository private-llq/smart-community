package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.CarCutOffEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface CarCutOffMapper extends BaseMapper<CarCutOffEntity> {
	/**
	 * 查询今日车辆进入次数
	 */
	int selectCarIntoCount(@Param("adminCommunityId")Long adminCommunityId, @Param("beginTime") LocalDate beginTime, @Param("overTime")LocalDate overTime);
	
	/**
	 * 查询今日车辆外出次数
	 */
	int selectCarGoOutCount(@Param("adminCommunityId")Long adminCommunityId, @Param("beginTime")LocalDate beginTime, @Param("overTime")LocalDate overTime);
}
