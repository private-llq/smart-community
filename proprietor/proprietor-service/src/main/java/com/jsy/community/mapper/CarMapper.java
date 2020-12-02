package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CarEntity;

import java.util.List;

/**
 * 车辆 Mapper 接口
 * @author YuLF
 * @since 2020-11-10
 */
public interface CarMapper extends BaseMapper<CarEntity> {


    /**
     * 列表添加车辆信息方式
     * @param carEntityList 业主车辆信息 列表
     * @return              返回影响行数
     */
    Integer addProprietorCar(List<CarEntity> carEntityList);
}
