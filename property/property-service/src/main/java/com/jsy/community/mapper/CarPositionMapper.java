package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.CarPositionEntity;

import java.util.List;

public interface CarPositionMapper extends BaseMapper<CarPositionEntity> {

    <T> void seavefile(List<T> list);

    List<CarPositionEntity> selectCarPosition(CarPositionEntity qo);
}
