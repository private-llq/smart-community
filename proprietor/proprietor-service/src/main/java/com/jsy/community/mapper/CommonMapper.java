package com.jsy.community.mapper;


import com.jsy.community.entity.RegionEntity;

import java.util.List;
import java.util.Map;

/**
 * 公共 Mapper 接口
 * @author YuLF
 * @since 2020-11-10
 */
public interface CommonMapper {

    List<Map<String, Object>> getAllCommunityFormCityId(Integer id, Integer page, Integer pageSize);

    List<Map<String, Object>> getBuildingOrUnitByCommunityId(Integer id, Integer houseLevelMode);

    List<Map<String, Object>> getBuildingOrUnitById(Integer id, Integer houseLevelMode);

    List<Map<String, Object>> getAllDoorFormFloor(Integer id);

    List<Map<String, Object>> getFloorByBuildingOrUnitId(Integer id, Integer houseLevelMode);
}
