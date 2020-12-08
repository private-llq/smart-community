package com.jsy.community.mapper;


import java.util.List;
import java.util.Map;

/**
 * 公共 Mapper 接口
 * @author YuLF
 * @since 2020-11-10
 */
public interface CommonMapper {

    List<Map> getAllCommunityFormCityId(Integer id);

    List<Map> getBuildingOrUnitByCommunityId(Integer id, Integer houseLevelMode);

    List<Map> getBuildingOrUnitOrFloorById(Integer id, Integer houseLevelMod);

    List<Map> getAllDoorFormFloor(Integer id);
}
