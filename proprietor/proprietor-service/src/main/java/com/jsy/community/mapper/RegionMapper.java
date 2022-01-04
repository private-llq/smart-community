package com.jsy.community.mapper;

import com.jsy.community.entity.RegionEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 省市区表Mapper
 * @author chq459799974
 * @since 2020-11-13
 */
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
	
	/**
	* @Description: 城市/区域模糊查询
	 * @Param: [searchStr]
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	**/
	List<RegionEntity> vagueQueryRegion(@Param("searchStr")String searchStr, @Param("lv")Integer lv,@Param("cityId")Integer cityId);
	
	/**
	 * @return java.util.List<java.lang.Long>
	 * @Author lihao
	 * @Description 根据城市id 获取其下 缴费类型id集合
	 * @Date 2020/12/11 16:19
	 * @Param [id]
	 **/
	List<Long> getListPayTypeId(Long id);
	
	/**
	* @Description: 查找名称相似城市(如重庆、重庆市)
	 * @Param: [cityName]
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2021/6/8
	**/
	@Select("select name,lng,lat from t_region where level = 2 and name like CONCAT(#{cityName},'_')")
	List<RegionEntity> getCityNameLike(String cityName);

	/**
	 * @author: Pipi
	 * @description: 通过区域名称模糊匹配区域
	 * @param regionName: 区域名称
	 * @return: {@link RegionEntity}
	 * @date: 2022/1/4 15:21
	 **/
	RegionEntity queryRegionByName(@Param("regionName") String regionName);
}
