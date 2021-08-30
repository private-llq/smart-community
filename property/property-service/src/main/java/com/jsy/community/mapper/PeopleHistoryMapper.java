package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.PeopleHistoryEntity;
import org.apache.ibatis.annotations.Param;

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
}
