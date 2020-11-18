package com.jsy.community.mapper;


import java.util.List;
import java.util.Map;

/**
 * 公共 Mapper 接口
 * @author YuLF
 * @since 2020-11-10
 */
public interface CommonMapper {

    List<Map> getAllCommunity(Integer id);

    List<Map> getAllUnitFormCommunity(Integer id);

    List<Map> getAllBuildingFormUnit(Integer id);

    List<Map> getAllFloorFormBuilding(Integer id);

    List<Map> getAllDoorFormFloor(Integer id);
}
