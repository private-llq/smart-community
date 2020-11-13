package com.jsy.community.mapper;

import java.util.List;

import com.jsy.community.entity.RegionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
	@Select("SELECT id,name,sname,ssname,pid,level,pinyin FROM region")
	List<RegionEntity> getAllRegion();
	
	/**
	* @Description: 获取子区域
	 * @Param: [id]
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/13
	**/
	@Select("SELECT id,name,sname,ssname,pid,level,pinyou FROM region where pid = #{id}")
	List<RegionEntity> getSubRegion(@Param("id") String id);
}
