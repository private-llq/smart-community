package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.entity.proprietor.CarMonthlyVehicle;
import com.jsy.community.qo.CarMonthlyVehicleQO;
import com.jsy.community.utils.PageInfo;
import org.apache.ibatis.annotations.Param;


public interface CarMonthlyVehicleMapper extends BaseMapper<CarMonthlyVehicle> {

    IPage FindByMultiConditionPage(@Param("page") Page page, @Param("query") CarMonthlyVehicleQO carMonthlyVehicleQO);

}
