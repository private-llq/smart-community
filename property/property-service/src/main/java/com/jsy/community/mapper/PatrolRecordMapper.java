package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PatrolRecordEntity;

import java.util.List;

/**
 * @author chq459799974
 * @description 物业巡检记录Mapper
 * @since 2021-07-23 15:36
 **/
public interface PatrolRecordMapper extends BaseMapper<PatrolRecordEntity> {
	
	/**
	* @Description: 批量新增
	 * @Param: [recordList]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021-07-27
	**/
	void addBatch(List<PatrolRecordEntity> recordList);
	
}
