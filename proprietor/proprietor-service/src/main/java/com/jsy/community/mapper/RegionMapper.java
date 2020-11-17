package com.jsy.community.mapper;

import com.jsy.community.entity.RegionEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 省市区表Mapper
 * @author chq459799974
 * @since 2020-11-13
 */
@Mapper
public interface RegionMapper {
	
	/**
	* @Description: 获取分级封装后的所有区域id,name,pid
	 * @Param: []
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/13
	**/
	List<RegionEntity> getAllRegion();
	
	/**
	* @Description: 定时同步区域表-清理旧数据
	 * @Param: []
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/11/14
	**/
	@Delete("truncate t_region")
	void deleteAll();
	
	/**
	* @Description: 定时同步区域表-更新数据
	 * @Param: [list]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/11/14
	**/
	int insertRegion(List<RegionEntity> list);
}
