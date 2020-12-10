package com.jsy.community.mapper;

import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.qo.RelationCarsQo;

import java.util.List;

/**
 * <p>
 * 家属 Mapper 接口
 * </p>
 *
 * @author chq459799974
 * @since 2020-12-3
 */
public interface RelationMapper {
    /**
     * 添加家属车辆
     * @param cars
     */
    void addCars(List<RelationCarsQo> cars);

    List<HouseMemberEntity> selectID(String id);
}
