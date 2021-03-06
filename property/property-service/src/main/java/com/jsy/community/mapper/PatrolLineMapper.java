package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PatrolLineEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
 * @author chq459799974
 * @description 物业巡检线路Mapper
 * @since 2021-07-23 15:37
 **/
public interface PatrolLineMapper extends BaseMapper<PatrolLineEntity> {
	
	/**
	* @Description: 删除某一线路下所有关联的巡检点
	 * @Param: [lineId]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	@Delete("delete from t_patrol_line_point where line_id = #{lineId}")
	void clearLinePoint(Long lineId);
	
	/**
	* @Description: 线路绑定巡检点
	 * @Param: [lineId, pointIds]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	void addLinePoint(@Param("lineId")Long lineId, @Param("pointIds")Collection<Long> pointIds);
	
	/**
	* @Description: 查询线路绑定的巡更点idStr
	 * @Param: [lineId]
	 * @Return: java.util.List<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	@Select("select point_id from t_patrol_line_point where line_id = #{lineId}")
	List<String> queryBindPointIdStrList(Long lineId);
	
	/**
	* @Description: 修改巡检线路
	 * @Param: [entity]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	int updateLine(@Param("entity")PatrolLineEntity entity);
	
	/**
	* @Description: 巡检点解除与巡检线路关联
	 * @Param: [pointId]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	@Delete("delete from t_patrol_line_point where point_id = #{pointId}")
	int unbindPoint(Long pointId);
	
}
