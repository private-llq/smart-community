package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.jsy.community.entity.property.CarEquipmentManageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarEquipmentManageMapper extends BaseMapper<CarEquipmentManageEntity>
{
  //分页查询
  List<CarEquipmentManageEntity> pageList(@Param("page") Long page, @Param("size") Long size);

  //分页查询
  List<CarEquipmentManageEntity> equipmentPage(@Param("page") Long page, @Param("size") Long size, @Param("query") CarEquipmentManageEntity query);

  //查询总条数
  Long findTotal(@Param("query") CarEquipmentManageEntity query);
}
