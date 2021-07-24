package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PatrolPointEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @author chq459799974
 * @description 物业巡检点Mapper
 * @since 2021-07-23 15:39
 **/
public interface PatrolPointMapper extends BaseMapper<PatrolPointEntity> {
	
	/**
	* @Description: 修改巡检点位
	 * @Param: [entity]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	int updatePoint(@Param("entity")PatrolPointEntity entity);
	
}
