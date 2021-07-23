package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PatrolEquipEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @author chq459799974
 * @description 物业巡检设备Mapper
 * @since 2021-07-23 15:36
 **/
public interface PatrolEquipMapper extends BaseMapper<PatrolEquipEntity> {
	
	/**
	* @Description: 修改巡检设备
	 * @Param: [entity]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	int updateEquip(@Param("entity")PatrolEquipEntity entity);
	
}
