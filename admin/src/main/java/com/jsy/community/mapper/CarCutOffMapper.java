package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.CarCutOffEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * @author: DKS
 * @since: 2021/11/9 11:46
 */
@Mapper
public interface CarCutOffMapper extends BaseMapper<CarCutOffEntity> {
	/**
	 * 查询今日车辆进入次数
	 */
	int selectCarIntoCount(@Param("beginTime") LocalDate beginTime, @Param("overTime") LocalDate overTime);
}
