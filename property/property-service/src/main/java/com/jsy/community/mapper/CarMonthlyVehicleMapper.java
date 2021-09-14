package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.property.CarMonthlyVehicle;
import com.jsy.community.qo.CarMonthlyVehicleQO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CarMonthlyVehicleMapper extends BaseMapper<CarMonthlyVehicle> {

    IPage<CarMonthlyVehicle> FindByMultiConditionPage(@Param("page") Page page, @Param("query") CarMonthlyVehicleQO carMonthlyVehicleQO);

    IPage<CarMonthlyVehicle> FindByMultiConditionPage2Position(@Param("page")Page<CarMonthlyVehicle> page, @Param("query") CarMonthlyVehicleQO carMonthlyVehicleQO);

    /**
     * 包月车辆
     * @param carMonthlyVehicleQO
     * @return
     */
    List<CarMonthlyVehicle> selectListQueryCar(@Param("query") CarMonthlyVehicleQO carMonthlyVehicleQO);

    /**
     * 包月车位
     * @param carMonthlyVehicleQO
     * @return
     */
    List<CarMonthlyVehicle> selectListQueryPostion(@Param("query") CarMonthlyVehicleQO carMonthlyVehicleQO);
}
