package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PeopleHistoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;


/**
 * @author DKS
 * @description 访客进出记录Mapper
 * @since 2021-11-09 11:46
 **/
@Mapper
public interface PeopleHistoryMapper extends BaseMapper<PeopleHistoryEntity> {
	
	/**
	 * 查询开门次数
	 */
	int selectOpenDoorCount(@Param("beginTime") LocalDate beginTime, @Param("overTime") LocalDate overTime);
	
	/**
	 * 查询访客次数
	 */
	int selectVisitorCount(@Param("beginTime") LocalDate beginTime, @Param("overTime") LocalDate overTime);
}
