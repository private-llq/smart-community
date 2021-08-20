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
  //List<CarEquipmentManageEntity> equipmentPage(@Param("page") Long page, @Param("size") Long size, @Param("carEquipmentManageQO") BaseQO query);

  //Long findTotal(@Param("carEquipmentManageQO") CarEquipmentManageQO carEquipmentManageQO);

  //分页查询
  //List<CarEquipmentManageEntity> pageList(@Param("page") Long page, @Param("size") Long size);

 // List<CarEquipmentManageEntity> equipmentPage(@Param("page") Long page, @Param("size") Long size, @Param("query") Object query);

  //Long findTotal(@Param("query") Object query);


  // List<CarEquipmentManageEntity> equipmentPage(@Param("page") Long page, @Param("size") Long size, @Param("equipmentName") String equipmentName);

 // Long findTotal(@Param("equipmentName") String equipmentName);



  //分页查询
 // List<CarEquipmentManageEntity> equipmentPage(@Param("page") Long page, @Param("size") Long size, @Param("query") String query);

  //查询总条数
//  Long findTotal(@Param("query") CarEquipmentManageEntity query);
}
