package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseBuildingTypeEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  HouseBuildingType 接口
 * </p>
 *
 * @author DKS
 * @since 2021-08-05
 */
public interface HouseBuildingTypeMapper extends BaseMapper<HouseBuildingTypeEntity> {
	
	/**
	 * @Description: 批量查询楼宇分类名称
	 * @Param: [ids]
	 * @Return: java.util.Map<java.lang.Integer,java.util.Map<java.lang.String,java.lang.String>>
	 * @Author: DKS
	 * @Date: 2021/8/9
	 **/
	@MapKey("id")
	Map<Integer, Map<String,String>> queryHouseBuildingType(@Param("list") List<Integer> ids);
	
}
