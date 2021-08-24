package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.jsy.community.entity.property.CarEquipmentManageEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarEquipmentManageQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarEquipmentManageMapper extends BaseMapper<CarEquipmentManageEntity>
{
  List<CarEquipmentManageEntity> equipmentPage(@Param("page") Long page, @Param("size") Long size, @Param("query") CarEquipmentManageEntity query);

  Long findTotal(@Param("query") CarEquipmentManageEntity query);

}
