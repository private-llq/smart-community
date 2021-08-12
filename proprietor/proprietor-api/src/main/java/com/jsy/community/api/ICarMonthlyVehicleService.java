package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.proprietor.CarLaneEntity;
import com.jsy.community.entity.proprietor.CarMonthlyVehicle;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CarMonthlyVehicleQO;
import com.jsy.community.utils.PageInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Component
public interface ICarMonthlyVehicleService extends IService<CarMonthlyVehicle> {

    Integer SaveMonthlyVehicle(CarMonthlyVehicle carMonthlyVehicle);

    Integer UpdateMonthlyVehicle(CarMonthlyVehicle carMonthlyVehicle);

    Integer DelMonthlyVehicle(String uid);

    PageInfo FindByMultiConditionPage(CarMonthlyVehicleQO carMonthlyVehicleQO);

    /**
     * 延期 0 按天  1 按月
     */
    void delay(String uid, Integer type, Integer dayNum, BigDecimal fee);


    void monthlyChange(String uid, Integer type);

    Map<String, Object> addLinkByExcel(List<String[]> strings);

    List<CarMonthlyVehicle> selectList();

}