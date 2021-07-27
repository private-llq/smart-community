package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PatrolPointEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Map;

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
	
	/**
	* @Description: 批量查询编号与实体对应关系
	 * @Param: [nums, brandId]
	 * @Return: java.util.Map<java.lang.String,com.jsy.community.entity.property.PatrolPointEntity>
	 * @Author: chq459799974
	 * @Date: 2021-07-27
	**/
	@MapKey("number")
	Map<String,PatrolPointEntity> queryByNumberBatch(@Param("nums")Collection<String> nums, @Param("brandId")Long brandId);
	
}
