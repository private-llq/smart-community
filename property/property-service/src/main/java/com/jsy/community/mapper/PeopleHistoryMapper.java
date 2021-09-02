package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.PeopleHistoryEntity;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;


/**
 * @author chq459799974
 * @description 访客进出记录Mapper
 * @since 2021-04-13 13:44
 **/
public interface PeopleHistoryMapper extends BaseMapper<PeopleHistoryEntity> {
	
	/**
	 * 分页查询
	 */
	Page<PeopleHistoryEntity> queryPage(Page<PeopleHistoryEntity> page, @Param("query") PeopleHistoryEntity query);
	
	/**
	 * 批量新增
	 */
	int insertBatch(List<PeopleHistoryEntity> peopleHistoryEntities);
	
	/**
	 * 查询开门次数
	 */
	int selectOpenDoorCount(@Param("adminCommunityId")Long adminCommunityId, @Param("beginTime")LocalDate beginTime, @Param("overTime")LocalDate overTime);
	
	/**
	 * 查询访客次数
	 */
	int selectVisitorCount(@Param("adminCommunityId")Long adminCommunityId, @Param("beginTime")LocalDate beginTime, @Param("overTime")LocalDate overTime);
}
