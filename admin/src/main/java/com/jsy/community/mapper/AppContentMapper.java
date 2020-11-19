package com.jsy.community.mapper;

import com.jsy.community.entity.RegionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author chq459799974
 * @description APP内容设置
 * @since 2020-11-19 14:10
 **/
@Mapper
public interface AppContentMapper {
	
	/**
	 * 备份推荐城市
	 */
	@Select("select id,name from t_hot_city")
	List<RegionEntity> getHotCity();
	
	/**
	 * 清空推荐城市
	 */
	@Update("truncate t_hot_city")
	void clearHotCity();
	
	/**
	 * 添加推荐城市
	 */
	int insertHotCity(List<RegionEntity> list);
}
