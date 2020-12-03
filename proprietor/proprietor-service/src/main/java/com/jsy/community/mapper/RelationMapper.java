package com.jsy.community.mapper;

import com.jsy.community.vo.RelationCarsVO;

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
    void addCars(List<RelationCarsVO> cars);

}
