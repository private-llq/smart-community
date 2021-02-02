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

    List<Map<String, Object>> getAllCommunityFormCityId(Long id, Integer page, Integer pageSize);

    List<Map<String, Object>> getBuildingOrUnitByCommunityId(Long id, Integer houseLevelMode);

    List<Map<String, Object>> getBuildingOrUnitById(Long id, Integer houseLevelMode);

    List<Map<String, Object>> getAllDoorFormFloor(Long id);

    List<Map<String, Object>> getFloorByBuildingOrUnitId(Long id, Integer houseLevelMode);
}
